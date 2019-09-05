package net.thedigitallink.flutter.service.follow;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Follow;
import net.thedigitallink.flutter.service.models.FollowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
public class FollowServiceController {

    @Autowired
    private EurekaClient eurekaClient;

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public FollowServiceController() {
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
    public ResponseEntity<Void> createFollow(@RequestBody Follow request) {
        log.trace("POST | /create | {}",request.toString());
        try {
            ResponseEntity<FollowResponse> entity = restTemplate.postForEntity(getUri("follow-dao","/save"),new HttpEntity<>(request.toRequestString(),httpHeaders), FollowResponse.class);
            return new ResponseEntity<>(entity.getStatusCode());
        }
        catch (Exception e) {
            log.trace("ERROR",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteFollow(@RequestBody Follow request) {
        log.trace("POST | /delete | {}",request.toString());
        try {
            ResponseEntity<FollowResponse> entity = restTemplate.postForEntity(getUri("follow-dao","/delete"),new HttpEntity<>(request.toRequestString(),httpHeaders), FollowResponse.class);
            return new ResponseEntity<>(entity.getStatusCode());
        }
        catch (Exception e) {
            log.trace("ERROR",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get/{username}", method=RequestMethod.GET)
    public ResponseEntity<List<Follow>> getFollow(@PathVariable String username) {
        log.trace("GET | /get/{}",username);
        try {
            ResponseEntity<FollowResponse> entity = restTemplate.postForEntity(getUri("follow-dao","/getAll"),new HttpEntity<>(Follow.builder().follower(username).build().toRequestString(),httpHeaders), FollowResponse.class);
            return new ResponseEntity<>(entity.getBody().getPayload(),entity.getStatusCode());
        }
        catch (Exception e) {
            log.trace("ERROR",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/exists", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> exists(@RequestBody Follow request) {
        log.trace("POST | /exists | {}",request.toString());
        try {
            ResponseEntity<FollowResponse> entity = restTemplate.postForEntity(getUri("follow-dao","/get"),new HttpEntity<>(request.toRequestString(),httpHeaders), FollowResponse.class);
            if(entity.getStatusCode()==HttpStatus.OK && entity.getBody().getPayload()!= null && entity.getBody().getPayload().size()>0) {
                return new ResponseEntity<>(true,entity.getStatusCode());
            }
            else {
                return new ResponseEntity<>(false,entity.getStatusCode());
            }
        }
        catch (Exception e) {
            log.trace("ERROR",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
