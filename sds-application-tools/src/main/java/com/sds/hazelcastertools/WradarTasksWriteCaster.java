package com.sds.hazelcastertools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.FileUtils;

import com.shadansou.sds.common.rest.dto.webradar.WradarTaskActiveIPInfoDto;
import com.shadansou.sds.hazelcast.api.imp.HazelcastOperationImpl;

public class WradarTasksWriteCaster {
	public static void main(String[] args) {
		HazelcastOperationImpl hazelcast = new HazelcastOperationImpl("SDSCaster", "SDSCaster", "172.16.110.12:5701");
		try {
			List<String> readLines = FileUtils.readLines(new File("C:/Users/caiwu/Desktop/127.0.0.1.txt"));
			for (String string : readLines) {
				String url=string.split(" ")[0].trim();
				String ip =string.split(" ")[1].trim();
				String port =string.split(" ")[2].trim();
				WradarTaskActiveIPInfoDto dto = new WradarTaskActiveIPInfoDto();
				System.out.println(ip+url+port);
				dto.setIp(ip);
				dto.setUrl(url);
				dto.setPort(Integer.parseInt(port));
				dto.setAttachInfo("scan_type:w;sch_style:sch_special;scan_end:w");
				dto.setProcotol("tcp");
				dto.setPriority(0);
				Queue<WradarTaskActiveIPInfoDto> queue = hazelcast.getQueue("web_special_queue");
				queue.add(dto);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
