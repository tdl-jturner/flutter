package net.thedigitallink.flutter.service.follow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class FollowServiceController {

    @Getter @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Follow {
        private UUID follower;
        private UUID author;

        public String toString() {
            try {
                return new ObjectMapper().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return "Unable to render JSON.";
            }
        }
        public static Follow fromJson(String json) throws IOException {
            return new ObjectMapper().readValue(json,Follow.class);
        }
    }

    @Getter @Setter @NoArgsConstructor
    static class Request {
        Follow payload;
        Request(Follow payload) {
            this.payload=payload;
        }
    }

    @Getter @Setter @NoArgsConstructor
    static class Response{
        List<Follow> payload = new ArrayList<>();
    }

    @Autowired
    private EurekaClient eurekaClient;

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private ObjectMapper objectMapper;

    public FollowServiceController() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        objectMapper = new ObjectMapper();
    }

    private HttpEntity<String> createEntity(Follow follow) {
        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(new Request(follow)),httpHeaders);
        } catch (JsonProcessingException e) {
            log.error("Unable to process JSON",e);
            return null;
        }
    }

    private URI getUri(String service,String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    @RequestMapping(value = "/create", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createFollow(@RequestBody Follow request) {
        try {
            ResponseEntity<Response> entity = restTemplate.postForEntity(getUri("follow-dao","/save"),createEntity(request), Response.class);
            return new ResponseEntity<>(entity.getStatusCode());
        }
            catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get/{id}", method=RequestMethod.GET)
    public ResponseEntity<Follow> getFollow(@PathVariable String id) {
        try {
            ResponseEntity<Response> entity = restTemplate.postForEntity(getUri("follow-dao","/get"),createEntity(Follow.builder().follower(UUID.fromString(id)).build()), Response.class);
            return new ResponseEntity<>(entity.getBody().payload.get(0),entity.getStatusCode());
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
