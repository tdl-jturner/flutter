package net.thedigitallink.flutter.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class UserServiceController {

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
    private DiscoveryClient discoveryClient;

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
        List<ServiceInstance> instanceList = discoveryClient.getInstances(service.toUpperCase());
        ServiceInstance serviceInstance = instanceList.get((int)(instanceList.size()-1 * Math.random()));
        return URI.create(serviceInstance.getUri()+"/"+service.toLowerCase()+api);
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
