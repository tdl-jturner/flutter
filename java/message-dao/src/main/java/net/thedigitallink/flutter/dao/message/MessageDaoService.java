package net.thedigitallink.flutter.dao.message;

import com.tmobile.opensource.casquatch.CasquatchDao;
import com.tmobile.opensource.casquatch.rest.Request;
import com.tmobile.opensource.casquatch.rest.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
