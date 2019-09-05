package net.thedigitallink.flutter.service.timeline;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class TimelineServiceController {

    @Autowired
    private EurekaClient eurekaClient;

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public TimelineServiceController() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    private URI getUri(String service,String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    @RequestMapping(value = "/get/{username}", method=RequestMethod.GET)
    public ResponseEntity<List<Timeline>> getTimeline(@PathVariable String username) {
        log.trace("GET | /get/{}",username);
        try {
            refreshTimeline(username);

            ResponseEntity<TimelineResponse> entity =
                restTemplate.postForEntity(
                    getUri("timeline-dao", "/get"),
                    new HttpEntity<>(
                        Timeline.builder()
                                .user(username)
                                .build().toRequestString()
                        ,httpHeaders
                    ),
                    TimelineResponse.class
                );

            return new ResponseEntity<>(entity.getBody().getPayload(),entity.getStatusCode());
        }
        catch (Exception e) {
            log.trace("ERROR",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/refresh/{username}", method=RequestMethod.GET)
    public ResponseEntity<Void> refreshTimeline(@PathVariable String username) {
        log.trace("GET | /refresh/{}",username);
        try {

            List<Follow> followList = restTemplate.exchange( getUri("follow-service", String.format("/get/%s",username)), HttpMethod.GET,null, new ParameterizedTypeReference<List<Follow>>(){}).getBody();

            for(Follow follow : followList) {
                log.trace("Getting lastUpdate for {}",follow.getAuthor());

                Long lastUpdate = null;
                try {
                    lastUpdate = restTemplate.postForEntity(
                            getUri("timeline-dao", "/getOne"),
                            new HttpEntity<>(
                                    Timeline.builder()
                                            .user(username)
                                            .author(follow.getAuthor()
                                            ).build().toRequestString()
                                    , httpHeaders
                            ),
                            TimelineResponse.class
                    ).getBody().getPayload().get(0).getCreatedDttm();
                }
                catch(HttpClientErrorException ex) {
                        lastUpdate=System.currentTimeMillis()-(1*24*60*60*1000);
                }

                List<Message> messages =
                        restTemplate.postForEntity(
                            getUri("message-dao", String.format("/get?since=%s",lastUpdate)),
                                new HttpEntity<>(
                                    Message.builder()
                                        .author(follow.getAuthor()
                                    ).build().toRequestString()
                                    ,httpHeaders
                                ),
                            MessageResponse.class
                        ).getBody().getPayload();

                for(Message message : messages) {
                    restTemplate.postForEntity(
                        getUri("timeline-dao", "/save"),
                        new HttpEntity<>(
                            Timeline.builder()
                                .user(username)
                                .author(follow.getAuthor())
                                .message(message.getId())
                                .createdDttm(message.getCreatedDttm()
                                ).build().toRequestString()
                                ,httpHeaders
                            ),
                        TimelineResponse.class
                    );
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            log.trace("ERROR",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
