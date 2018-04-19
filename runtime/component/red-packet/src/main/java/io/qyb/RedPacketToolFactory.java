package io.qyb;

import org.moqui.context.ExecutionContextFactory;
import org.moqui.context.ToolFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedPacketToolFactory implements ToolFactory<RedPacketTool> {

    public static RedPacketTool redPacketTool;

    @Override
    public void init(ExecutionContextFactory ecf) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.parseInt(System.getProperty("redis_max_idle")));

        jedisPoolConfig.setMaxTotal(Integer.parseInt(System.getProperty("redis_max_total")));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(System.getProperty("redis_max_wait_millis")));
        jedisPoolConfig.setTestOnReturn(true);

        jedisPoolConfig.setNumTestsPerEvictionRun(1024);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(1800000);
        jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(10000);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setBlockWhenExhausted(false);

        String password = System.getProperty("redis_password");
        JedisPool pool;
        if (password != null && password != "") {
            pool = new JedisPool(jedisPoolConfig, System.getProperty("redis_host"), Integer.parseInt(System.getProperty("redis_port")), Integer.parseInt(System.getProperty("redis_time_out")), password);
        } else {
            pool = new JedisPool(jedisPoolConfig, System.getProperty("redis_host"));
        }
        this.redPacketTool = new RedPacketTool(pool);
    }

    @Override
    public RedPacketTool getInstance(Object... parameters) {
        if (redPacketTool == null) throw new IllegalStateException("redPacketTool not initialized");
        return redPacketTool;
    }
}
