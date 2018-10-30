//package com.sds.tools;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.apache.commons.io.FileUtils;
//import com.shadansou.sds.cassandra.api.impl.CassandraOperator;
//import com.shadansou.sds.common.util.SdsStringUtil;
//import com.shadansou.sds.commoninner.area.dto.WorldAreaDto;
//import com.shadansou.sds.commoninner.plugin.dto.Dradar_Vul_Nse_Map;
//import com.shadansou.sds.commoninner.plugin.dto.Vul_Box;
//import com.shadansou.sds.hazelcast.api.imp.HazelcastOperationImpl;
//import com.shadansou.sds.index.dto.IndexDocumentDto;
//import com.shadansou.sds.index.dto.IndexRequestDto;
//import com.shadansou.sds.index.dto.IndexResponseDto;
//import com.shadansou.sds.searchsyntax.regular.SearchBehaviorHolder;
//import com.shadansou.sds.searchsyntax.regular.SolrQueryFactory;
//import com.shadansou.sds.solr.impl.SolrCloudClient;
//
///**
// * 通过销售那边给的语法，进行解析从而导出他们需要的数据到固定文件
// * 
// * @author wyb
// * @since 2018-04-25
// */
//public class SyntaxParsingVulDataExport {
//	public static void main(String[] args) {
//		CassandraOperator cass = new CassandraOperator("172.16.111.10", "sds");
//		HazelcastOperationImpl hazelcast = new HazelcastOperationImpl("SDSCaster", "SDSCaster", "172.16.110.12:5701");
//		SolrCloudClient client = new SolrCloudClient("172.16.106.6:2181", "search");
//		IndexRequestDto indexDto = new IndexRequestDto();
//		indexDto.addCollection("finger1");
//		indexDto.addCollection("finger2");
//		indexDto.addCollection("finger3");
//		indexDto.addCollection("finger4");
//		indexDto.addCollection("finger5");
//
//		String condition = "地区：渝北区|title:渝北    漏洞:高|漏洞:中|漏洞:低 日期:[2018-01-01,2018-05-01]";
//		// 解析搜索语法
//		List<SearchBehaviorHolder> holderList = SolrQueryFactory.getHolderList(condition);
//		if (holderList == null) {
//			return;
//		}
//		if (indexDto.getFiltList() == null) {
//			indexDto.setFiltList(new ArrayList<byte[]>());
//		}
//		boolean isSec = false;
//		// 设置搜索条件
//		for (SearchBehaviorHolder holder : holderList) {
//			if (holder.getSearchCondition().indexOf("CONDITION_ERROR") != -1) {
//				indexDto.setCondition(null);
//				return;
//			}
//			if (holder.getSearchCondition().contains("sec_level")) {
//				isSec = true;
//			}
//			if (indexDto.getCondition() == null) {
//				indexDto.setCondition(holder.getSearchCondition().getBytes());
//			} else {
//				indexDto.getFiltList().add(holder.getSearchCondition().getBytes());
//			}
//		}
//		List<byte[]> filtList = indexDto.getFiltList();
//		for (byte[] bs : filtList) {
//			String ss = new String(bs);
//			try {
//				FileUtils.write(new File("C:/Users/caiwu/Desktop/yufajiexi.txt"), ss, true);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		// 返回的结果形式
//		List<String> returnFields = new ArrayList<String>();
//		returnFields.add("id");
//		returnFields.add("port");
//		returnFields.add("url");
//		returnFields.add("title_str");
//		// returnFields.add("province_code");
//		// returnFields.add("area_code");
//		// returnFields.add("city_code");
//		returnFields.add("sec_plugin_str");
//		indexDto.setReturnFields(returnFields);
//		// 返回的结果数量
//		String rownum = "100000";
//		indexDto.setRownum(Integer.parseInt(rownum));
//		try {
//			IndexResponseDto res = client.queryIndex(indexDto);
//			int i = 0;
//			for (IndexDocumentDto result : res.getIndexResult()) {
//				i++;
//				String id = result.getFieldValue("id");
//				String ip = id.split("\\|")[0];
//				String port = result.getFieldValue("port");
//				String url = result.getFieldValue("url");
//				String title_str = result.getFieldValue("title_str");
//				Map<String, Object> queryCondition = new HashMap<String, Object>();
//				queryCondition.put("id", id);
//				List<Map<String, Object>> query = cass.query("finger", queryCondition);
//				if (query.size() < 0)
//					continue;
//				Map<String, Object> map = query.get(0);
//				for (String ss : map.keySet()) {
//					if (!ss.equals("sec_plugin"))
//						continue;
//					String object = (String) map.get(ss);
//					if (SdsStringUtil.isNullOrEmpty(object))
//						continue;
//					String[] split = object.split(" ");
//					String vul_name = "";
//					String vul_desc = "";
//					String vul_level = "";
//					String vul_solution = "";
//					for (String string : split) {
//						String spl = string.replaceAll("_", "-");
//						Dradar_Vul_Nse_Map dra = (Dradar_Vul_Nse_Map) hazelcast.getMap("vul_nse_lib").get(string);
//						if (null != dra) {
//							vul_name = dra.getName();
//							vul_desc = dra.getDesc();
//							vul_level = dra.getRisk_level();
//							vul_solution = dra.getSolution();
//						} else {
//							Dradar_Vul_Nse_Map draa = (Dradar_Vul_Nse_Map) hazelcast.getMap("vul_nse_lib").get(spl);
//							if (null != draa) {
//								vul_name = draa.getName();
//								vul_desc = draa.getDesc();
//								vul_level = draa.getRisk_level();
//								vul_solution = draa.getSolution();
//							} else {
//								Vul_Box vul = (Vul_Box) hazelcast.getMap("vul_lib").get(string);
//								if (null != vul) {
//									vul_name = vul.getName();
//									vul_desc = vul.getDesc();
//									vul_level = vul.getRisk_level();
//									vul_solution = vul.getSolution();
//								}
//							}
//						}
//						if (vul_level.equals("3")) {
//							vul_level = "高危";
//						} else if (vul_level.equals("2")) {
//							vul_level = "中危";
//						} else if (vul_level.equals("1")) {
//							vul_level = "低危";
//						}
//						String rest = title_str + " \t" + ip + " \t" + port + " \t" + url + " \t" + vul_level + " \t"
//								+ vul_name + " \t" + vul_desc + " \t" + vul_solution + "\n";
//						System.out.println(rest);
//						FileUtils.write(new File("C:/Users/caiwu/Desktop/语法漏洞解析1res.txt"), rest, true);
//					}
//
//				}
//				System.out.println(res.getIndexResult().size());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
