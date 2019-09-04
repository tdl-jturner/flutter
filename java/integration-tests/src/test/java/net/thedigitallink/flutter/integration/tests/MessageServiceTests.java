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
        ResponseEntity<Void> entity = restTemplate.postForEntity(getUri("message-service","/create"),new HttpEntity<>(message,httpHeaders),Void.class);
        assert(entity.getStatusCode().is2xxSuccessful());
    }

}