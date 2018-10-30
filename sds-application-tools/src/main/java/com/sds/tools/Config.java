package com.sds.tools;

public class Config {
	private String protocol;
	private String file;
	private String topic;
	private String count;
	private String port;
	private String sch_style;
	private String time;
	private String collection;
	private String scan_end;
	
	private String splitFile;
	private String resultFile;
	private String prefixStr;
	private String endStr;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSch_style() {
		return sch_style;
	}

	public void setSch_style(String sch_style) {
		this.sch_style = sch_style;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public synchronized String getScan_end() {
		return scan_end;
	}

	public synchronized void setScan_end(String scan_end) {
		this.scan_end = scan_end;
	}

	public String getSplitFile() {
		return splitFile;
	}

	public void setSplitFile(String splitFile) {
		this.splitFile = splitFile;
	}

	public String getResultFile() {
		return resultFile;
	}

	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}

	public String getPrefixStr() {
		return prefixStr;
	}

	public void setPrefixStr(String prefixStr) {
		this.prefixStr = prefixStr;
	}

	public String getEndStr() {
		return endStr;
	}

	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}

}
