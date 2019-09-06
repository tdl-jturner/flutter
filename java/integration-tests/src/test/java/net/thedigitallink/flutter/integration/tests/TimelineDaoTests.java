package net.thedigitallink.flutter.integration.tests;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import net.thedigitallink.flutter.service.models.Follow;
import net.thedigitallink.flutter.service.models.FollowResponse;
import net.thedigitallink.flutter.service.models.Timeline;
import net.thedigitallink.flutter.service.models.TimelineResponse;
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
public class TimelineDaoTests {

    @Autowired
    EurekaClient eurekaClient;

    private PodamFactory podamFactory = new PodamFactoryImpl();
    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;

    public TimelineDaoTests() {
        restTemplate=new RestTemplate();
        httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    private URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(),false);
        return URI.create(String.format("http://%s:%s/%s%s",instanceInfo.getIPAddr(),instanceInfo.getPort(),service.toLowerCase(),api));
    }

    private Timeline random() {
        Timeline timeline = podamFactory.manufacturePojoWithFullData(Timeline.class);
        restTemplate.postForEntity(getUri("timeline-dao","/save"),new HttpEntity<>(timeline.toRequestString(),httpHeaders), TimelineResponse.class);
        return timeline;
    }

    @Test
    public void testGet() {
        Timeline timeline = random();
        ResponseEntity<TimelineResponse> entity = restTemplate.postForEntity(getUri("timeline-dao","/get"),new HttpEntity<>(Timeline.builder().user(timeline.getUser()).build().toRequestString(),httpHeaders),TimelineResponse.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(timeline.getAuthor(),(entity.getBody().getPayload().get(0)).getAuthor());
    }

    @Test
    public void testGetSince() {
        Timeline timeline = random();
        ResponseEntity<TimelineResponse> entity = restTemplate.postForEntity(getUri("timeline-dao","/get?since="+timeline.getCreatedDttm()),new HttpEntity<>(Timeline.builder().user(timeline.getUser()).author(timeline.getAuthor()).build().toRequestString(),httpHeaders),TimelineResponse.class);
        assert(entity.getStatusCode().is2xxSuccessful());
        assertEquals(timeline.getMessage(),(entity.getBody().getPayload().get(0)).getMessage());
    }

    @Test
    public void testSave() {
        Timeline timeline = random();
        ResponseEntity<TimelineResponse> entity = restTemplate.postForEntity(getUri("timeline-dao","/save"),new HttpEntity<>(timeline.toRequestString(),httpHeaders), TimelineResponse.class);
        assert (entity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testDelete() {
        Timeline timeline = random();
        ResponseEntity<TimelineResponse> checkEntityPre = restTemplate.postForEntity(getUri("timeline-dao","/get"),new HttpEntity<>(timeline.toRequestString(),httpHeaders), TimelineResponse.class);
        assertNotNull(checkEntityPre.getBody().getPayload());

        ResponseEntity<TimelineResponse> deleteEntity = restTemplate.postForEntity(getUri("timeline-dao","/delete"),new HttpEntity<>(timeline.toRequestString(),httpHeaders), TimelineResponse.class);
        assert (deleteEntity.getStatusCode().is2xxSuccessful());

        ResponseEntity<TimelineResponse> checkEntityPost = restTemplate.postForEntity(getUri("timeline-dao","/get"),new HttpEntity<>(timeline.toRequestString(),httpHeaders), TimelineResponse.class);
        assertNull(checkEntityPost.getBody().getPayload());
    }

}