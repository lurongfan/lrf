package com.sds.tools.lu;

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bson.Document;

import com.shadansou.sds.mongodb.api.IMongodbOperation;
import com.shadansou.sds.mongodb.api.impl.MongodbOperationImpl;

public class CopyToTest {
	
	private static IMongodbOperation imongo = new MongodbOperationImpl(null, null, "172.16.108.10:27017",
			"security_oauth_v2");
	private static IMongodbOperation imongo2 = new MongodbOperationImpl(null, null, "172.16.110.103:27017",
			"security_oauth_v2");
	
	public static void main(String[] args) {
		
		List<Document> list = imongo.findAll("user");
		for (Document d : list) {
			imongo2.insert("user", d);
			}
		System.out.println("结束");
	}
}
