package com.sds.tools;

import java.io.File;
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

/**
 * 统计地区ip生成的任务被采集器采集到的结果数
 * 
 * @author wyb
 * @since 2018-04-25
 */
public class RegionIpTongJi {
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
		String codes = "";
		try {
			List<String> readLines = FileUtils.readLines(new File("C:/Users/caiwu/Desktop/web_ip.txt"));
			int i = 0;
			for (String string : readLines) {
				if (i == readLines.size() - 1) {
					codes = codes + string;
				} else {
					codes = codes + string + " OR ";
				}
				i++;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		long date = SdsDateUtil.string2long("2018-07-06", SdsDateUtil.DATE_FORMAT_YYYY_MM_DD);
		String condition = "scandate:[" + date + " TO " + System.currentTimeMillis() + "]";
		System.out.println(condition);
		indexDto.setCondition(condition.getBytes());// 时间的范围起始时间+截止时间
		// 设置搜索条件
		List<byte[]> filtList = new ArrayList<byte[]>();
		String type = "info_type:web";
		filtList.add(type.getBytes());
		filtList.add(codes.getBytes());
		indexDto.setFiltList(filtList);
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
			System.out.println("搜索到的结果" + res.getIndexResult().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
