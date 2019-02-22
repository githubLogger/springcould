package com.log.sericefeign;

import org.springframework.stereotype.Component;

@Component  //加入到容器中
public class SchedualServiceHiHystric implements SchedualServiceHi {
    /**
     * 当 service-hi 工程不可用的时候，service-feign调用 service-hi的API接口时，会执行快速失败，
     * 直接返回一组字符串，而不是等待响应超时，这很好的控制了容器的线程阻塞。
     */
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }
}
