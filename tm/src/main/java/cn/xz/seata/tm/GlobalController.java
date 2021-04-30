package cn.xz.seata.tm;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @since 2021/4/30 12:37 下午
 */
@RequestMapping
@RestController
@Slf4j
public class GlobalController {
    RestTemplate restTemplate = new RestTemplateBuilder().build();
    @Autowired
    private GlobalController globalController;

    @GetMapping("global")
    @GlobalTransactional
    public String global() {
        RequestEntity<Void> requestEntity = RequestEntity.post("http://localhost:8090/branch/start")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(RootContext.KEY_XID, RootContext.getXID())
                .build();
        log.info("request:{}", requestEntity);

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        return "ok";
    }

    @GetMapping("timeout")
    public String timeout() {
        globalController.global();
        globalController.global();

        return "ok";
    }
}
