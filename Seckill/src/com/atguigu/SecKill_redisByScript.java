package com.atguigu;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.joran.conditional.ElseAction;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;

/**
 * LUA脚本解决 库存遗留问题
 */
public class SecKill_redisByScript {
	
	private static final  org.slf4j.Logger logger =LoggerFactory.getLogger(SecKill_redisByScript.class) ;

	public static void main(String[] args) {
		JedisPool jedispool =  JedisPoolUtil.getJedisPoolInstance();
 
		Jedis jedis=jedispool.getResource();
		System.out.println(jedis.ping());
		
		Set<HostAndPort> set=new HashSet<HostAndPort>();

	//	doSecKill("201","sk:0101");
	}

	//复制来的
	static String secKillScript =
			"local userid=KEYS[1];\r\n" +
			"local prodid=KEYS[2];\r\n" + //传入两个变量
			"local qtkey='sk:'..prodid..\":qt\";\r\n" + 
			"local usersKey='sk:'..prodid..\":user\";\r\n" + //拼接库存key和userkey
			"local userExists=redis.call(\"sismember\",usersKey,userid);\r\n" + //判断user是否存在
			"if tonumber(userExists)==1 then \r\n" + 
			"   return 2;\r\n" + //如果是1(即存在该user)就return 2 (关于返回值,java代码有判断)
			"end\r\n" + 
			"local num= redis.call(\"get\" ,qtkey);\r\n" + //判断库存
			"if tonumber(num)<=0 then \r\n" + 
			"   return 0;\r\n" + //库存为0,return 0,表示秒杀结束
			"else \r\n" + 
			"   redis.call(\"decr\",qtkey);\r\n" + 
			"   redis.call(\"sadd\",usersKey,userid);\r\n" +//操作redis数据
			"end\r\n" + 
			"return 1" ;//return 1 表示添加成功
			 
	static String secKillScript2 = 
			"local userExists=redis.call(\"sismember\",\"{sk}:0101:usr\",userid);\r\n" +
			" return 1";

	public static boolean doSecKill(String uid,String prodid) throws IOException {

		JedisPool jedispool =  JedisPoolUtil.getJedisPoolInstance();
		Jedis jedis=jedispool.getResource();

		//String sha1=  .secKillScript;  加载脚本
		String sha1=  jedis.scriptLoad(secKillScript);
		//给名称为sha1的LUA脚本赋值两个变量,并执行
		Object result= jedis.evalsha(sha1, 2, uid,prodid);

		//判断返回值
		String reString=String.valueOf(result);
		if ("0".equals( reString )  ) {
			System.err.println("已抢空！！");
		}else if("1".equals( reString )  )  {
			System.out.println("LUA脚本===抢购成功！！！！");
		}else if("2".equals( reString )  )  {
			System.err.println("该用户已抢过！！");
		}else{
			System.err.println("抢购异常！！");
		}
		jedis.close();
		return true;
	}
}
