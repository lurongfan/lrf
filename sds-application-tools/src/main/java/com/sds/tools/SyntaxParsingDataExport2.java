package com.sds.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.shadansou.sds.cassandra.api.impl.CassandraOperator;
import com.shadansou.sds.common.util.SdsDateUtil;
import com.shadansou.sds.common.util.SdsStringUtil;
import com.shadansou.sds.commoninner.area.dto.WorldAreaDto;
import com.shadansou.sds.hazelcast.api.imp.HazelcastOperationImpl;
import com.shadansou.sds.index.dto.IndexDocumentDto;
import com.shadansou.sds.index.dto.IndexRequestDto;
import com.shadansou.sds.index.dto.IndexResponseDto;
import com.shadansou.sds.searchsyntax.regular.SearchBehaviorHolder;
import com.shadansou.sds.searchsyntax.regular.SolrQueryFactory;
import com.shadansou.sds.solr.impl.SolrCloudClient;

public class SyntaxParsingDataExport2 {

	CassandraOperator cass = new CassandraOperator("172.16.111.10", "sds");
	HazelcastOperationImpl hazelcast = new HazelcastOperationImpl("SDSCaster", "SDSCaster", "172.16.110.12:5701");
	SolrCloudClient client = new SolrCloudClient("172.16.106.6:2181", "search");

	public void tools(String key) {

		IndexRequestDto indexDto = new IndexRequestDto();
		indexDto.addCollection("finger1");
		indexDto.addCollection("finger2");
		indexDto.addCollection("finger3");
		indexDto.addCollection("finger4");
		indexDto.addCollection("finger5");
		// String condition = "(homepage:七师)|(domain:nqs.gov.cn)";
		String condition = "内容:\"" + key + "\" 日期:[2018-07-09,2018-07-16] -域名:edu.cn -域名:gov.cn";

		System.out.println("condition:    " + condition);

		File file = new File("C:/Users/eefung/Desktop/result3.txt");

		List<SearchBehaviorHolder> holderList = SolrQueryFactory.getHolderList(condition);

		if (holderList == null) {
			return;
		}
		if (indexDto.getFiltList() == null) {
			indexDto.setFiltList(new ArrayList<byte[]>());
		}
		boolean isSec = false;
		// 设置搜索条件
		for (SearchBehaviorHolder holder : holderList) {
			if (holder.getSearchCondition().indexOf("CONDITION_ERROR") != -1) {
				indexDto.setCondition(null);
				return;
			}
			if (holder.getSearchCondition().contains("sec_level")) {
				isSec = true;
			}
			if (indexDto.getCondition() == null) {
				indexDto.setCondition(holder.getSearchCondition().getBytes());
			} else {
				indexDto.getFiltList().add(holder.getSearchCondition().getBytes());
			}
		}
		// indexDto.getFiltList().add(dd.getBytes());
		List<byte[]> filtList = indexDto.getFiltList();
		for (byte[] bs : filtList) {
			String ss = new String(bs);
			try {
				FileUtils.write(new File("C:/Users/eefung/Desktop/yufajiexi3.txt"), ss + "\n", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 返回的结果形式
		List<String> returnFields = new ArrayList<String>();
		returnFields.add("id");
		returnFields.add("port");
		returnFields.add("url");
		returnFields.add("title_str");
		returnFields.add("scandate");
		returnFields.add("province_code");
		returnFields.add("area_code");
		returnFields.add("city_code");
		returnFields.add("country_code");
		// returnFields.add("domain_rev");

		indexDto.setReturnFields(returnFields);
		// 返回的结果数量
		String rownum = "1000000";
		indexDto.setRownum(Integer.parseInt(rownum));
		try {
			IndexResponseDto res = client.queryIndex(indexDto);
			for (IndexDocumentDto result : res.getIndexResult()) {
				String id = result.getFieldValue("id");
				String ip = id.split("\\|")[0];
				String port = result.getFieldValue("port");
				String url = result.getFieldValue("url");
				String title_str = result.getFieldValue("title_str");
				// String domain = result.getFieldValue("domain_rev");
				String scandate = result.getFieldValue("scandate");
				String province_code = result.getFieldValue("province_code");
				String city_code = result.getFieldValue("city_code");
				String area_code = result.getFieldValue("area_code");
				String country_code = result.getFieldValue("country_code");
				String area = null;
				if (!SdsStringUtil.isNullOrEmpty(area_code)) {
					WorldAreaDto object = (WorldAreaDto) hazelcast.getMap("region_code_map").get(area_code);
					if (object != null) {
						area = object.getFullname();
					}
				} else if (!SdsStringUtil.isNullOrEmpty(city_code)) {
					WorldAreaDto object = (WorldAreaDto) hazelcast.getMap("region_code_map").get(city_code);
					if (object != null) {
						area = object.getFullname();
					}
				} else if (!SdsStringUtil.isNullOrEmpty(province_code)) {
					WorldAreaDto object = (WorldAreaDto) hazelcast.getMap("region_code_map").get(province_code);
					if (object != null) {
						area = object.getFullname();
					}
				} else if (!SdsStringUtil.isNullOrEmpty(country_code)) {
					WorldAreaDto object = (WorldAreaDto) hazelcast.getMap("region_code_map").get(country_code);
					if (object != null) {
						area = object.getFullname();
					}
				}

				// 获取备案号
				//String icp = getIcp(id);

				// String rest = url + "\t" + icp + "\n";
				scandate = SdsDateUtil.long2String(Long.parseLong(scandate),
						SdsDateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);

				String rest = title_str + " \t" + url + " \t" + ip + "\n";
				System.out.println(rest);
				FileUtils.write(file, rest, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
		System.out.println("查询完毕");
	}

	/* 获取备案号 */
	public String getIcp(String id) {
		String icp = null;
		Map<String, Object> queryCondition = new HashMap<String, Object>();
		queryCondition.put("id", id);
		List<Map<String, Object>> query = cass.query("finger", queryCondition);
		if (query.size() > 0) {
			Map<String, Object> map = query.get(0);
			for (String ss : map.keySet()) {
				Object object = map.get(ss);
				if (object == null)
					continue;
				String[] split = object.toString().split("\n");
				for (String string : split) {
					if (string.startsWith("{") && !string.equals("{}") && string.contains("icp=")) {
						// System.out.println(string);
						String[] split2 = string.split("[,;]");
						for (String string2 : split2) {
							if (!string2.trim().startsWith("icp="))
								continue;
							icp = string2.trim().substring(4, string2.trim().length());
							// System.out.println(icp);
						}
					}
				}
			}
		}

		if (icp != null) {
			icp = icp.trim();
		}
		return icp;
	}
	
	public static void main(String[] args) {

		File file = new File("C:\\Users\\eefung\\Desktop\\兵团关键字2.txt");
		BufferedReader reader = null;
		SyntaxParsingDataExport2 s = new SyntaxParsingDataExport2();
		try {
			reader = new BufferedReader(new FileReader(file));
			String key = null;
			while ((key = reader.readLine()) != null) {
				System.out.println("key" + key);
				s.tools(key);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("所有数据查询完毕");
	}

}
