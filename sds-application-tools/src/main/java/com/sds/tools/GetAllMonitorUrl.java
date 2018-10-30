package com.sds.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import com.shadansou.sds.mongodb.api.IMongodbOperation;
import com.shadansou.sds.mongodb.api.impl.MongodbOperationImpl;

/*获取所有的监测网站*/
public class GetAllMonitorUrl {

	private static final Log LOGGER = LogFactory.getLog(GetAllMonitorUrl.class);
	private static IMongodbOperation imongo = new MongodbOperationImpl(null, null, "172.16.108.10:27017",
			"security_oauth_v2");

	public static void main(String[] args) {
		try {
			File file = new File("C:/Users/eefung/Desktop/result10.txt");
			List<Document> list = imongo.findAll("monitor_url");
			for (Document d : list) {
			String url = d.getString("url")+"\n";
			System.out.println(url);
				FileUtils.write(file, url, true);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}	
	}
}
