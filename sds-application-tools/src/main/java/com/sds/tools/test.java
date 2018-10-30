package com.sds.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.datastax.driver.core.Metadata;
import com.shadansou.sds.common.util.IPUtil;
import com.shadansou.sds.common.util.SdsStringUtil;
import com.shadansou.sds.commoninner.po.schema.Ip_Detail_Schema;

import net.sf.json.JSONObject;


public class test {
	public static void main1(String[] args) {
		// Map<String,Object> map=new HashMap<String, Object>();
		// Map<String,String> ma=new HashMap<String, String>();
		// ma.put("result", "1");
		// map.put("result", ma);
		// Map<String,String> object =(Map<String,String> )map.get("result");
		// object.put("result", "2");
		// try {
		// String str=URLDecoder.decode("新疆商贸", "gbk");
		// System.out.println(str);
		// String str1 = URLEncoder.encode(str,"ASCII");
		// System.out.println(str1);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		String ascii = IDN.toASCII("新疆商贸.com");
		System.out.println(ascii);
	}

	public static void main2(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("sds");
		if (list.contains("sds")) {
			System.out.println("sssssssssss");
		}
	}

	public static void main(String[] args) {
		test te=new test();
		Set<String> set = new HashSet<String>();
		try {
			List<String> readLines = FileUtils.readLines(new File("C:/Users/caiwu/Desktop/未知ip已处理1.txt"), "UTF-8");
			for (String string : readLines) {
//				String ip = string.split(" ")[1].trim();
				set.add(string);
			}
//			List<String> read = FileUtils.readLines(new File("C:/Users/caiwu/Desktop/未知0702.txt"), "UTF-8");
//			for (String string : read) {
//				String ip = string.split(" ")[1].trim();
//				set.add(ip);
//			}
			List<String> list = new ArrayList<String>();
			Map<Long, String> map = new HashMap<Long, String>();
			for (String string : set) {
				long iplong = IPUtil.ipToLong(string);
				map.put(iplong, string);
			}
			List<Entry<Long, String>> entryList = new ArrayList<Map.Entry<Long, String>>(map.entrySet());
			Collections.sort(entryList, new Comparator<Entry<Long, String>>() {
				public int compare(Entry<Long, String> o1, Entry<Long, String> o2) {
					return -o2.getKey().compareTo(o1.getKey());
				}
			});
			for (Entry<Long, String> entry : entryList) {
				list.add(entry.getValue());
			}
			String temp = "dd";
			for (String string : list) {
				System.out.println(string);
				if(string.startsWith(temp)){
					continue;
				}else{
					//查basicservice 如果有国家的话过滤掉，没有的话写入文件
					String string2 = te.get(string);
					if(SdsStringUtil.isNullOrEmpty(string2)){
						FileUtils.write(new File("C:/Users/caiwu/Desktop/未知ip已处理2.txt"), string + "\n", true);
						continue;
					}
					JSONObject json=JSONObject.fromObject(string2);
					FileUtils.write(new File("C:/Users/caiwu/Desktop/ip地域信息2.txt"), string+"\t\t\t"+json + "\n", true);
					if(!json.containsKey("country_name") || SdsStringUtil.isNullOrEmpty(json.getString("country_name"))){
						FileUtils.write(new File("C:/Users/caiwu/Desktop/未知ip已处理2.txt"), string + "\n", true);
					}
				}
				temp = string.split("\\.")[0] + "." + string.split("\\.")[1]+ "." + string.split("\\.")[2];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		test te=new test();
//		String string = te.get("103.113.158.37");
//		System.out.println(string);
//	}
//	
	public String get(String ip) {    
	    try {    
	        HttpClient client = new DefaultHttpClient();  
	        // 创建httpget.      
	        HttpGet httpget = new HttpGet("http://172.16.109.118:8181/cxf/commbasic/locationInfo/queryIpDetail/?ip=" + ip);    
	        System.out.println("executing request " + httpget.getURI());    
	        // 执行get请求.      
	        HttpResponse response = client.execute(httpget);    
	        try {    
	            // 获取响应实体      
	            HttpEntity entity = response.getEntity();       
	            // 打印响应状态      
	            System.out.println(response.getStatusLine()); 
	            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
	            if (entity != null) {
	            	InputStream content = entity.getContent();
	            	byte[] buffer = new byte[1024];  
	                int len = -1;  
	                while ((len = content.read(buffer)) != -1) {  
	                	outSteam.write(buffer, 0, len);  
	                }
	            	return outSteam.toString();
	            }
	    } catch (Exception e) {    
	        e.printStackTrace();    
	    }  
	    }catch (IOException e) {    
	        e.printStackTrace();    
	    }
	    return null;
	}
}
