package cn.xz.seata.rm;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @since 2021/4/30 12:55 下午
 */
@LocalTCC()
public interface TccService {

    @TwoPhaseBusinessAction(name = "branch")
    public String branch();

    boolean commit(BusinessActionContext businessActionContext);

    boolean rollback(BusinessActionContext businessActionContext);
}
