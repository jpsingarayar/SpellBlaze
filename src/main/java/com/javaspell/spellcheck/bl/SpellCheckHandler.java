package com.javaspell.spellcheck.bl;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.javaspell.spellcheck.api.request.SpellCheckRequest;
import com.javaspell.spellcheck.api.response.ServiceResponse;
import com.javaspell.spellcheck.bl.spellservice.SpellCheckService;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SpellCheckHandler {
	
@Inject
SpellCheckService spellCheckService;

@Timed
@ExceptionMetered
@HystrixCommand(groupKey = "hystrixGroup", commandKey = "helloCommandKey", threadPoolKey = "helloThreadPoolKey", fallbackMethod = "fallbackHello")
@Cacheable(cacheNames = "default", key = "T(com.shc.services.sc.util.MiscUtil).getCacheKey(#request)", condition = "T(com.shc.services.sc.util.MiscUtil).isCacheOverride(#request) == false", unless = "#result == null")
public ServiceResponse spellCorrect(SpellCheckRequest request) {
    System.out.println("Inside getHello");
    return spellCheckService.getCorrectedWord(request);
}


public ServiceResponse fallbackHello(SpellCheckRequest request) {
    return new ServiceResponse() ;
}

}
