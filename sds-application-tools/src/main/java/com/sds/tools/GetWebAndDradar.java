package com.sds.tools;

import java.util.List;

import com.sds.jedis.RedisService;
import com.sds.jedis.RedisServiceImpl;

public class GetWebAndDradar {
	
	public static void main(String[] args) {
		RedisService	redis = new RedisServiceImpl("172.16.106.113", 6379, "shadan2017*#");
		List<String> list=redis.lrange("wradar_tasks", 0, -1);
		List<String> list1=redis.lrange("wradar_real_time_tasks", 0, -1);
		System.out.println(list.size());
		for(String s :list){
			System.out.println(s);
		}
		
	}

}
