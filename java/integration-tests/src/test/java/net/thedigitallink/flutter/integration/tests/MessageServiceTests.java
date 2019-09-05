package net.thedigitallink.flutter.integration.tests;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MessageServiceTests {

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public MessageServiceTests() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    private URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private Message random() {
        Message message = podamFactory.manufacturePojoWithFullData(Message.class);
        restTemplate.postForEntity(getUri("message-service","/create"),new HttpEntity<>(message,httpHeaders),Void.class);
        return message;
    }

    private Message random(String author) {
        Message message = podamFactory.manufacturePojoWithFullData(Message.class);
        message.setAuthor(author);
        restTemplate.postForEntity(getUri("message-service","/create"),new HttpEntity<>(message,httpHeaders),Void.class);
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
    public void testGetAll() {
        Message message = random();
        for(int i=0;i<5;i++) {
            random(message.getAuthor());
        }
        ResponseEntity<List<Message>>  entity = restTemplate.exchange( getUri("message-service", "/getAll/"+message.getAuthor()), HttpMethod.GET,null, new ParameterizedTypeReference<List<Message>>(){});
        assert(entity.getStatusCode().is2xxSuccessful());
        assert(entity.getBody().size()>=1);
    }

    @Test
    public void testCreate() {
        Message message = random();
        ResponseEntity<Void> entity = restTemplate.postForEntity(getUri("message-service","/create"),new HttpEntity<>(message,httpHeaders),Void.class);
        assert(entity.getStatusCode().is2xxSuccessful());
    }

}