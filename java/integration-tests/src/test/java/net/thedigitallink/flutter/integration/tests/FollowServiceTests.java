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
public class FollowServiceTests {

    @Getter @Setter @NoArgsConstructor
    static class Follow {
        private UUID follower;
        private UUID author;
    }

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private ObjectMapper objectMapper;

    public FollowServiceTests() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        objectMapper = new ObjectMapper();
    }

    private HttpEntity<String> createEntity(Follow follow) {
        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(follow),httpHeaders);
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
        Follow follow = podamFactory.manufacturePojoWithFullData(Follow.class);
        restTemplate.postForEntity(getUri("follow-service","/create"),createEntity(follow),Void.class);
        return follow;
    }

    @Test
    public void testGet() {
        Follow follow = random();
        ResponseEntity<Follow> entity = restTemplate.getForEntity(getUri("follow-service","/get")+"/"+follow.getFollower().toString(),Follow.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(follow.getAuthor(),entity.getBody().getAuthor());
    }

    @Test
    public void testCreate() {
        Follow follow = random();
        ResponseEntity<Void> entity = restTemplate.postForEntity(getUri("follow-service","/create"),createEntity(follow),Void.class);
        assert(entity.getStatusCode().is2xxSuccessful());
    }

}