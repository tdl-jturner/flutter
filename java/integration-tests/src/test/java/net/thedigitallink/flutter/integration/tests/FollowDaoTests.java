package net.thedigitallink.flutter.integration.tests;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Follow;
import net.thedigitallink.flutter.service.models.FollowResponse;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FollowDaoTests {

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public FollowDaoTests() {
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
        restTemplate.postForEntity(getUri("follow-dao","/save"),new HttpEntity<>(follow.toRequestString(),httpHeaders), FollowResponse.class);
        return follow;
    }

    @Test
    public void testGet() {
        Follow follow = random();
        ResponseEntity<FollowResponse> entity = restTemplate.postForEntity(getUri("follow-dao","/get"),new HttpEntity<>(Follow.builder().follower(follow.getFollower()).build().toRequestString(),httpHeaders),FollowResponse.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(follow.getAuthor(),entity.getBody().getPayload().get(0).getAuthor());
    }

    @Test
    public void testSave() {
        Follow follow = random();
        ResponseEntity<FollowResponse> entity = restTemplate.postForEntity(getUri("follow-dao","/save"),new HttpEntity<>(follow.toRequestString(),httpHeaders), FollowResponse.class);
        assert (entity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testDelete() {
        Follow follow = random();
        ResponseEntity<FollowResponse> checkEntityPre = restTemplate.postForEntity(getUri("follow-dao","/get"),new HttpEntity<>(follow.toRequestString(),httpHeaders), FollowResponse.class);
        assertNotNull(checkEntityPre.getBody().getPayload());

        ResponseEntity<FollowResponse> deleteEntity = restTemplate.postForEntity(getUri("follow-dao","/delete"),new HttpEntity<>(follow.toRequestString(),httpHeaders), FollowResponse.class);
        assert (deleteEntity.getStatusCode().is2xxSuccessful());

        ResponseEntity<FollowResponse> checkEntityPost = restTemplate.postForEntity(getUri("follow-dao","/get"),new HttpEntity<>(follow.toRequestString(),httpHeaders), FollowResponse.class);
        assertNull(checkEntityPost.getBody().getPayload());
    }

}