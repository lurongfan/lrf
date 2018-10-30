package com.sds.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import com.shadansou.sds.common.util.IPDomainUtil;
import com.shadansou.sds.mongodb.api.IMongodbOperation;
import com.shadansou.sds.mongodb.api.impl.MongodbOperationImpl;

public class GetMonitor {

	private static final Log LOGGER = LogFactory.getLog(GetAllMonitorUrl.class);
	private static IMongodbOperation imongo = new MongodbOperationImpl(null, null, "172.16.108.10:27017",
			"security_oauth_v2");

//	public static void main(String[] args) {
//		try {
//			String host = new URL("http://www.baidu.com").getHost().toLowerCase();
//			System.out.println(host);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static void main(String[] args) {
		File file = new File("C:/Users/eefung/Desktop/yuyanbin.txt");
		
		Document q = new Document();
		q.append("username", "yuyanbin@oshadan.com");
		List<Document> list = imongo.findLess("monitor_url", q, null);
		for(Document d : list){
			String url =d.getString("url");
			try {
				System.out.println("url");
				FileUtils.write(file, url+"\n", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("结束");
		
//		File file = new File("C:/Users/eefung/Desktop/chongqing2.txt");
//		// List<Document> list = imongo.findAll("monitor_url");
//		Document q = new Document();
//		q.append("region_name", "重庆市 垫江县");
//		List<Document> list = imongo.findLess("group_info", q, null);
//		
//		for(Document d : list){
//			String groupid = d.getString("_id");
//			Document q1 = new Document();
//			q1.append("groupid", groupid);
//			
//			List<Document> list1 = imongo.findLess("monitor_url",q1, null);
//			for(Document c :list1){
//				String url =c.getString("url");
//				try {
//					FileUtils.write(file, url+"\n", true);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//			
//		}
//		System.out.println("结束");
		
//		
//		String url = null;
//		String ip = null;
//		String port = null;
//		String rest = null;
//		for (Document d : list) {
//			url = d.getString("url");
//			port = IPDomainUtil.getPortByUrl(url) + "";
//			
//			ip = d.getString("ip");
//
//			rest = url + " \t" + ip + " \t" + port + " \n";
//			try {
//				FileUtils.write(file, url+"\n", true);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//
	}

}
