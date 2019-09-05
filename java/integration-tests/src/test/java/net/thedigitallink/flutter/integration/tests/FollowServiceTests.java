package net.thedigitallink.flutter.integration.tests;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Follow;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FollowServiceTests {

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public FollowServiceTests() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    private URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private Follow random() {
        Follow follow = podamFactory.manufacturePojoWithFullData(Follow.class);
        restTemplate.postForEntity(getUri("follow-service","/create"),new HttpEntity<>(follow,httpHeaders),Void.class);
        return follow;
    }

    @Test
    public void testGet() {
        Follow follow = random();
        ResponseEntity<List<Follow>> entity = restTemplate.exchange( getUri("follow-service", String.format("/get/%s",follow.getFollower())), HttpMethod.GET,null, new ParameterizedTypeReference<List<Follow>>(){});
        assert(entity.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(follow.getAuthor(),entity.getBody().get(0).getAuthor());
    }

    @Test
    public void testCreate() {
        Follow follow = random();
        ResponseEntity<Void> entity = restTemplate.postForEntity(getUri("follow-service","/create"),new HttpEntity<>(follow,httpHeaders),Void.class);
        assert(entity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testExists() {
        Follow follow = random();
        ResponseEntity<Boolean> entity = restTemplate.postForEntity(getUri("follow-service","/exists"),new HttpEntity<>(follow,httpHeaders),Boolean.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertTrue(entity.getBody());
    }

    @Test
    public void testNotExists() {
        Follow follow = podamFactory.manufacturePojoWithFullData(Follow.class);
        ResponseEntity<Boolean> entity = restTemplate.postForEntity(getUri("follow-service","/exists"),new HttpEntity<>(follow,httpHeaders),Boolean.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertFalse(entity.getBody());
    }

}