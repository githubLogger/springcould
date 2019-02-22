eureka-server:
    服务注册中心，服务提供者在启动的时候向注册中心注册自己提供的服务，这样服务消费者就可以通过服务注册中心查询所需服务的url
service-hi：
    服务提供者，向注册中心注册自己提供的服务，同一个服务可以由多个服务提供者提供，用于负载均衡
Service-ribbon:
    Ribbon是用来做负载均衡的，通过restTemplate.getForObject(“http://SERVICE-HI/hi?name=”+name,String.class)方法
    实现轮流调用SERVICE-HI服务的提供者。SERVICE-HI是服务提供者向注册中心注册时的服务名
Feign：
    Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。
    它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，
    并和Eureka结合，默认实现了负载均衡的效果。
Hystrix：
    断路器当较底层的服务如果出现故障，会导致连锁故障。当对特定的服务的调用的不可用达到一个阀值（Hystric 是5秒20次） 断路器将会被打开。
    在service-feign中添加断路器：
        ①是自带断路器的，所以改造Feign项目不需要添加依赖。
        ②在配置文件中使用feign.hystrix.enabled=true打开熔断器，
        ③在需要调用服务的接口（SchedualServiceHi）的@FeignClient注解中加上fallback的指定类就行了，例：@FeignClient(value = "service-hi",fallback = SchedualServiceHiHystric.class)
        ④创建指定类SchedualServiceHiHystric，指定类需要实现SchedualServiceHi接口
    在serice-ribbon中添加断路器：
        ①添加spring-cloud-starter-netflix-hystrix依赖。
        ②在程序的启动类ServiceRibbonApplication 加@EnableHystrix注解开启Hystrix：
        ③在调用服务的方法上（hiService）加上 @HystrixCommand(fallbackMethod = "hiError")
        ④编写调用服务失败时的方法hiError
zuul:
    Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。
    zuul默认和Ribbon结合实现了负载均衡的功能。
    路由功能：
        ①创建service-zuul工程
        ②添加依赖，并将自己注册到注册中心
        ③在配置文件中指定转发规则
            zuul:
              routes:
                api-a:
                  path: /api-a/**
                  serviceId: service-ribbon
                api-b:
                  path: /api-b/**
                  serviceId: service-feign
        ④打来http://localhost:8769/api-a/hi?name=forezp进行测试路由功能
        ⑤编写MyFilter 集成ZuulFilter类，用来实现过滤，做一些安全验证
Config:
    在spring cloud config 组件中，分两个角色，一是config server，二是config client。
    config-client从config-server获取了foo的属性，而config-server是从git仓库读取的
    config-server:（在需要时可以注册到eureka中实现集群）
        ①导入spring-cloud-config-server依赖
        ②在程序的入口Application类加上@EnableConfigServer注解开启配置服务器的功能
        ③修改配置文件（github上要事先上传‘application-profile.properties’）
            spring.application.name=config-server
            server.port=8888

            spring.cloud.config.server.git.uri=https://github.com/githubLogger/SpringcloudConfig/   //放配置文件的地址
            spring.cloud.config.server.git.searchPaths=respo    //说明在哪个文件夹下
            spring.cloud.config.label=master
            spring.cloud.config.server.git.username=    //不是私有仓库不需要用户名密码
            spring.cloud.config.server.git.password=
    config-client：
        ①添加spring-cloud-starter-config依赖
        ②修改配置文件
            spring.application.name=config-client   //会请求对应的配置文件
            spring.cloud.config.label=master        //指明远程仓库的分支
            spring.cloud.config.profile=dev         //dev开发环境、test测试环境、pro正式环境。会自动请求对应环境的配置文件
            spring.cloud.config.uri= http://localhost:8888/     //指明配置服务中心的网址。
            server.port=8881
Bus:
    Spring Cloud Bus 将分布式的节点用轻量的消息代理连接起来。它可以用于广播配置文件的更改或者服务之间的通讯，也可以用于监控。
    改造config-client，实现通知配置文件的更改：
        ①加上起步依赖spring-cloud-starter-bus-amqp（电脑需要装RabbitMq）
        ②application.properties中加上RabbitMq的配置
            spring.rabbitmq.host=localhost
            spring.rabbitmq.port=5672
            spring.rabbitmq.username=guest
            spring.rabbitmq.password=guest

            spring.cloud.bus.enabled=true
            spring.cloud.bus.trace.enabled=true
            management.endpoints.web.exposure.include=bus-refresh
        ③在启动类上加@RefreshScope接口
        ④依次启动eureka-server、confg-cserver,启动两个config-client，端口为：8881、8882。
        ⑤http://localhost:8881/hi 或者http://localhost:8882/hi 获取当前配置内容
        ⑥更新github配置文件，并发送post请求到http://localhost:8881/actuator/bus-refresh通知刷新配置 （如果status是415，请求头加上Content-Type:application/json ）
        注：/actuator/bus-refresh接口可以指定服务，比如 “/actuator/bus-refresh?destination=customers:**” 即刷新服务名为customers的所有服务。
zipkin：
    Spring Cloud Sleuth 主要功能就是在分布式系统中提供追踪解决方案，并且兼容支持了 zipkin，你只需要在pom文件中引入相应的依赖即可。
    ZipkinServer：收集调用数据，并展示；在spring Cloud为F版本的时候，已经不需要自己构建Zipkin Server了，只需要下载jar即可，下载地址：https://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/
    调用端：（service-hi，service-miya）
        ①引入起步依赖spring-cloud-starter-zipkin
        ②在其配置文件中指定zipkin server的地址。spring.zipkin.base-url=http://localhost:9411
        ③对外暴露接口，并相互调用
        ④访问http://localhost:9411/，点击依赖分析/Dependencies查看依赖