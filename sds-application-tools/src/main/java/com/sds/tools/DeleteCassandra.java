package com.sds.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.shadansou.sds.cassandra.api.impl.CassandraOperator;

public class DeleteCassandra {
	public static void main(String[] args) {
		CassandraOperator cass = new CassandraOperator("172.16.111.10", "sds");
		List<String> readLines;
		try {
			List<Object> list = new ArrayList<Object>();
			readLines = FileUtils.readLines(new File("C:/Users/caiwu/Desktop/127id.txt"));
			for (String string : readLines) {
				Object id = string;
				list.add(id);
			}
				cass.deleteById("finger", list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
