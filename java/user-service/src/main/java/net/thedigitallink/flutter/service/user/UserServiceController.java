package net.thedigitallink.flutter.service.user;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.User;
import net.thedigitallink.flutter.service.models.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class UserServiceController {

    @Autowired
    private EurekaClient eurekaClient;

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public UserServiceController() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    private URI getUri(String service,String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    @RequestMapping(value = "/create", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createUser(@RequestBody User request) {
        log.trace("POST | /create | {}",request.toString());
        try {
            log.info("Received /create : {}",request.toString());
            ResponseEntity<UserResponse> entity = restTemplate.postForEntity(getUri("user-dao","/save"),new HttpEntity<>(request.toRequestString(),httpHeaders), UserResponse.class);
            return new ResponseEntity<>(entity.getStatusCode());
        }
        catch (Exception e) {
            log.error("Exception Found",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get/{username}", method=RequestMethod.GET)
    public ResponseEntity<User> getUser(@PathVariable String username) {
        log.trace("GET | /get/{}",username);
        try {
            ResponseEntity<UserResponse> entity = restTemplate.postForEntity(getUri("user-dao","/get"),new HttpEntity<>(User.builder().username(username).build().toRequestString(),httpHeaders), UserResponse.class);
            return new ResponseEntity<>((User) entity.getBody().getPayload().get(0),entity.getStatusCode());
        }
        catch (Exception e) {
            log.error("Exception Found",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/getAll", method=RequestMethod.GET)
    public ResponseEntity<List<User>> getAllUsers() {
        log.trace("GET | /getAll");
        try {
            ResponseEntity<UserResponse> entity = restTemplate.postForEntity(getUri("user-dao","/getAll"),new HttpEntity<>((new User()).toRequestString(),httpHeaders), UserResponse.class);
            return new ResponseEntity<>(entity.getBody().getPayload(),entity.getStatusCode());
        }
        catch (Exception e) {
            log.error("Exception Found",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
