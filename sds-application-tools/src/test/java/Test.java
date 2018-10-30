import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;

public class Test {
	private BufferedReader reader = null;
	public static void main(String[] args) throws UnknownHostException {
	//System.out.println(InetAddress.getLocalHost().getHostAddress());	
	
	//System.out.println(getWebIP());
	//System.out.println(new Date());
		Test t = new Test();
		t.test();
	}
	public static String getWebIP() {
		String ip = null;

		// 根据网卡取本机配置的IP
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				// 业务代码部署在eth0上
				while (ips.hasMoreElements() ) {	
				
						
	                    System.out.println("网卡接口名称：" + ni.getName());
	                    System.out.println("网卡接口地址：" +  ips.nextElement().getHostAddress());
	                    System.out.println();
	                
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	public  void test() {
		int count=0;
		File file = new File("C:\\Users\\eefung\\Desktop\\fv\\webcommand17.txt");
		try {
			reader = new BufferedReader(new FileReader(file));
			String line =null;
			while ((line = reader.readLine()) != null) {
				count++;
				if(!line.startsWith("http")){
					
					System.out.println(line);
					System.out.println(count);
				}
				//System.out.println(count);
			}
			System.out.println("结束");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
	}
	
	
}

