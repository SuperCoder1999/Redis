package com.atguigu.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * 手机验证功能
 */
public class PhoneCode {

    public static void main(String[] args) {
    }

    //模拟验证码发送
    @Test
    public void testSendOut() {
        verifyCode("13678765435");
    }

    //模拟验证码校验
    @Test
    public void verifyCode() {
        getRedisCode("13678765435","025048");
    }

    //3 验证码校验
    public static void getRedisCode(String phone,String code) {
        //从redis获取验证码
        Jedis jedis = new Jedis("123.57.92.107",6379);
        jedis.auth("N331150871");
        //验证码key (这里的codeKey是直接拼接出来了,不是多个方法共享的)
        String codeKey = "VerifyCode"+phone+":code";
        String redisCode = jedis.get(codeKey);
        //判断 (应该写成 code.equals(redisCode) 因为前端可以保证code输入非空,而redisCode可能是null)
        if(code.equals(redisCode)) {
            System.out.println("成功");
        }else {
            System.out.println("失败");
        }
        /*老师的:
        if(redisCode.equals(code)) {
            System.out.println("成功");
        }else {
            System.out.println("失败");
        }*/

        jedis.close();
    }

    //2 每个手机每天只能发送三次，验证码放到redis中，设置过期时间120
    public static void verifyCode(String phone) {
        //连接redis
        Jedis jedis = new Jedis("123.57.92.107",6379);
        jedis.auth("N331150871");

        //拼接key,保证每个key都不相同
        //手机发送次数key
        String countKey = "VerifyCode"+phone+":count";
        //验证码key
        String codeKey = "VerifyCode"+phone+":code";

        //每个手机每天只能发送三次
        String count = jedis.get(countKey);
        if(count == null) {
            //没有发送次数，第一次发送
            //设置发送次数是1
            jedis.setex(countKey,24*60*60,"1");
        } else if(Integer.parseInt(count)<=2) {
            //发送次数+1
            jedis.incr(countKey);
        } else if(Integer.parseInt(count)>2) {
            //发送三次，不能再发送
            System.out.println("今天发送次数已经超过三次");
            jedis.close();
            return;
        }

        //发送验证码放到redis里面(这里还有一个功能模块--给用户发送相同的验证码)
        String vcode = getCode();
        jedis.setex(codeKey,120,vcode);
        System.out.println("验证码vcode=" + vcode);
        jedis.close();
    }

    //1 生成6位数字验证码 (优化:random获取一个[0,1)的数,*1000,000再取整
    public static String getCode() {
        Random random = new Random();
        String code = "";
        for(int i=0;i<6;i++) {
            int rand = random.nextInt(10);
            code += rand;
        }
        return code;
    }
}
