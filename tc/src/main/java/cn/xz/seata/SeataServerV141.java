package cn.xz.seata;

import io.seata.common.XID;
import io.seata.common.thread.NamedThreadFactory;
import io.seata.common.util.NetUtil;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.core.rpc.ShutdownHook;
import io.seata.core.rpc.netty.NettyRemotingServer;
import io.seata.server.ParameterParser;
import io.seata.server.Server;
import io.seata.server.UUIDGenerator;
import io.seata.server.coordinator.DefaultCoordinator;
import io.seata.server.env.ContainerHelper;
import io.seata.server.env.PortHelper;
import io.seata.server.metrics.MetricsManager;
import io.seata.server.session.SessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @since 2021/4/30 12:26 下午
 */
public class SeataServerV141 {
//    public static void main(String[] args) throws IOException {
//        // 将服务端线程设置为 1
//        System.setProperty(ConfigurationKeys.MIN_SERVER_POOL_SIZE, "1");
//        System.setProperty(ConfigurationKeys.MAX_SERVER_POOL_SIZE, "1");
//
//        Server.main(args);
//    }


    private static final int MIN_SERVER_POOL_SIZE = 1;
    private static final int MAX_SERVER_POOL_SIZE = 500;
    private static final int MAX_TASK_QUEUE_SIZE = 20000;
    private static final int KEEP_ALIVE_TIME = 500;
    private static final ThreadPoolExecutor WORKING_THREADS = new ThreadPoolExecutor(MIN_SERVER_POOL_SIZE,
            MAX_SERVER_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(MAX_TASK_QUEUE_SIZE),
            new NamedThreadFactory("ServerHandlerThread", MAX_SERVER_POOL_SIZE), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
        // get port first, use to logback.xml
        int port = PortHelper.getPort(args);
        System.setProperty(ConfigurationKeys.SERVER_PORT, Integer.toString(port));

        // create logger
        final Logger logger = LoggerFactory.getLogger(Server.class);
        if (ContainerHelper.isRunningInContainer()) {
            logger.info("The server is running in container.");
        }

        //initialize the parameter parser
        //Note that the parameter parser should always be the first line to execute.
        //Because, here we need to parse the parameters needed for startup.
        ParameterParser parameterParser = new ParameterParser(args);

        //initialize the metrics
        MetricsManager.get().init();

        System.setProperty(ConfigurationKeys.STORE_MODE, parameterParser.getStoreMode());

        NettyRemotingServer nettyRemotingServer = new NettyRemotingServer(WORKING_THREADS);
        //server port
        nettyRemotingServer.setListenPort(parameterParser.getPort());
        UUIDGenerator.init(parameterParser.getServerNode());
        //log store mode : file, db, redis
        SessionHolder.init(parameterParser.getStoreMode());

        DefaultCoordinator coordinator = new DefaultCoordinator(nettyRemotingServer);
        coordinator.init();
        nettyRemotingServer.setHandler(coordinator);
        // register ShutdownHook
        ShutdownHook.getInstance().addDisposable(coordinator);
        ShutdownHook.getInstance().addDisposable(nettyRemotingServer);

        //127.0.0.1 and 0.0.0.0 are not valid here.
        if (NetUtil.isValidIp(parameterParser.getHost(), false)) {
            XID.setIpAddress(parameterParser.getHost());
        } else {
            XID.setIpAddress(NetUtil.getLocalIp());
        }
        XID.setPort(nettyRemotingServer.getListenPort());

        try {
            nettyRemotingServer.init();
        } catch (Throwable e) {
            logger.error("nettyServer init error:{}", e.getMessage(), e);
            System.exit(-1);
        }

        System.exit(0);
    }
}
