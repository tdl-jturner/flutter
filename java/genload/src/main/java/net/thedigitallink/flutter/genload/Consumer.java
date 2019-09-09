package net.thedigitallink.flutter.genload;


import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Consumer extends Thread{

    RestTemplate restTemplate;

    public static RateLimiter rateLimiter;

    private HttpHeaders httpHeaders;

    private Boolean keepRunning = true;

    public void close() {
        keepRunning=false;
    }

    public Consumer(GenLoadConfig genLoadConfig) {
        if(rateLimiter==null) {
            rateLimiter=RateLimiter.create(genLoadConfig.getRate());
            log.info("Initialized Rate Limiter: {}",genLoadConfig.getRate());
        }
        restTemplate= new RestTemplate();

        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    public void run() {
        log.info("Started {}({})",this.getClass().getSimpleName(), Thread.currentThread().getId() );
        while(keepRunning) {
            rateLimiter.acquire();
            Action action;
            try {
                action = Application.actionQueue.poll();
            }
            catch(Exception e ){
                log.error("Error getting action: {}",e.getMessage());
                continue;
            }
            consume(action);
        }
        log.info("Stopped {}({})",this.getClass().getSimpleName(), Thread.currentThread().getId() );
    }

    @Async
    public CompletableFuture<Void> consume(Action action ) {
        if(action!=null) {
            log.debug("Performing action {}", action);
            try {
                if (action.getPayload() == null) {
                    restTemplate.exchange(action.getUri(), HttpMethod.GET, null, new ParameterizedTypeReference<String>() {});
                } else {
                    restTemplate.exchange(action.getUri(), HttpMethod.POST, new HttpEntity<>(action.getPayload(), httpHeaders), new ParameterizedTypeReference<String>() {});
                }
            } catch (Exception e) {
                //log.error("Failed Action: {} returned {}",action.getUri(), e.getMessage());
            }
        }
        return CompletableFuture.completedFuture(null);

    }

}
