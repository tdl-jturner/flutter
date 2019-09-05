package net.thedigitallink.flutter.integration.tests;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.*;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TimelineServiceTests {

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public TimelineServiceTests() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    private URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private <E extends AbstractEntity> E random(Class<E> c) {
        E entity = podamFactory.manufacturePojoWithFullData(c);
        restTemplate.postForEntity(getUri(c.getSimpleName().toLowerCase()+"-service","/create"),new HttpEntity<>(entity.toString(),httpHeaders),Void.class);
        return entity;
    }

    private <E extends AbstractEntity> void save(Class<E> c, E entity) {
        restTemplate.postForEntity(getUri(c.getSimpleName().toLowerCase()+"-service","/create"),new HttpEntity<>(entity.toString(),httpHeaders),Void.class);
    }

    @Test
    public void testGet() {
        User mainUser = random(User.class);
        List<User> users = new ArrayList<>();
        for(int i = 0;i<5;i++) {
            users.add(random(User.class));
        }
        for(User user : users) {
            save(Follow.class, Follow.builder().author(user.getUsername()).follower(mainUser.getUsername()).build());
            for(int i=0;i<5;i++) {
                Message message = podamFactory.manufacturePojoWithFullData(Message.class);
                message.setAuthor(user.getUsername());
                save(Message.class,message);
            }
        }

        ResponseEntity<List<Timeline>>  entity = restTemplate.exchange( getUri("timeline-service", String.format("/get/%s",mainUser.getUsername())), HttpMethod.GET,null, new ParameterizedTypeReference<List<Timeline>>(){});
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(entity.getBody().size(),5);
    }

    @Test
    public void testCreate() {
        User user = random(User.class);
        ResponseEntity<Void> entity = restTemplate.postForEntity(getUri("user-service","/create"),new HttpEntity<>(user,httpHeaders),Void.class);
        assert(entity.getStatusCode().is2xxSuccessful());
    }

}