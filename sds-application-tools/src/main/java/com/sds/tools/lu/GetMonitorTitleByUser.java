package com.sds.tools.lu;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.shadansou.sds.common.util.IPDomainUtil;
import com.shadansou.sds.mongodb.api.IMongodbOperation;
import com.shadansou.sds.mongodb.api.impl.MongodbOperationImpl;



/**通过用户账号名获取用户监测站点的url,title等信息
 * @author eefung
 *
 */
public class GetMonitorTitleByUser {
	private static Log logger = LogFactory.getLog(GetMonitorTitleByUser.class .getName());
	private static IMongodbOperation imongo;
	private static Cluster cluster;
	private static File file;
	private static Session session;
	private static ResultSet resultSet;
	private static final String ipRegex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

	public static void init() {
		String cbaseurl = "172.16.112.10";
		imongo = new MongodbOperationImpl(null, null, "172.16.108.10:27017", "security_oauth_v2");
		cluster = Cluster.builder().addContactPoints(cbaseurl).withPort(9042).build();
		file = new File("C:/Users/eefung/Desktop/r10.txt");
		session = cluster.connect();
	}
	
	//通过mongodb查询到的信息组成cbase的id获取title
	public static Map<String, String> getTitle(Document d) {
		Map<String, String> map = new HashMap<String, String>();
		String title = null;
		String url = d.getString("url");
		String port = IPDomainUtil.getPortByUrl(url) + "";
		String ip = d.getString("ip");
		
		//解析url是否包含IP，是则id由ip和port组成，否还需要加host
		String[] checkIp = url.split(":");
		String ipaddr = checkIp[1].substring(3);
		String id = null;
		if (isIpCorrect(ipaddr)) {
			id = ip + "||" + port;
		} else {
			String host = IPDomainUtil.getHost(url);
			id = ip + "|" + host + "|" + port;
		}
		String cql = "select * from sds.finger where id = '" + id + "'";
		resultSet = session.execute(cql);
		for (Row row : resultSet) {
			title = row.getString("title");
		}
		map.put("title", title);
		map.put("url", url);
		map.put("id", id);
		return map;
	}

	public static void main(String[] args) {
		
		String url = "http://218.65.110.177:8888/bsp/jsp/login.jsp";
		System.out.println(IPDomainUtil.getHost(url));
		System.out.println(IPDomainUtil.getPortByUrl(url));
		
		
//		init();
//		Document q = new Document();
//		q.append("username", "sds_test_lrf");
//		List<Document> list = imongo.findLess("monitor_url", q, null);
//
//		String rest = null;
//		String url = null;
//		String title = null;
//		String id = null;
//
//		try {
//			for (Document d : list) {
//				Map<String, String> map = getTitle(d);
//				url = map.get("url");
//				id = map.get("id");
//				title = map.get("title");
//				rest = url + " \t" + id + " \t" + title;
//				FileUtils.write(file, rest + "\n", true);
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		} finally {
//			imongo.close();
//			cluster.close();
//		}
//		System.out.println("结束");
	}

	public static boolean isIpCorrect(String ipAddr) {
		return ipAddr.matches(ipRegex);
	}
}
