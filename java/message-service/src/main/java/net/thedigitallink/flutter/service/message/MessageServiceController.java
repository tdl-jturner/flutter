package net.thedigitallink.flutter.service.message;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Message;
import net.thedigitallink.flutter.service.models.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@RestController
@Slf4j
public class MessageServiceController {

    @Autowired
    private EurekaClient eurekaClient;

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public MessageServiceController() {
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
    public ResponseEntity<Void> createMessage(@RequestBody Message request) {
        log.trace("POST | /create | {}",request.toString());
        try {
            ResponseEntity<MessageResponse> entity = restTemplate.postForEntity(getUri("message-dao","/save"),new HttpEntity<>(request.toRequestString(),httpHeaders), MessageResponse.class);
            return new ResponseEntity<>(entity.getStatusCode());
        }
        catch (Exception e) {
            log.trace("ERROR",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get/{id}", method=RequestMethod.GET)
    public ResponseEntity<Message> getMessage(@PathVariable String id) {
        log.trace("GET | /get/{}",id);
        try {
            ResponseEntity<MessageResponse> entity = restTemplate.postForEntity(getUri("message-dao","/get"),new HttpEntity<>(Message.builder().id(UUID.fromString(id)).build().toRequestString(),httpHeaders), MessageResponse.class);
            return new ResponseEntity<>(entity.getBody().getPayload().get(0),entity.getStatusCode());
        }
        catch (Exception e) {
            log.trace("ERROR",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
