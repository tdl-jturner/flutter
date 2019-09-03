package net.thedigitallink.flutter.dao.message;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.tmobile.opensource.casquatch.CasquatchDao;
import com.tmobile.opensource.casquatch.rest.Request;
import com.tmobile.opensource.casquatch.rest.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

@RestController
@Slf4j
public class MessageDaoService {
    @Autowired
    CasquatchDao casquatchDao;

    @RequestMapping(value = "/save", method= RequestMethod.POST)
    public Response<Void> save(@RequestBody Request<Message> request) {
        return new Response<Void>(casquatchDao.save(Message.class,request.getPayload(),request.getQueryOptions()), Response.Status.SUCCESS);
    }

    @RequestMapping(value = "/get", method= RequestMethod.POST)
    public Response<Message> get(@RequestBody Request<Message> request) {
        return new Response<Message>(casquatchDao.getById(Message.class,request.getPayload(),request.getQueryOptions()));
    }


    @RequestMapping(value = "/getAll", method= RequestMethod.POST)
    public Response<Message> getAll(@RequestBody Request<Message> request) {
        return getAllSince(request,System.currentTimeMillis()-(1*24*60*60*1000));
    }

    @RequestMapping(value = "/getAll?since={createdDate}", method= RequestMethod.POST)
    public Response<Message> getAllSince(@RequestBody Request<Message> request, @PathVariable Long createdDate) {

        Message_StatementFactory messageStatementFactory = new Message_StatementFactory(casquatchDao.getSession());

        Select select = QueryBuilder.selectFrom(messageStatementFactory.getTableName()).all();
        select = messageStatementFactory.selectWhereObject(select,request.getPayload(),request.getQueryOptions());
        select.whereColumn("created_date").isGreaterThan(bindMarker());

        SimpleStatement simpleStatement = select.build(createdDate);

        List<Message> messageList = casquatchDao.execute(simpleStatement).map(messageStatementFactory::map).all();

        return new Response<Message>(messageList);
    }
}
