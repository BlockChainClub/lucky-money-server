package io.qyb;

import one.chainclub.qybcoin.jsonrpcclient.QybcoinException;
import one.chainclub.qybcoin.jsonrpcclient.QybcoinJSONRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QybClientTool {
    protected final static Logger logger = LoggerFactory.getLogger(QybClientTool.class);
    private QybcoinJSONRPCClient qybClient;

    public QybClientTool(QybcoinJSONRPCClient qybClient) {
        this.qybClient = qybClient;
    }

    public String getAddress(String account) {
        String address = "";
        try {
            if (account != null && account != ""){
                address = qybClient.getAccountAddress(account);
            }else{
                address = qybClient.getNewAddress();
            }
        } catch (QybcoinException e) {
            logger.error("QybClientTool test getAccountAddress 'redpacket' error");
        }
        return address;
    }
}
