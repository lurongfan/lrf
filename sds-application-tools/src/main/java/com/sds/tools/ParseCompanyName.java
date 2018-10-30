package com.sds.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.io.FileUtils;

import com.shadansou.sds.cassandra.api.impl.CassandraOperator;
import com.shadansou.sds.common.rest.dto.webradar.WradarTaskActiveIPInfoDto;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ParseCompanyName {
	public static void main1(String[] args) {
		try {
			List<String> readLines = FileUtils.readLines(new File("C:/Users/caiwu/Desktop/新疆/北屯.txt"), "UTF-8");
			String colls = "";
			for (String string : readLines) {
				colls = string;
			}
			JSONObject json = JSONObject.fromObject(colls);
			String result = json.getString("模糊搜索结果");
			JSONArray array = JSONArray.fromObject(result);
			for (Object object : array) {
				JSONObject jsons = JSONObject.fromObject(object);
				if (!jsons.containsKey("company_name")) {
					continue;
				}
				String string = jsons.getString("company_name");
				if (string.contains("(") && !string.contains(")")) {
					string = string.substring(0, string.indexOf("("));
				}
				if (string.contains("（") && !string.contains("）")) {
					string = string.substring(0, string.indexOf("（"));
				}
				FileUtils.write(new File("C:/Users/caiwu/Desktop/新疆/北屯companyname.txt"), string + "\n", true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main2(String[] args) {
		SolrCloudClient client = new SolrCloudClient("172.16.106.6:2181", "search");
		try {
			File file = new File("C:/Users/caiwu/Desktop/新疆/昆玉result.txt");
			List<String> lines = FileUtils.readLines(new File("C:/Users/caiwu/Desktop/新疆/昆玉companyname.txt"));
			for (String string : lines) {
				IndexRequestDto indexDto = new IndexRequestDto();
				indexDto.addCollection("finger1");
				indexDto.addCollection("finger2");
				indexDto.addCollection("finger3");
				indexDto.addCollection("finger4");
				indexDto.addCollection("finger5");
				String condition = string;
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
				// 返回的结果形式
				List<String> returnFields = new ArrayList<String>();
				returnFields.add("id");
				returnFields.add("port");
				returnFields.add("url");
				returnFields.add("title_str");
				returnFields.add("scandate");
				indexDto.setReturnFields(returnFields);
				// 返回的结果数量
				String rownum = "100";
				indexDto.setRownum(Integer.parseInt(rownum));
				IndexResponseDto res = client.queryIndex(indexDto);
				String results = "公司:" + string + "\n";
				String re = "公司:" + string;
				for (IndexDocumentDto result : res.getIndexResult()) {
					String id = result.getFieldValue("id");
					String ip = id.split("\\|")[0];
					String port = result.getFieldValue("port");
					String url = result.getFieldValue("url");
					String title_str = result.getFieldValue("title_str");
					String scandate = result.getFieldValue("scandate");
					long startTime = SdsDateUtil.string2long("2018-01-01 00:00:00",
							SdsDateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
					if (Long.parseLong(scandate) > startTime) {
						results = results + "\t\t" + title_str + "\t" + url + "\n";
					}
				}
				if (res.getIndexResult().size() == 0) {
					results = re + "\t 空 \n";
				}
				FileUtils.write(file, results + "\n", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ParseCompanyName parse = new ParseCompanyName(); 
		try {
			String path = "C:/Users/caiwu/Desktop/新疆result";
			List<String> allFileNames = parse.getAllFileNames(path);
			for (String name : allFileNames) {
				List<String> readLines = FileUtils.readLines(new File(path + "/" + name));
				for (int i=0;i<readLines.size();i++) {
					if(i<readLines.size()-1){
					if(readLines.get(i).trim().startsWith("公司") && readLines.get(i+1).isEmpty()){
						i++;
						continue;
					}
					if(readLines.get(i).trim().startsWith("公司") && readLines.get(i).trim().endsWith("空")){
						continue;
					}
					if(readLines.get(i).trim().startsWith("公司") && readLines.get(i+1).trim().contains("【汕.cn】")){
						continue;
					}
					if(readLines.get(i).trim().contains("【汕.cn】")){
						continue;
					}
					if(readLines.get(i).trim().isEmpty()){
						continue;
					}
					if(readLines.get(i).startsWith("公司")){
						FileUtils.write(new File(path + "s/" + name), "\n", true);
					}
					FileUtils.write(new File(path + "s/" + name), readLines.get(i)+"\n", true);
					}else{
						FileUtils.write(new File(path + "s/" + name), readLines.get(i)+"\n", true);	
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件夹下的所有文件名字
	 * 
	 * @param fileName
	 * @return
	 */
	public List<String> getAllFileNames(String fileName) {
		List<String> list = new ArrayList<String>();
		File file = new File(fileName);
		if (file.exists()) {
			if (file.isFile()) {
				list.add(file.getName());
			} else {
				File[] listFiles = file.listFiles();
				for (File file2 : listFiles) {
					list.add(file2.getName());
				}
			}
		} else {
			System.out.println("路径下的文件or文件夹不存在！！！");
		}
		return list;
	}
}
