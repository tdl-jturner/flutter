package net.thedigitallink.flutter.service.user;

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
import java.util.*;

@RestController
@Slf4j
public class UserServiceController {

    @Getter @Setter
    @Builder
    @AllArgsConstructor @NoArgsConstructor
    static class User {
        private UUID id;
        private String username;
        private String email;
        private Boolean enableNotifications;
        long createdDttm;

        public User(String username, String email) {
            id=UUID.randomUUID();
            createdDttm=new Date().getTime();
        }
        public String toString() {
            try {
                return new ObjectMapper().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return "Unable to render JSON.";
            }
        }
        public static User fromJson(String json) throws IOException {
            return new ObjectMapper().readValue(json,User.class);
        }
    }

    @Getter @Setter @NoArgsConstructor
    static class Request {
        User payload;
        Request(User payload) {
            this.payload=payload;
        }
    }

    @Getter @Setter @NoArgsConstructor
    static class Response{
        List<User> payload = new ArrayList<>();
    }

    @Autowired
    private EurekaClient eurekaClient;

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private ObjectMapper objectMapper;

    public UserServiceController() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        objectMapper = new ObjectMapper();
    }

    private HttpEntity<String> createEntity(User user) {
        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(new Request(user)),httpHeaders);
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
    public ResponseEntity<Void> createUser(@RequestBody User request) {
        try {
            ResponseEntity<Response> entity = restTemplate.postForEntity(getUri("user-dao","/save"),createEntity(request), Response.class);
            return new ResponseEntity<>(entity.getStatusCode());
        }
            catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get/{id}", method=RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable String id) {
        try {
            ResponseEntity<Response> entity = restTemplate.postForEntity(getUri("user-dao","/get"),createEntity(User.builder().id(UUID.fromString(id)).build()), Response.class);
            return new ResponseEntity<>(entity.getBody().payload.get(0),entity.getStatusCode());
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
