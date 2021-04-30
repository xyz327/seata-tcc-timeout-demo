package cn.xz.seata;

import io.seata.core.constants.ConfigurationKeys;
import io.seata.server.Server;

import java.io.IOException;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @since 2021/4/30 12:26 下午
 */
public class SeataServer {
    public static void main(String[] args) throws IOException {
        // 将服务端线程核心线程设置为 1
        System.setProperty(ConfigurationKeys.MIN_SERVER_POOL_SIZE, "1");
        // System.setProperty(ConfigurationKeys.MAX_SERVER_POOL_SIZE, "1");

        Server.main(args);
    }

}
