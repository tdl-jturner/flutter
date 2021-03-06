package net.thedigitallink.flutter.integration.tests;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Message;
import net.thedigitallink.flutter.service.models.MessageResponse;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MessageDaoTests {

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public MessageDaoTests() {
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
        restTemplate.postForEntity(getUri("message-dao","/save"),new HttpEntity<>(message.toRequestString(),httpHeaders), MessageResponse.class);
        return message;
    }

    @Test
    public void testGet() {
        Message message = random();
        ResponseEntity<MessageResponse> entity = restTemplate.postForEntity(getUri("message-dao","/get"),new HttpEntity<>(Message.builder().id(message.getId()).build().toRequestString(),httpHeaders),MessageResponse.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(message.getAuthor(),entity.getBody().getPayload().get(0).getAuthor());
    }

    @Test
    public void testGetAll() {
        Message message = random();
        ResponseEntity<MessageResponse> entity = restTemplate.postForEntity(getUri("message-dao","/getAll"),new HttpEntity<>(Message.builder().author(message.getAuthor()).build().toRequestString(),httpHeaders),MessageResponse.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(entity.getBody().getPayload().get(0).getMessage(),message.getMessage());
    }

    @Test
    public void testGetAllSince() {
        Message message = random();
        ResponseEntity<MessageResponse> entity = restTemplate.postForEntity(getUri("message-dao","/getAll?since="+message.getCreatedDttm()),new HttpEntity<>(Message.builder().author(message.getAuthor()).build().toRequestString(),httpHeaders),MessageResponse.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(entity.getBody().getPayload().get(0).getMessage(),message.getMessage());
    }

    @Test
    public void testSave() {
        Message message = random();
        ResponseEntity<MessageResponse> entity = restTemplate.postForEntity(getUri("message-dao","/save"),new HttpEntity<>(message.toRequestString(),httpHeaders), MessageResponse.class);
        assert (entity.getStatusCode().is2xxSuccessful());
    }

}