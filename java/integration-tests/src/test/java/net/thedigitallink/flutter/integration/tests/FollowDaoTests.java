package net.thedigitallink.flutter.integration.tests;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.*;
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
public class FollowDaoTests {

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    static class Follow {
        private UUID follower;
        private UUID author;
    }

    @Getter @Setter @NoArgsConstructor
    static class Request {
        private Follow payload;
        Request(Follow payload) {
            this.payload=payload;
        }
    }

    @Getter @Setter @NoArgsConstructor
    static class Response{
        private List<Follow> payload = new ArrayList<>();
    }

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private ObjectMapper objectMapper;

    public FollowDaoTests() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        objectMapper = new ObjectMapper();
    }

    private HttpEntity<String> createEntity(Follow follow) {
        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(new Request(follow)),httpHeaders);
        } catch (JsonProcessingException e) {
            log.error("Unable to process JSON",e);
            return null;
        }
    }

    private URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private Follow random() {
        Follow user = podamFactory.manufacturePojoWithFullData(Follow.class);
        restTemplate.postForEntity(getUri("follow-dao","/save"),createEntity(user),Response.class);
        return user;
    }

    @Test
    public void testGet() throws Exception {
        Follow follow = random();
        ResponseEntity<Response> entity = restTemplate.postForEntity(getUri("follow-dao","/get"),createEntity(Follow.builder().follower(follow.getFollower()).build()),Response.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(follow.getAuthor(),entity.getBody().getPayload().get(0).getAuthor());
    }

    @Test
    public void testSave() throws Exception {
        Follow follow = random();
        ResponseEntity<Response> entity = restTemplate.postForEntity(getUri("follow-dao","/save"),createEntity(follow), Response.class);
        assert (entity.getStatusCode().is2xxSuccessful());
    }

}