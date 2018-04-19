package io.qyb;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

public class RedPacketTool {

    public static final String LUASCRIPT = "if redis.pcall('hexists', KEYS[2], KEYS[3]) == 1 then return -1 elseif redis.pcall('exists', KEYS[1]) == 0 then return -3 else local amount = redis.call('rpop', KEYS[1]) if amount then redis.call('hmset', KEYS[2], KEYS[3], amount) return amount else return -2 end end";

    public static final String PACKET_PREFIX = "redPacket";

    static MathContext mc = new MathContext(7);

    public static JedisPool jedisPool;

    public RedPacketTool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private Jedis getJedis() {
        return jedisPool.getResource();
    }

    public String keyBuilder(String... words) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i == 0){
                sb.append(words[i]);
            }else{
                if (StringUtils.isNotEmpty(words[i])) sb.append(":").append(words[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 生成随机金额
     *
     * @param redPacket
     * @return
     */
    public BigDecimal getRandomMoney(RedPacket redPacket) {
        if (redPacket.remainSize == 1) {
            redPacket.remainSize--;
            return redPacket.remainMoney;
        }
        Random r = new Random();
        BigDecimal min = new BigDecimal("0.01", mc);
        BigDecimal avg = redPacket.remainMoney.divide(new BigDecimal(redPacket.remainSize), mc);
        BigDecimal max = avg.multiply(new BigDecimal(2, mc));
        BigDecimal money = new BigDecimal(String.valueOf(r.nextDouble()), mc).multiply(max, mc);
        money = money.compareTo(min) <= 0 ? new BigDecimal(0.01, mc) : money;
        redPacket.remainSize--;
        redPacket.remainMoney = redPacket.remainMoney.subtract(money, mc);
        return money;
    }

    /**
     * 设置红包
     *
     * @param orderId
     * @param values
     * @return
     */
    public long lpush(String orderId, String... values) {
        try {
            String key = keyBuilder(PACKET_PREFIX, orderId);
            return getJedis().lpush(key, values);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 生成红包并放入redis
     * @param orderId
     * @param allot
     * @param amount
     * @param quantity
     * @return
     */
    public long build(String orderId, String allot, BigDecimal amount, int quantity) {
        String[] values = new String[quantity];
        if ("FIXED".equals(allot)) {
            for (int i = 0; i < quantity; i++) {
                values[i] = amount.divide(new BigDecimal(quantity, mc), mc).toString();
            }
        } else if ("RANDOM".equals(allot)) {
            RedPacket redPacket = new RedPacket(quantity, amount);
            for (int i = 0; i < quantity; i++) {
                values[i] = getRandomMoney(redPacket).toString();
            }
        }
        return lpush(orderId, values);
    }

    /**
     * 获取红包
     * -4 错误
     * -1 已领过
     * -2 已领完
     * -3 不存在
     * >0 金额
     *
     * @param userId
     * @param orderId
     */
    public Object getRedPacket(String userId, String orderId) {
        try {
            String key1 = keyBuilder(PACKET_PREFIX, orderId);
            return getJedis().eval(LUASCRIPT, 3, key1, key1 + ":got", userId);
        } catch (Exception e) {
            return null;
        }
    }

}
