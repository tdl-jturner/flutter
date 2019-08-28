package net.thedigitallink.flutter.integration.tests;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserDaoTests {

    @Getter @Setter @NoArgsConstructor
    static class User {
        private UUID id;
        private String username;
        private String email;
        private Boolean enableNotifications;
        long createdDttm;
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
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private ObjectMapper objectMapper;

    public UserDaoTests() {
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

    private URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private User random() {
        User user = podamFactory.manufacturePojoWithFullData(User.class);
        restTemplate.postForEntity(getUri("user-dao","/save"),createEntity(user),Response.class);
        return user;
    }

    @Test
    public void testGet() {
        User user = random();
        ResponseEntity<Response> entity = restTemplate.postForEntity(getUri("user-dao","/get"),createEntity(user),Response.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(entity.getBody().getPayload().get(0).getId(),user.getId());
    }

    @Test
    public void testSave() {
        User user = random();
        ResponseEntity<Response> entity = restTemplate.postForEntity(getUri("user-dao","/save"),createEntity(user), Response.class);
        assert (entity.getStatusCode().is2xxSuccessful());
    }

}