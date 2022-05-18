package com.example.Seckill;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;

public class SeckillRedis {

    @Test
    public void SeckillRedis(){
        Jedis jedis = new Jedis("192.168.102.128",6379);
        System.out.println(jedis.ping());
        jedis.close();
    }
    
    public static boolean doSeckill(String uid, String prodid) throws IOException{
        //1、 uid 和 prodid 非空判断
        if (uid == null || prodid == null){
            return false;
        }

        //2、连接 redis
        //Jedis jedis = new Jedis("192.168.102.128",6379);

            //通过连接池得到jedis对象
        JedisPool jedisPoolInstance = JedisPoolUtil.getJedisPoolInstance();
        Jedis jedis = jedisPoolInstance.getResource();

        //3、拼接 key：库存key、秒杀成功用户key
        String kcKey = "sk:" + prodid + ":qt";
        String userKey = "sk:" + prodid + ":user";

        //监视库存
        jedis.watch(kcKey);

        //4、获取库存，如果库存null，秒杀还没开始
        String kc = jedis.get(kcKey);
        if (kc == null){
            System.out.println("秒杀还没有开始，请稍等");
            jedis.close();
            return false;
        }

        //5、判断用户是否重复秒杀操作
        if (jedis.sismember(userKey, uid)) {
            System.out.println("你已经秒杀成功了，不能重复秒杀");
            jedis.close();
            return false;
        }

        //6、判断如果库存数量 <= 0，秒杀结束
        if (Integer.parseInt(kc) <= 0){
            System.out.println("秒杀已经结束");
            jedis.close();
            return false;
        }

        //7、秒杀过程
        //使用事务
        Transaction multi = jedis.multi();

        //组队操作
        multi.decr(kcKey);
        multi.sadd(userKey,uid);

        //执行
        List<Object> results = multi.exec();

        if (results == null || results.size() == 0){
            System.out.println("秒杀失败了......");
            jedis.close();
            return false;
        }


        //7.1、库存-1
        //jedis.decr(kcKey);
        //7.2、把秒杀用户添加清单里面
        //jedis.sadd(userKey,uid);

        System.out.println("秒杀成功");
        jedis.close();
        
        return true;
    }

}
