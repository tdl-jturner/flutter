package net.thedigitallink.flutter.dao.follow;

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
public class FollowDaoService {
    @Autowired
    CasquatchDao casquatchDao;

    @RequestMapping(value = "/save", method= RequestMethod.POST)
    public Response<Void> save(@RequestBody Request<Follow> request) {
        return new Response<Void>(casquatchDao.save(Follow.class,request.getPayload(),request.getQueryOptions()), Response.Status.SUCCESS);
    }
    @RequestMapping(value = "/get", method= RequestMethod.POST)
    public Response<Follow> get(@RequestBody Request<Follow> request) {
        log.error(request.toString());
        return new Response<Follow>(casquatchDao.getById(Follow.class,request.getPayload(),request.getQueryOptions()));
    }
}
