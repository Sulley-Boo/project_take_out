import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class JedisTest {

    @Test
    public void test() {
        // 获取连接
        Jedis jedis = new Jedis("localhost", 6379);

        // 执行具体操作
        jedis.set("username", "X.D.He");
        System.out.println(jedis.get("username"));

        jedis.hset("userInfo", "addr", "anqing");
        jedis.hset("userInfo", "birth", "1996-08-22");
        System.out.println(jedis.hget("userInfo", "addr"));
        System.out.println(jedis.hget("userInfo", "birth"));

        Set<String> keys = jedis.keys("*");
        System.out.println(keys);

        // 关闭连接
        jedis.close();
    }
}
