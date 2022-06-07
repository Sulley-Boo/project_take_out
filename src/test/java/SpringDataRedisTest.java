import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 操作String类型数据
     */
    @Test
    public void testString() {
        redisTemplate.opsForValue().set("city", "Guilin");
        String value = (String) redisTemplate.opsForValue().get("city");
        System.out.println(value);

        redisTemplate.opsForValue().set("school", "桂林电子科技大学", 10l, TimeUnit.MILLISECONDS);

    }

    @Test
    public void testHash() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("user", "name", "X.D.He");
        hashOperations.put("user", "age", "25");

    }

}
