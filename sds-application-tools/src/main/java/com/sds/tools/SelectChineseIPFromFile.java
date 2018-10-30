package com.sds.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shadansou.sds.avro.rest.client.AreaInfoUtils;
import com.shadansou.sds.avro.rest.client.SdsRestClientUtil;
import com.shadansou.sds.common.constants.CommonConstants;
import com.shadansou.sds.common.util.SdsStringUtil;
import com.shadansou.sds.common.util.json.JsonConverter;
import com.shadansou.sds.commoninner.constants.CacheUniformName;
import com.shadansou.sds.commoninner.constants.MongodbNames;
import com.shadansou.sds.commoninner.po.Ip_Detail;
import com.shadansou.sds.commoninner.util.IPDetailUtil;
import com.shadansou.sds.configagent.dto.HazelcastConfigResponseDto;
import com.shadansou.sds.configagent.dto.MongodbConfigResponseDto;
import com.shadansou.sds.configagent.facade.ConfigProxy;
import com.shadansou.sds.configagent.util.PathUtil;

import com.shadansou.sds.hazelcast.api.IHazelcastOperation;
import com.shadansou.sds.hazelcast.api.imp.HazelcastOperationImpl;
import com.shadansou.sds.mongodb.api.IMongodbOperation;
import com.shadansou.sds.mongodb.api.impl.MongodbOperationImpl;

public class SelectChineseIPFromFile {
	private static final Log logger = LogFactory.getLog(SelectChineseIPFromFile.class);
	private static IHazelcastOperation ihazelCast = null;
	private IMongodbOperation mongoPublic;
	private BufferedReader read = null;
	private FileWriter write = null;

	public void start() {

		initConfitg();
		Config conf = loadProperties();
		if (SdsStringUtil.isNullOrEmpty(conf.getSplitFile()) || SdsStringUtil.isNullOrEmpty(conf.getResultFile())
				|| SdsStringUtil.isNullOrEmpty(conf.getPrefixStr()) || SdsStringUtil.isNullOrEmpty(conf.getEndStr())) {
			logger.error("配置文件有误");
			logger.info(JsonConverter.obj2Json(conf));
			return;
		}
		String line = null;
		try {
			int count = 0;
			int totle_num = 0;
			write = new FileWriter(conf.getResultFile());
			read = new BufferedReader(new FileReader(conf.getSplitFile()));
			String prefix = conf.getPrefixStr() + " ";
			String end = " " + conf.getEndStr();
			Thread.currentThread().setContextClassLoader(Ip_Detail.class.getClassLoader());
			while ((line = read.readLine()) != null) {
				count++;
				totle_num++;
				String[] strs = line.split(" ");
				if (strs.length < 3) {
					continue;
				}
				String ip = strs[3];
				Ip_Detail info = IPDetailUtil.searchDetail(ip);
				if (info != null) {
					String country_code = info.getCountry_code();
					if (!SdsStringUtil.isNullOrEmpty(country_code) && (country_code.equals("CN"))) {

						write.write(prefix + ip + end + "\r\n");
					}
				}
				if (count % 500 == 0) {
					write.flush();
					logger.info(totle_num);
				}
			}
		} catch (Exception e) {
			logger.error(line);
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (write != null) {
					write.close();
				}
				if (read != null) {
					read.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void stop() {
		try {
			if (write != null) {
				write.close();
			}
			if (read != null) {
				read.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (mongoPublic != null) {
			mongoPublic.close();
		}
		if (ihazelCast != null) {
			ihazelCast.shutdown();
		}
	}

	public void initConfitg() {
		PathUtil.setCoordinate_url("http://biz100.sds.cc:8181/cxf/coordinate");

		MongodbConfigResponseDto mongodto = ConfigProxy.getMongodbCfg();
		if (null == mongodto) {
			logger.error("SelectDataByCountryFromFile： 获取mdb业务数据库配置失败");
			return;
		}

		MongodbConfigResponseDto mongodto_user = ConfigProxy.getMongodbUserCfg();
		if (null == mongodto_user) {
			logger.error("SelectDataByCountryFromFile： 获取mdb用户数据库配置失败");
			return;
		}

		mongoPublic = new MongodbOperationImpl(mongodto.getUsername(), mongodto.getPassword(), mongodto.getUrl(),
				MongodbNames.SDS_PUBLIC);

		if (ihazelCast == null) {
			HazelcastConfigResponseDto hazelcastdto = ConfigProxy.getHazelCastCfg();
			ihazelCast = new HazelcastOperationImpl(hazelcastdto.getUsername(), hazelcastdto.getPassword(),
					hazelcastdto.getURL());
		}

		IPDetailUtil.initalIpDetails(ihazelCast, getZkVersion(CommonConstants.IPDETAIL_VERSION), mongoPublic);
		;
	}

	public String getZkVersion(String type) {
		return ConfigProxy.getDynamicCfg(type).getValue();
	}

	private Config loadProperties() {
		Config config = new Config();
		Properties property = new Properties();
		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream("/config.properties");
			property.load(in);

			config.setSplitFile(property.getProperty("split_file"));
			config.setResultFile(property.getProperty("result_file"));
			config.setPrefixStr(property.getProperty("prefix_str"));
			config.setEndStr(property.getProperty("end_str"));
			return config;
		} catch (FileNotFoundException e) {
			logger.error("找不到config.properties配置文件!", e);
			return null;
		} catch (IOException e) {
			logger.error("加载配置文件config.properties失败!", e);
			return null;
		}
	}

	public static void main(String[] args) {
		File file = new File("C:/Users/eefung/Desktop/result11.txt");
		File file2 = new File("C:/Users/eefung/Desktop/result12.txt");
		// SelectChineseIPFromFile s = new SelectChineseIPFromFile();
		// s.start();
		// System.out.println("结束");
		PathUtil.setCoordinate_url("http://biz100.sds.cc:8181/cxf/coordinate");
		
		if (ihazelCast == null) {
			HazelcastConfigResponseDto hazelcastdto = ConfigProxy.getHazelCastCfg();
			ihazelCast = new HazelcastOperationImpl(hazelcastdto.getUsername(), hazelcastdto.getPassword(),
					hazelcastdto.getURL());
		}
		System.out.println("开始");
		//List<String> hz_ipdetail_world = ihazelCast.getList(CacheUniformName.IP_DETAIL_WORLD_KEYINLIST);
		Map<String, Ip_Detail>	maps_world = ihazelCast.getMap(CacheUniformName.IP_DETAIL_WORLD);
//		for(String s:hz_ipdetail_world ){
//			try {
//				FileUtils.write(file, s+"\n", true);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		for(String key:maps_world.keySet()){
			String s ="key:"+key+" "+"value:"+maps_world.get(key).toString();
			try {
				FileUtils.write(file2, s+"\n", true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		System.out.println("结束");
		
	}

	// public static void main(String[] args) {
	// SdsRestClientUtil client = new SdsRestClientUtil();
	// AreaInfoUtils areaUtils = new AreaInfoUtils(client );
	// String info = areaUtils.getAreaInfo("185.244.94.217");
	// if (info != null && !info.equals("null")) {
	// JSONObject json = JSONObject.fromObject(info);
	// String country_code = json.getString("country_code");
	// if (country_code.equals("US") || country_code.equals("CN")) {
	// System.out.println(country_code);
	// }
	// }
	// }
}
