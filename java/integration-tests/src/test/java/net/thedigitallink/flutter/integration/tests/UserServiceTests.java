package net.thedigitallink.flutter.integration.tests;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.User;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTests {

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public UserServiceTests() {
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
        restTemplate.postForEntity(getUri("user-service","/create"),new HttpEntity<>(user.toString(),httpHeaders),Void.class);
        return user;
    }

    @Test
    public void testGet() {
        User user = random();
        ResponseEntity<User> entity = restTemplate.getForEntity(getUri("user-service","/get")+"/"+user.getUsername().toString(),User.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(entity.getBody().getUsername(),user.getUsername());
    }

    @Test
    public void testGetAll() {
        List<User> userList = new ArrayList<>();
        for(int i = 0;i<5;i++) {
            userList.add(random());
        }
        ResponseEntity<List<User>>  entity = restTemplate.exchange( getUri("user-service", "/getAll"), HttpMethod.GET,null, new ParameterizedTypeReference<List<User>>(){});
        assert(entity.getStatusCode().is2xxSuccessful());
        assert(entity.getBody().size()>=5);
    }

    @Test
    public void testCreate() {
        User user = random();
        ResponseEntity<Void> entity = restTemplate.postForEntity(getUri("user-service","/create"),new HttpEntity<>(user,httpHeaders),Void.class);
        assert(entity.getStatusCode().is2xxSuccessful());
    }

}