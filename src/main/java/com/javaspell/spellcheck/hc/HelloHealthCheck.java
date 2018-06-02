package com.javaspell.spellcheck.hc;


import com.javaspell.spellcheck.api.request.SpellCheckRequest;
import com.javaspell.spellcheck.api.response.ServiceResponse;
import com.javaspell.spellcheck.bl.spellservice.SpellCheckService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class HelloHealthCheck implements HealthIndicator {

    @Inject
    private SpellCheckService spellCheckService;
    
    private List<SpellCheckRequest> scRequest;
    @PostConstruct
    private void initialize(){
    	scRequest = new ArrayList<>();
    	scRequest.add(new SpellCheckRequest("kenmoae"));
    	scRequest.add(new SpellCheckRequest("samsumg"));
    	scRequest.add(new SpellCheckRequest("logitach"));
    }

    @Override
    public Health health() {
    	long validRequestCount = scRequest.stream()
                .map(request -> spellCheckService.getCorrectedWord(request))
                .filter(this::isValid)
                .count();

        if(validRequestCount != scRequest.size())
            return Health.down().withDetail("Error Code", 1).build();

        return Health.up().build();
    }
    
    private boolean isValid(ServiceResponse response){

        String correctedWord = response.getCorrectedPhrase();
        long responseTime = response.getResponseTime();
        int editDistanceMax = response.getEditDistanceMax();

        return (correctedWord!=null && responseTime<10 && editDistanceMax<=2);
    }
}