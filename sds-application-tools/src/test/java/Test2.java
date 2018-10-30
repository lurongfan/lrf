import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Test2 {
	
	public static void main(String[] args) {
//		System.out.println("=========指定目录下的所有文件夹==========");
//		File fileDirectory = new File("E:\\");
//		for (File temp : fileDirectory.listFiles()) {
//			if (temp.isDirectory()) {
//				System.out.println(temp.toString());
//			}
//		}
//		System.out.println("=========指定目录下的所有文件==========");
//		File file = new File("C:\\Users\\eefung\\Documents\\im");
//		File[] aa = file.listFiles();
//		for (int i = 0; i < aa.length; i++) {
//			if (aa[i].isFile()) {
//				System.out.println(aa[i]);
//			}
//		}
		int count=0;
		File file = new File("C:\\Users\\eefung\\Desktop\\");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				count++;
				if(!line.startsWith("http")||line.contains("scan_end:w")){
					System.out.println(count);
				}
				
				
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
