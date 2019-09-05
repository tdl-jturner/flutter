package net.thedigitallink.flutter.integration.tests;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.User;
import net.thedigitallink.flutter.service.models.UserResponse;
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
public class UserDaoTests {

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public UserDaoTests() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    private URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private User random() {
        User user = podamFactory.manufacturePojoWithFullData(User.class);
        restTemplate.postForEntity(getUri("user-dao","/save"),new HttpEntity<>(user.toRequestString(),httpHeaders),UserResponse.class);
        return user;
    }

    @Test
    public void testGet() {
        User user = random();
        ResponseEntity<UserResponse> entity = restTemplate.postForEntity(getUri("user-dao","/get"),new HttpEntity<>(user.toRequestString(),httpHeaders), UserResponse.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(user.getUsername(), entity.getBody().getPayload().get(0).getUsername());
    }

    @Test
    public void testSave() {
        User user = random();
        ResponseEntity<UserResponse> entity = restTemplate.postForEntity(getUri("user-dao","/save"),new HttpEntity<>(user.toRequestString(),httpHeaders), UserResponse.class);
        assert (entity.getStatusCode().is2xxSuccessful());
    }

}