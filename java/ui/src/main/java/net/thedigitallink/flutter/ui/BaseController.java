package net.thedigitallink.flutter.ui;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

@Slf4j
public class BaseController {

    @Autowired
    private EurekaClient eurekaClient;

    protected RestTemplate restTemplate;
    protected HttpHeaders httpHeaders;

    public BaseController() {
        restTemplate = new RestTemplate();
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    protected URI getUri(String service, String api) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(service.toUpperCase(), false);
        return URI.create(String.format("http://%s:%s/%s%s", instanceInfo.getIPAddr(), instanceInfo.getPort(), service.toLowerCase(), api));
    }
}
