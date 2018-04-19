package io.qyb;

import java.math.BigDecimal;

public class RedPacket {
    int remainSize;
    BigDecimal remainMoney;

    RedPacket(int remainSize, BigDecimal remainMoney) {
        this.remainSize = remainSize;
        this.remainMoney = remainMoney;
    }


}
