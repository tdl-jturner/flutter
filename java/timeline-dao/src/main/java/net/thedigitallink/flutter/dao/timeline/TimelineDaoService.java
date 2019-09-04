package net.thedigitallink.flutter.dao.timeline;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.netflix.discovery.EurekaClient;
import com.tmobile.opensource.casquatch.CasquatchDao;
import com.tmobile.opensource.casquatch.rest.Request;
import com.tmobile.opensource.casquatch.rest.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

@RestController
@Slf4j
public class TimelineDaoService {

    private CasquatchDao casquatchDao;

    @Autowired
    private EurekaClient eurekaClient;

    Timeline_StatementFactory timelineStatementFactory;


    public TimelineDaoService(CasquatchDao casquatchDao) {
        this.casquatchDao=casquatchDao;
        timelineStatementFactory = new Timeline_StatementFactory(casquatchDao.getSession());
    }

    @RequestMapping(value = "/get", method= RequestMethod.POST)
    public Response<Timeline> get(@RequestBody Request<Timeline> request, @RequestParam(value="since", required = false) Long createdDate) {
        if(createdDate!=null) {
            log.trace("POST | /get?since={} | {}", createdDate, request.toString());

            Timeline timeline = request.getPayload();
            Select select = QueryBuilder.selectFrom(timelineStatementFactory.getTableName()).all();
            select = timelineStatementFactory.selectWhereObject(select, timeline, request.getQueryOptions());
            select = select.whereColumn("created_dttm").isGreaterThanOrEqualTo(bindMarker());
            timeline.setCreatedDttm(createdDate);

            BoundStatementBuilder boundStatementBuilder = casquatchDao.getSession().prepare(select.build()).boundStatementBuilder();
            boundStatementBuilder = timelineStatementFactory.bindObject(boundStatementBuilder, timeline, request.getQueryOptions());
            BoundStatement boundStatement = boundStatementBuilder.build();

            List<Timeline> timelineList = casquatchDao.execute(boundStatement).map(timelineStatementFactory::map).all();

            return new Response<>(timelineList);
        }
        else {
            log.trace("POST | /get | {}",request.toString());
            return new Response<>(casquatchDao.getAllById(Timeline.class,request.getPayload(),request.getQueryOptions()));
        }
    }

    @RequestMapping(value = "/save", method= RequestMethod.POST)
    public Response<Void> save(@RequestBody Request<Timeline> request) {
        log.trace("POST | /save | {}",request.toString());
        return new Response<>(casquatchDao.save(Timeline.class,request.getPayload(),request.getQueryOptions()));
    }


}
