package net.thedigitallink.flutter.integration.tests;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MessageServiceTests {

    @Getter @Setter @NoArgsConstructor
    static class Message {
        private UUID id;
        private UUID author;
        private String message;
        private Long createdDttm;
    }

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private ObjectMapper objectMapper;

    public MessageServiceTests() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        objectMapper = new ObjectMapper();
    }

    private HttpEntity<String> createEntity(Message message) {
        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(message),httpHeaders);
        } catch (JsonProcessingException e) {
            log.error("Unable to process JSON",e);
            return null;
        }
    }

    private URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private Message random() {
        Message message = podamFactory.manufacturePojoWithFullData(Message.class);
        restTemplate.postForEntity(getUri("message-service","/create"),createEntity(message),Void.class);
        return message;
    }

    @Test
    public void testGet() {
        Message message = random();
        ResponseEntity<Message> entity = restTemplate.getForEntity(getUri("message-service","/get")+"/"+message.getId().toString(),Message.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(entity.getBody().getId(),message.getId());
    }

    @Test
    public void testCreate() {
        Message message = random();
        ResponseEntity<Void> entity = restTemplate.postForEntity(getUri("message-service","/create"),createEntity(message),Void.class);
        assert(entity.getStatusCode().is2xxSuccessful());
    }

}