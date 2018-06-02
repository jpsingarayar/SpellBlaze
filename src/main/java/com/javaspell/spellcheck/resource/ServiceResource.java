package com.javaspell.spellcheck.resource;


import com.javaspell.spellcheck.api.request.SpellCheckRequest;
import com.javaspell.spellcheck.api.response.ServiceResponse;
import com.javaspell.spellcheck.bl.spellservice.SpellCheckService;
import com.javaspell.spellcheck.util.RequestUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@Produces(MediaType.APPLICATION_JSON)
@RefreshScope
public class ServiceResource {
	 private static final Logger LOGGER = Logger.getLogger(ServiceResource.class.getName());


    
    @Inject
    SpellCheckService spellCheckService;
    
    private final AtomicLong counter = new AtomicLong();


    String foo = "World";

    @RequestMapping("/spellcheck")
    public ServiceResponse sayHello(@RequestParam("q") String q,
            @RequestParam(value="debug", defaultValue = "false") String debug) {
    	
    	//SpellCheckService spellCheckService = spellCheck.getSpellCheckService();
    	long startTime = System.currentTimeMillis();
    	long requestId = counter.incrementAndGet();
    	SpellCheckRequest serviceRequest = RequestUtil.buildRequest(requestId, q, debug);
    	ServiceResponse response = spellCheckService.getCorrectedWord(serviceRequest);
    	long responseTime =  System.currentTimeMillis() - startTime;
		response.setId(requestId);
		response.setResponseTime(responseTime);
		if(!serviceRequest.isDebug())
            response.setProximity(null);
		return response;
        //return new Saying(1, helloBusinessLogic.getHello(q) + " config foo " + foo);
    }
}
