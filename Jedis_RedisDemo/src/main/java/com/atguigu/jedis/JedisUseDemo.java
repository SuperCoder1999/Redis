package com.atguigu.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class JedisUseDemo {

    public static void main(String[] args) {

    }

    //操作zset
    @Test
    public void jedisZsetMethods() {
        //创建Jedis对象
        Jedis jedis = new Jedis("123.57.92.107",6379);
        jedis.auth("N331150871");

        //score类型是 double
        jedis.zadd("china",100d,"shanghai");

        Set<String> china = jedis.zrange("china", 0, -1);
        System.out.println(china);

        jedis.close();
    }

    //操作hash
    @Test
    public void jedisHashMethods() {
        //创建Jedis对象
        Jedis jedis = new Jedis("123.57.92.107",6379);
        jedis.auth("N331150871");

        jedis.hset("users","age","20");
        String hget = jedis.hget("users", "age");
        System.out.println(hget);
        jedis.close();
    }

    //操作set
    @Test
    public void jedisSetMethods() {
        //创建Jedis对象
        Jedis jedis = new Jedis("123.57.92.107",6379);
        jedis.auth("N331150871");


        jedis.sadd("names","lucy");
        jedis.sadd("names","mary");

        Set<String> names = jedis.smembers("names");
        System.out.println(names);
        jedis.close();
    }

    //操作list
    @Test
    public void jedisListMethods() {
        //创建Jedis对象
        Jedis jedis = new Jedis("123.57.92.107",6379);
        jedis.auth("N331150871");

        jedis.lpush("key1","lucy","mary","jack");
        List<String> values = jedis.lrange("key1", 0, -1);
        System.out.println(values);
        jedis.close();
    }

    //操作key string
    @Test
    public void jedisStringMethods() {
        //创建Jedis对象
        Jedis jedis = new Jedis("123.57.92.107",6379);
        jedis.auth("N331150871");

        System.out.println(jedis.get("k1"));

        //添加
        jedis.set("name","lucy");

        //获取
        String name = jedis.get("name");
        System.out.println("jedis.get(\"name\")= " + name);

        //设置多个key-value
        jedis.mset("k3","v3","k2","v2");
        List<String> mget = jedis.mget("k3", "k2");
        System.out.println("mget集合= " + mget);

        //设置过期时间 / 查看存活时间
        /*
        //set并且设置存活时间后,注销掉,否则重新set或设置时间,查询结果还是100秒
        jedis.set("k1", "v1");
        System.out.println("设置: " + jedis.expire("k1", 100));*/
        System.out.println("k1 ttl= " + jedis.ttl("k1"));

        //相当于命令: keys *  [keys keyName,keyName就是匹配key的,*的功能是匹配所有]
        Set<String> keys = jedis.keys("*");
        for(String key : keys) {
            System.out.println("keyName=" + key);
        }
        jedis.close();
    }

    /**
     * 创建jedis,连接服务器的redis.类似创建datasource,连接mysql
     */
    @Test
    public void testRedisByJedis() {
        //创建Jedis对象,connect timeout =>设置安全组和防火墙放行端口6379
        Jedis jedis = new Jedis("123.57.92.107",6379);
        jedis.auth("N331150871");
        //测试
        String value = jedis.ping();
        System.out.println(value);
        jedis.close();
    }
}
