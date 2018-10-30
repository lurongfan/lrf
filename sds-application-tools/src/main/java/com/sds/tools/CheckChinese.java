package com.sds.tools;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.shadansou.sds.hazelcast.api.imp.HazelcastOperationImpl;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class CheckChinese {

	public static void main(String[] args) {

		try {
			File file = new File("C:\\Users\\eefung\\Desktop\\vul_lib6.txt");

			HazelcastOperationImpl hazelcast = new HazelcastOperationImpl("SDSCaster", "SDSCaster",
					"172.16.110.12:5701");
			Map map = hazelcast.getMap("vul_lib");
			for (Object o : map.keySet()) {
				JsonConfig conf = new JsonConfig();
				conf.setAllowNonStringKeys(true);
				JSONObject json = JSONObject.fromObject(map.get(o), conf);
				Map d = (Map) json.get("desc");
				String nameEn = d.get("name_en").toString();
				if (isContainChinese(nameEn)) {
					String result = o + ":  " + nameEn + "\n";
					if (!o.toString().startsWith("self")) {
						FileUtils.write(file, result, true);
					}
				}
			}
			System.out.println("结束");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
}
