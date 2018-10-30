package com.sds.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import com.shadansou.sds.cassandra.api.impl.CassandraOperator;
import com.shadansou.sds.common.util.IPDomainUtil;
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

/**
 * 通过销售那边给的语法，进行解析从而导出他们需要的数据到固定文件
 * 
 * @author wyb
 * @since 2018-04-25
 */
public class SyntaxParsingDataExport {
	public static void main(String[] args) {
		CassandraOperator cass = new CassandraOperator("172.16.111.10", "sds");
		HazelcastOperationImpl hazelcast = new HazelcastOperationImpl("SDSCaster", "SDSCaster", "172.16.110.12:5701");
		SolrCloudClient client = new SolrCloudClient("172.16.106.6:2181", "search");
		// List<String> list = new ArrayList<String>();
		// list.add("2018-03");
		// list.add("2018-04");
		// list.add("2018-05");
		// list.add("2018-06");
		// list.add("[2018-07-01,2018-07-03]");
		// list.add("[2018-07-04,2018-07-11]");
		// for (String str : list) {
		IndexRequestDto indexDto = new IndexRequestDto();
		indexDto.addCollection("finger1");
		indexDto.addCollection("finger2");
		indexDto.addCollection("finger3");
		indexDto.addCollection("finger4");
		indexDto.addCollection("finger5");
		// String condition = "(homepage:七师)|(domain:nqs.gov.cn)";
		String condition = "内容:轮台 -domain:tuliu.com|gov.cn|huatu.com|bendibao.com|xjxnw.com|xjxnw.com.cn -标题：兵团|自治区";

		System.out.println("condition:    " + condition);

		File file = new File("C:/Users/lurongfan/Desktop/luntai2.txt");
		// 解析搜索语法
		// String dd="city_code:652700 OR
		// monitor_uid_dynamic:ab88dde9-a849-45a2-b215-c466e91c0553 OR
		// monitor_uid_dynamic:dab57b48-47d3-4abc-b680-682184986662 OR
		// monitor_area_dynamic:652700";

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
				FileUtils.write(new File("C:/Users/lurongfan/Desktop/yufajiexi.txt"), ss + "\n", true);
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

				/* 取 在dynamic_field 字段中的值 */

				String isp = null;
				String police_record_num = null;
				String icp = null;
				String contact = null;
				String child_domain = null;
				String sec_level = null;
				Map<String, Object> queryCondition = new HashMap<String, Object>();
				queryCondition.put("id", id);
				List<Map<String, Object>> query = cass.query("finger", queryCondition);
				if (query.size() > 0) {
					Map<String, Object> map = query.get(0);
					sec_level = map.get("sec_level")+"";		
					Map<String, Object> dynamic_field = (Map<String, Object>) map.get("dynamic_field");
					Object is = dynamic_field.get("isp");
					Object p = dynamic_field.get("police_record_num");
					Object ic = dynamic_field.get("icp");
					Object c = dynamic_field.get("contact");
					Object d = dynamic_field.get("child_domain");

					isp = is + "";
					police_record_num = p + "";
					icp = ic + "";
					contact = c + "";
					child_domain = d + "";
				}

				scandate = SdsDateUtil.long2String(Long.parseLong(scandate),
						SdsDateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);

				String rest = title_str + " \t" 
				+ url + " \t" 
				+ port+ " \t" 
				+ icp + " \t" 
				+ scandate + "\n";

				System.out.println(rest);
				
			
					FileUtils.write(file, rest, true);
				
			}
			System.out.println("查询完毕");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
	}
}
