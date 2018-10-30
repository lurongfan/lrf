package com.sds.hazelcastertools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.shadansou.sds.index.dto.IndexDocumentDto;
import com.shadansou.sds.index.dto.IndexRequestDto;
import com.shadansou.sds.index.dto.IndexResponseDto;
import com.shadansou.sds.solr.impl.SolrCloudClient;

public class CheckSurpluseSolr {

	private static Cluster cluster = null;

	private static int num = 0;
	
	private String solrurl = "172.16.106.6:2181";
	private String cbaseurl = "172.16.112.10";

	private SolrCloudClient client = new SolrCloudClient(solrurl, "search");
	private SolrCloudClient c1 = new SolrCloudClient(solrurl, "finger1");
	private SolrCloudClient c2 = new SolrCloudClient(solrurl, "finger2");
	private SolrCloudClient c3 = new SolrCloudClient(solrurl, "finger3");
	private SolrCloudClient c4 = new SolrCloudClient(solrurl, "finger4");
	private SolrCloudClient c5 = new SolrCloudClient(solrurl, "finger5");
	private String ip;
	// solr,cbase中都有数据
	private String filename1 = "C:\\Users\\eefung\\Desktop\\id1.txt";
	// solr,cbase中都没有数据
	private String filename2 = "C:\\Users\\eefung\\Desktop\\id2.txt";
	// solr中有，cbase没有
	private String filename3 = "C:\\Users\\eefung\\Desktop\\id3.txt";
	// solr中没有，cbase有
	private String filename4 = "C:\\Users\\eefung\\Desktop\\id4.txt";

	private BufferedWriter writer = null;

	private Boolean solr;
	private Boolean cbase;

	private Long ip1 = ipToLong("1.0.0.0");
	private Long ip2 = ipToLong("60.255.255.255");
	private Long ip3 = ipToLong("61.0.0.0");
	private Long ip4 = ipToLong("90.255.255.255");
	private Long ip5 = ipToLong("91.0.0.0");
	private Long ip6 = ipToLong("120.255.255.255");
	private Long ip7 = ipToLong("121.0.0.0");
	private Long ip8 = ipToLong("185.255.255.255");
	private Long ip9 = ipToLong("186.0.0.0");
	private Long ip10 = ipToLong("255.255.255.255");

	private List<String> idlist = new ArrayList<String>();

	public Boolean checkSolr(String id) {

		IndexRequestDto indexDto = new IndexRequestDto();
		indexDto.addCollection("finger1");
		indexDto.addCollection("finger2");
		indexDto.addCollection("finger3");
		indexDto.addCollection("finger4");
		indexDto.addCollection("finger5");

		String condition = "id:" + id;
		indexDto.setCondition(condition.getBytes());

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

		indexDto.setReturnFields(returnFields);

		String rownum = "100000";
		indexDto.setRownum(Integer.parseInt(rownum));

		try {
			IndexResponseDto res = client.queryIndex(indexDto);
			List<IndexDocumentDto> list = res.getIndexResult();
			for (IndexDocumentDto result : list) {
				String id2 = result.getFieldValue("id");
				String ip2 = id2.split("\\|")[0];
				String port = result.getFieldValue("port");
				String url = result.getFieldValue("url");
				String title_str = result.getFieldValue("title_str");
				// String domain = result.getFieldValue("domain_rev");
				// String scandate = result.getFieldValue("scandate");
				// String province_code = result.getFieldValue("province_code");
				// String city_code = result.getFieldValue("city_code");
				// String area_code = result.getFieldValue("area_code");
				// String country_code = result.getFieldValue("country_code");
				String rest = title_str + " \t" + url + " \t" + ip + " \t" + port + "\n";
				System.out.println(rest);

				ip = ip2;
			}
			if (list.size() > 0) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Boolean checkCbass(String id) {

		String cid = null;
		cluster = Cluster.builder().addContactPoints(cbaseurl).withPort(9042).build();
		Session session = cluster.connect();
		String cql = "select * from sds.finger where id = '" + id + "'";
		ResultSet resultSet = session.execute(cql);

		for (Row row : resultSet) {
			cid = row.getString("id");
			System.out.println(row.getString("title"));
			System.out.println(cid);
		}
		if (!StringUtils.isEmpty(cid)) {
			return true;
		}
		return false;
	}

	public Boolean check(String id) {

		solr = checkSolr(id);
		cbase = checkCbass(id);

		if (solr == true && cbase == true) {
			writerInText(filename1, id);
			return true;
		}
		if (solr == false && cbase == false) {
			writerInText(filename2, id);
			return true;
		}

		if (solr == false && cbase == true) {
			writerInText(filename4, id);
		}

		writerInText(filename3, id);
		deleteById(id);
		num++;
		return false;
	}

	/* 将id写入对应的文件 */
	public void writerInText(String filename, String id) {

		try {
			FileWriter fw = new FileWriter(filename, true);
			writer = new BufferedWriter(fw);
			writer.write(id);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteById(String id) {

		if (idlist.size() != 0) {
			idlist.clear();
		}
		idlist.add(id);

		Long ipnum = ipToLong(ip);
		System.out.println(ipnum);

		if (ipnum >= ip1 && ipnum <= ip2) {
			c1.deleteIndexById(idlist);
		} else if (ipnum >= ip3 && ipnum <= ip4) {
			c2.deleteIndexById(idlist);
		} else if (ipnum >= ip5 && ipnum <= ip6) {
			c3.deleteIndexById(idlist);
		} else if (ipnum >= ip7 && ipnum <= ip8) {
			c4.deleteIndexById(idlist);
		} else if (ipnum >= ip9 && ipnum <= ip10) {
			c5.deleteIndexById(idlist);
		}
	}

	public long ipToLong(String iP) {
		String[] ipArr = iP.split("\\.");
		long result = 0;
		for (int i = 0; i < ipArr.length; i++) {
			int power = ipArr.length - 1 - i;
			int ip = Integer.parseInt(ipArr[i]);
			result += ip * Math.pow(256, power);
		}
		return result;
	}

	public static void main(String[] args) {

		File file = new File("C:\\Users\\eefung\\Desktop\\id.txt");
		BufferedReader reader = null;

		CheckSurpluseSolr t = new CheckSurpluseSolr();

		try {
			reader = new BufferedReader(new FileReader(file));
			String id = null;
			int line = 1;
			Boolean result;
			while ((id = reader.readLine()) != null) {
				result = t.check(id);
				System.out.println(result);
				System.out.println("line " + line + ": " + id);
				line++;
			}
			System.out.println("删除个数" + num);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					t.writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
