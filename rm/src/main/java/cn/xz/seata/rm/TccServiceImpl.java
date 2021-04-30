package cn.xz.seata.rm;

import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @since 2021/4/30 2:12 下午
 */
@Service
public class TccServiceImpl implements TccService {
    @Override
    @SneakyThrows
    public String branch() {
        Thread.sleep(2000L);
        return "ok";
    }

    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        return true;
    }

    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        return true;
    }
}
