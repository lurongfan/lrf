package com.sds.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import com.shadansou.sds.common.util.SdsStringUtil;
import com.shadansou.sds.mongodb.api.IMongodbOperation;
import com.shadansou.sds.mongodb.api.impl.MongodbOperationImpl;

public class GetMonitorUrl {

	private static final Log LOGGER = LogFactory.getLog(GetMonitorUrl.class);
	private static IMongodbOperation imongo = new MongodbOperationImpl(null, null, "172.16.108.10:27017",
			"security_oauth_v2");

	public static void main(String[] args) {
		try {
//			File file = new File("C:/Users/eefung/Desktop/url2.txt");
//			List<Document> list = imongo.findAll("monitor_url");
//			for (Document d : list) {
//				String url = d.getString("url");
//				String scheduletime = d.getString("scheduletime");
//				String scantime = d.getString("scantime");
//				if(!SdsStringUtil.strIsNullOrEmpty(scantime)){
//					String date2 = scantime.split(" ")[0].trim();
//					if(date2.trim().equals("2018-09-10")){
//						String result = url+"\t" + "scheduletime:"+scheduletime+"\t"+"scantime:"+scantime+"\t"+"\n";
//						FileUtils.write(file, result, true);
//					}
//				}
//				
//			}
			int num=0;
			File file = new File("C:/Users/eefung/Desktop/url.txt");
			List<Document> list = imongo.findAll("monitor_url");
			for (Document d : list) {
			String scheduletime = d.getString("scheduletime");
				if(!SdsStringUtil.strIsNullOrEmpty(scheduletime)){
					String date1 =scheduletime.split(" ")[0];
					if(date1.trim().equals("2018-09-09")){
						num++;
						String scantime = d.getString("scantime");
						if(!SdsStringUtil.strIsNullOrEmpty(scantime)){
							String date2 = scantime.split(" ")[0].trim();
							if(!"2018-09-10".equals(date2.trim())){
								String url = d.getString("url");
								String result = url+"\t" + "scheduletime:"+scheduletime+"\t"+"scantime:"+scantime+"\t"+"\n";
								FileUtils.write(file, result, true);
							}	
						}			
					}
				}		
			}
			System.out.println(num);
			System.out.println("结束");
			System.out.println("结束");
		} catch (Exception e) {
			// TODO: handle exception
		}	
	}
}
