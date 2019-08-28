package net.thedigitallink.flutter.dao.follow;

import com.tmobile.opensource.casquatch.annotation.CasquatchSpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
@RestController
@CasquatchSpring
public class Application {

    @Autowired
    private DiscoveryClient discoveryClient;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(this.getClass().getPackage().getName()))
                .paths(PathSelectors.any())
                .build();
    }

    @RequestMapping(value="/info", method= RequestMethod.GET)
    public List<ServiceInstance> getInfo(@Value("${spring.application.name}") String applicationName) {
        return this.discoveryClient.getInstances(applicationName.toUpperCase());
    }

    @RequestMapping(value="/health", method=RequestMethod.GET)
    public String getHealth() {
        return "Up";
    }

    @RequestMapping(value="/knownServices", method=RequestMethod.GET)
    public List<String> getKnownServices() {
        return this.discoveryClient.getServices();
    }

}

