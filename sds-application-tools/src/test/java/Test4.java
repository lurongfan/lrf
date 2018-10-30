import com.shadansou.sds.common.util.IPDomainUtil;

public class Test4 {
	
	public static void main(String[] args) {
		//String s = "null		wangzhanhong		2018-08-16 23:00:06	2018-10-11 09:07:43	3";
		String s = "name:华南光电,二厂 url:http://dt119index.aspx group: 一组";
		String[] ss = s.split(" ");
		System.out.println(ss.length);
		for(String e:ss){
			System.out.println(e);
		}
 	}

//	public static void main(String[] args) {
//	System.out.println(IPDomainUtil.getHost("http://www.qthzyxy.com"));	
//	System.out.println(IPDomainUtil.getPortByUrl("http://www.qthzyxy.com"));
//	System.out.println(IPDomainUtil.getDomain("http://www.qthzyxy.com"));
//	}
}
