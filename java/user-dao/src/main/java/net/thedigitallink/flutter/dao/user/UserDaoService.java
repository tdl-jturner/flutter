package net.thedigitallink.flutter.dao.user;

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
public class UserDaoService {
    @Autowired
    CasquatchDao casquatchDao;

    @RequestMapping(value = "/save", method= RequestMethod.POST)
    public Response<Void> save(@RequestBody Request<User> request) {
        log.trace("POST | /save | {}",request.toString());
        return new Response<>(casquatchDao.save(User.class,request.getPayload(),request.getQueryOptions()), Response.Status.SUCCESS);
    }
    @RequestMapping(value = "/get", method= RequestMethod.POST)
    public Response<User> get(@RequestBody Request<User> request) {
        log.trace("POST | /get | {}",request.toString());
        return new Response<>(casquatchDao.getById(User.class,request.getPayload(),request.getQueryOptions()));
    }
}
