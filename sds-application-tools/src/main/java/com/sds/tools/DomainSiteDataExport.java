package com.sds.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bson.Document;

import com.shadansou.sds.cassandra.api.impl.CassandraOperator;
import com.shadansou.sds.hazelcast.api.imp.HazelcastOperationImpl;
import com.shadansou.sds.index.dto.IndexDocumentDto;
import com.shadansou.sds.index.dto.IndexRequestDto;
import com.shadansou.sds.index.dto.IndexResponseDto;
import com.shadansou.sds.mongodb.api.IMongodbOperation;
import com.shadansou.sds.mongodb.api.impl.MongodbOperationImpl;
import com.shadansou.sds.searchsyntax.regular.SearchBehaviorHolder;
import com.shadansou.sds.searchsyntax.regular.SolrQueryFactory;
import com.shadansou.sds.solr.impl.SolrCloudClient;

public class DomainSiteDataExport {
	public static void main(String[] args) {
		CassandraOperator cass = new CassandraOperator("172.16.111.10", "sds");
		HazelcastOperationImpl hazelcast = new HazelcastOperationImpl("SDSCaster", "SDSCaster", "172.16.110.12:5701");
		SolrCloudClient client = new SolrCloudClient("172.16.106.6:2181", "search");
		IndexRequestDto indexDto = new IndexRequestDto();
		indexDto.addCollection("finger1");
		indexDto.addCollection("finger2");
		indexDto.addCollection("finger3");
		indexDto.addCollection("finger4");
		indexDto.addCollection("finger5");
		IMongodbOperation imongo_user = new MongodbOperationImpl(null, null, "172.16.108.10:27017","security_oauth_v2");
		Document query = new Document();
		Document regex = new Document("$regex", "bzwxb");
		regex.put("$options", "i");
		query.put("groupname", regex);
		Document doc=new Document();
		doc.put("site_begin", 1);
		doc.put("ip", 1);
		List<Document> findLess = imongo_user.findLess("monitor_site", query, doc);
		for (Document document : findLess) {
			System.out.println(document);
//			String site_url=document.getString("site_begin");
//			String site_ip=document.getString("site_ip");
//			String condition = "url:"+site_url;
//			File file=new File("C:/Users/caiwu/Desktop/bozhou11.txt");
//			// 解析搜索语法
//			List<SearchBehaviorHolder> holderList = SolrQueryFactory.getHolderList(condition);
//			if (holderList == null) {
//				return;
//			}
//			if (indexDto.getFiltList() == null) {
//				indexDto.setFiltList(new ArrayList<byte[]>());
//			}
//			boolean isSec = false;
//			// 设置搜索条件
//			for (SearchBehaviorHolder holder : holderList) {
//				if (holder.getSearchCondition().indexOf("CONDITION_ERROR") != -1) {
//					indexDto.setCondition(null);
//					return;
//				}
//				if (holder.getSearchCondition().contains("sec_level")) {
//					isSec = true;
//				}
//				if (indexDto.getCondition() == null) {
//					indexDto.setCondition(holder.getSearchCondition().getBytes());
//				} else {
//					indexDto.getFiltList().add(holder.getSearchCondition().getBytes());
//				}
//			}
//			List<byte[]> filtList = indexDto.getFiltList();
//			for (byte[] bs : filtList) {
//				String ss = new String(bs);
//				try {
//					FileUtils.write(new File("C:/Users/caiwu/Desktop/yufajiexi.txt"), ss, true);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			// 返回的结果形式
//			List<String> returnFields = new ArrayList<String>();
//			returnFields.add("id");
//			returnFields.add("port");
//			returnFields.add("url");
//			returnFields.add("title_str");
//			returnFields.add("province_code");
//			returnFields.add("area_code");
//			returnFields.add("city_code");
//			
//			indexDto.setReturnFields(returnFields);
//			// 返回的结果数量
//			String rownum = "100000";
//			indexDto.setRownum(Integer.parseInt(rownum));
//			try {
//				IndexResponseDto res = client.queryIndex(indexDto);
//				for (IndexDocumentDto result : res.getIndexResult()) {
//					String id = result.getFieldValue("id");
//					String ip = id.split("\\|")[0];
//					String port = result.getFieldValue("port");
//					String url = result.getFieldValue("url");
//					String title_str = result.getFieldValue("title_str");
////					String province_code = result.getFieldValue("province_code");
////					String city_code = result.getFieldValue("city_code");
////					String area_code = result.getFieldValue("area_code");
////					String area = null;
////					if (!SdsStringUtil.isNullOrEmpty(area_code)) {
////						WorldAreaDto object = (WorldAreaDto) hazelcast.getMap("region_code_map").get(area_code);
////						if (object != null) {
////							area = object.getFullname();
////						}
////					} else if (!SdsStringUtil.isNullOrEmpty(city_code)) {
////						WorldAreaDto object = (WorldAreaDto) hazelcast.getMap("region_code_map").get(city_code);
////						if (object != null) {
////							area = object.getFullname();
////						}
////					} else if (!SdsStringUtil.isNullOrEmpty(province_code)) {
////						WorldAreaDto object = (WorldAreaDto) hazelcast.getMap("region_code_map").get(province_code);
////						if (object != null) {
////							area = object.getFullname();
////						}
////					}
////					String icp = null;
////					Map<String, Object> queryCondition = new HashMap<String, Object>();
////					queryCondition.put("id", id);
////					List<Map<String, Object>> query = cass.query("finger", queryCondition);
////					if (query.size() > 0) {
////						Map<String, Object> map = query.get(0);
////						for (String ss : map.keySet()) {
////							Object object = map.get(ss);
////							if (object == null)
////								continue;
////							String[] split = object.toString().split("\n");
////							for (String string : split) {
////								if (string.startsWith("{") && !string.equals("{}") && string.contains("icp=")) {
////									System.out.println(string);
////									String[] split2 = string.split("[,;]");
////									for (String string2 : split2) {
////										if (!string2.trim().startsWith("icp="))
////											continue;
////										icp = string2.trim().substring(4, string2.trim().length());
////										System.out.println(icp);
////									}
////								}
////							}
////						}
////						String rest = title_str + " \t"+ url + " \t" + ip + " \t" + port + " \t"  + icp + " \t" + area
////								+ "\n";
//					String rest =  ip+ " \t"+ port + " \t" + url + " \t" + title_str + "\n";
//						System.out.println(rest);
//						FileUtils.write(file, rest, true);
////					}
////					if(!SdsStringUtil.isNullOrEmpty(url)){
////						String rest = url + "\n";
////						System.out.println(rest);
////						FileUtils.write(new File("C:/Users/caiwu/Desktop/语法导出.txt"), rest, true);
////					} else {
////						String rest = ip + ":" + port + "\n";
////						System.out.println(rest);
////						FileUtils.write(new File("C:/Users/caiwu/Desktop/语法导出aaa.txt"), rest, true);
////					}
//					
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		}
	}

