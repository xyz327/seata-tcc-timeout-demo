package cn.xz.seata.rm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @since 2021/4/30 12:34 下午
 */
@RequestMapping("branch")
@RestController
public class BranchController {

    @Autowired
    private TccService tccService;

    @PostMapping("start")
    public String startBranch() {
        return tccService.branch();
    }
}
