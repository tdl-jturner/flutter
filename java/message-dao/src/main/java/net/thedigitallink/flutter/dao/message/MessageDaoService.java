package net.thedigitallink.flutter.dao.message;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.tmobile.opensource.casquatch.CasquatchDao;
import com.tmobile.opensource.casquatch.rest.Request;
import com.tmobile.opensource.casquatch.rest.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

@RestController
@Slf4j
public class MessageDaoService {

    private CasquatchDao casquatchDao;
    private Message_StatementFactory messageStatementFactory;
    private MessageByAuthor_StatementFactory messageByAuthorStatementFactory;

    public MessageDaoService(CasquatchDao casquatchDao) {
        this.casquatchDao=casquatchDao;
        messageStatementFactory = new Message_StatementFactory(casquatchDao.getSession());
        messageByAuthorStatementFactory = new MessageByAuthor_StatementFactory(casquatchDao.getSession());
    }

    @RequestMapping(value = "/save", method= RequestMethod.POST)
    public Response<Void> save(@RequestBody Request<Message> request) {
        log.trace("POST | /save | {}",request.toString());
        casquatchDao.save(Message.class,request.getPayload(),request.getQueryOptions());
        casquatchDao.save(MessageByAuthor.class,new MessageByAuthor(request.getPayload()),request.getQueryOptions());
        return new Response<>(null, Response.Status.SUCCESS);
    }

    @RequestMapping(value = "/get", method= RequestMethod.POST)
    public Response<Message> get(@RequestBody Request<Message> request) {
        log.trace("POST | /get | {}",request.toString());
        return new Response<>(casquatchDao.getById(Message.class,request.getPayload(),request.getQueryOptions()));
    }

    @RequestMapping(value = "/getAll", method= RequestMethod.POST)
    public Response<MessageByAuthor> getAll(@RequestBody Request<MessageByAuthor> request, @RequestParam(value="since",required = false) Long createdDate) {
        if(createdDate==null) {
            createdDate = System.currentTimeMillis()-(1*24*60*60*1000);
        }
        log.trace("POST | /getAll?since={} | {}",createdDate,request.toString());

        MessageByAuthor message=request.getPayload();
        Select select = QueryBuilder.selectFrom(messageByAuthorStatementFactory.getTableName()).all();
        select = messageByAuthorStatementFactory.selectWhereObject(select,message,request.getQueryOptions());
        select = select.whereColumn("created_dttm").isGreaterThanOrEqualTo(bindMarker());
        message.setCreatedDttm(createdDate);

        BoundStatementBuilder boundStatementBuilder = casquatchDao.getSession().prepare(select.build()).boundStatementBuilder();
        boundStatementBuilder = messageByAuthorStatementFactory.bindObject(boundStatementBuilder, message, request.getQueryOptions());
        BoundStatement boundStatement = boundStatementBuilder.build();

        List<MessageByAuthor> messageList = casquatchDao.execute(boundStatement).map(messageByAuthorStatementFactory::map).all();

        return new Response<>(messageList);
    }
}
