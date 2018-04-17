package io.qyb;

import one.chainclub.qybcoin.jsonrpcclient.QybcoinJSONRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.moqui.context.ExecutionContextFactory;
import org.moqui.context.ToolFactory;

public class QybClientToolFactory implements ToolFactory<QybClientTool> {
    protected final static Logger logger = LoggerFactory.getLogger(QybClientToolFactory.class);
    private QybClientTool qybClientTool;
    private static String usernam;
    private static String password;
    private static String host;
    private static String port;

    @Override
    public void init(ExecutionContextFactory ecf) {
        try {
            usernam = System.getProperty("qyb_rpc_username");
            password = System.getProperty("qyb_rpc_password");
            host = System.getProperty("qyb_rpc_host");
            port = System.getProperty("qyb_rpc_port");

            String rpcUrl = "http://";
            StringBuilder sb = new StringBuilder();
            sb.append("http://").append(usernam).append(":").append(password).append("@").append(host).append(":").append(port == null ? "8332" : port).append("/");
            QybcoinJSONRPCClient qybcoinJSONRPCClient = new QybcoinJSONRPCClient(sb.toString());
            qybClientTool = new QybClientTool(qybcoinJSONRPCClient);

        } catch (Exception e) {
            logger.error("QybClientToolFactory ini failed!");
            e.printStackTrace();
        }
    }

    @Override
    public QybClientTool getInstance(Object... parameters) {
        if (qybClientTool == null) throw new IllegalStateException("QybClientTool not initialized");
        return qybClientTool;
    }
}