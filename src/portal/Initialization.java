// 2009-06-04 
// HWStatistic, LicenseStatistic, PeraLS, ThreeDES, Portal.
package portal;
import javax.servlet.http.*;
import java.io.*;
import java.util.Date;
import portal.Utility;
/*
	public static String fetch( String filePathName, String name )	
*/
public class Initialization{

	public Initialization(){
	}
	
	public static String fetch( String filePathName, String name )
	{
		String value = new String("NULL");
		if( name == null ) return value;
		if( filePathName== null)return value;
		String line = new String();
		RandomAccessFile file = null;
		try{
			file = new RandomAccessFile( filePathName , "r" );
			file.seek(0);
			while((line = file.readLine ()) != null)
			{
				if(Utility.getWordsAt(line,1).equals(name)){
					value = line.substring(line.indexOf(Utility.getWordsAt(line,2)), line.length());
				}
			}
			file.close();
		}catch(IOException e){ 
			System.out.println("public static String fetch( String filePathName, String name )"+e);
			e.printStackTrace();
		}
		return value;
    }
	
	public static void main(String args[])
	{	
		System.out.println(Initialization.fetch("/opt/tomcat/webapps/portal/parameters",args[0]));
	}
}
