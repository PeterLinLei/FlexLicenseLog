// 
// GridLS, LicenseStatistic, Initialization, Portal.
package portal; 
import java.io.*;
import java.sql.*;

/*
	public static String rm( String fileName )
	public static String submitToSGE( String commandFileName )
	public static String fetch( String dbDriver, String dbUrl, String dbUser, String dbPassword, String condition, String table, String column)
	public static String makeDir( String jobDir, String user, String userGroup )
	public static String dos2unix( String fileName )
	public static String getWordsAt(String line,int index)
	public static int execute( String dbDriver, String dbUrl, String dbUser,String dbPassword,String statement)	
*/
public class Utility{

	public Utility(){
	}

	public static boolean createFile(String file,String filePath)
	{
		//write file
		RandomAccessFile f = null;
		try{
			File f2 = new File(filePath);
			if (f2.exists()) {
				f2.delete();
				f2.createNewFile();
			}	
			f = new RandomAccessFile(f2,"rw");
			f.writeBytes(file);
			f.close();
		}catch(IOException e){ 
			System.out.println(e);
			return false;	
		}
		return true;
	}
	
	// execute INSERT,UPDATE or DELETE statement. 
	public static int executeSQL( String dbDriver, String dbUrl, String dbUser,String dbPassword,String statement)
	{
		int executeRecordNumber = -1;
		try{
			Connection conn1;
			Statement stmt1;	
			Class.forName(dbDriver);
			conn1 = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
			stmt1 = conn1.createStatement();
			executeRecordNumber = stmt1.executeUpdate(statement);
			stmt1.close();
			conn1.close();	
		}	
		catch(Exception e)
		{
			System.out.println("public static int execute( String dbDriver, String dbUrl, String dbUser,String dbPassword,String statement)");
			System.out.println(e);
		}
		return executeRecordNumber;		
	}

	// fetch column from table where condition. 
	public static String[][] selectSQL( String dbDriver, String dbUrl, String dbUser, String dbPassword, String condition, String table, String column)
	{
		String result[][] = new String[512][16];
		int columnNumber = 0;
		int rowNumber = 0;
		if(!column.equals(""))
		{
			for(int i=0;i<column.length();i++)
			{
				if(column.charAt(i)==',')
					columnNumber ++;
			}
			columnNumber ++;
			result[0][1] = String.valueOf(columnNumber);
		}
		else{ return result;}
		try{
			Connection conn1;
			Statement stmt1;	
			ResultSet rs1;
			Class.forName(dbDriver);
			conn1 = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
			String fetch_column = new String();
			if(condition.equals(""))
				fetch_column = "select "+column+" from "+table;
			else
				fetch_column = "select "+column+" from "+table+" "+condition;
			System.out.println(fetch_column);
			stmt1 = conn1.createStatement();
			rs1 = stmt1.executeQuery(fetch_column);
			rowNumber = 1;
			while(rs1.next() && rowNumber<510)
			{
				for(int i=0;i<columnNumber;i++)
				{
					result[rowNumber][i] = rs1.getString( i+1 );
				}
				rowNumber++;
			}
			result[0][0] = String.valueOf( rowNumber-1 );
			rs1.close();
			stmt1.close();
			conn1.close();	
		}	
		catch(Exception e)
		{
		}
		return result;		
	}
	
	// remove file
	public static String rm( String fileName )
	{
		try{
			Process process = Runtime.getRuntime().exec("rm -rf " + fileName);
			InputStreamReader ir=new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(ir);
			String line = br.readLine();
			if(line!=null)
				return "fail";
		}catch(IOException e){
			System.err.println("IOException " + e.getMessage());
			return "fail";
		}
		return "success"; 			
	}
	
	// Submit to SGE. 
	public static String submitToSGE( String commandFileName )
	{
		String jobId = new String("NULL"); 
		try{
			Runtime.getRuntime().exec("chmod +x "+commandFileName);
			Process process = Runtime.getRuntime().exec(commandFileName);
			InputStreamReader ir=new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(ir);
			String line = br.readLine();
			if(line==null)
				return "NULL";
			jobId=Utility.getWordsAt(line,3);
		}catch(IOException e){
			System.err.println("IOException " + e.getMessage());
			return "NULL";
		}
		return jobId; 	
	}

	// fetch column from table where condition. 
	public static String fetch( String dbDriver, String dbUrl, String dbUser, String dbPassword, String condition, String table, String column)
	{
		String result = new String("null");
		try{
			Connection conn1;
			Statement stmt1;	
			ResultSet rs1; 
			Class.forName(dbDriver);
			conn1 = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
			String fetch_column = "select "+column+" from "+table+" where "+condition;
			System.out.println(fetch_column);
			stmt1 = conn1.createStatement();
			rs1 = stmt1.executeQuery(fetch_column);
			if(rs1.first())
			{
				result = rs1.getString(1);
				// System.out.println(result);
			}
			rs1.close();
			stmt1.close();
			conn1.close();	
		}	
		catch(Exception e)
		{
			System.out.println("public static String fetch( String dbDriver, String dbUrl, String dbUser, String dbPassword, String condition, String table, String column)" + e);
			return "fail";
		}
		return result;		
	}
	
	// Make directory, and run "chown -R user:userGroup dir"	
	// Make directory, and run "chown -R user dir"
	public static String makeDir( String jobDir, String user, String userGroup )
	{
		try
		{
			String parentDir = jobDir.substring(0,jobDir.lastIndexOf('/'));
			System.out.println(parentDir);
			if(!(new File(parentDir)).isDirectory())
				makeDir(parentDir,user,userGroup);
			if(!(new File(jobDir).isDirectory()))
			{
				System.out.println("jobDir "+jobDir);
				new File(jobDir).mkdir();
				try
				{
					String[] commands = new String[]{"chown","-R",user,jobDir};
					for(int i=0;i<commands.length;i++)
					{
						System.out.println(commands[i]);
					}
					Process process = Runtime.getRuntime().exec(commands);
					InputStreamReader er=new InputStreamReader(process.getErrorStream());
					BufferedReader input = new BufferedReader(er);
					String line;
					while ((line = input.readLine()) != null){
						System.out.println(line);
					}
				}catch (IOException e){
					System.out.println("public String makeDir(){");
					System.err.println ("chown ERROR! " + e.getMessage());
					return "fail";
				}
			}
			else
			{
				System.out.println("Job directory already exist! ");
				return "exist";
			}
		}catch(SecurityException e)
		{
			System.out.println("Can not make directory! " + e);
			return "fail";
		}
		return "success";
	}

	public static boolean isFileExist(String fileAbsoluteName)
	{
		boolean exist = false;
		try{
			File file = new File(fileAbsoluteName);
			if (file.exists()) 
				exist = true;
			else
				exist = false;
		}catch(Exception e){ 
			System.out.println(e);
		}
		return exist;
	}

	// Compress 
	public static boolean compress(String workingDir,String compressedFileName,String compressFileList)
	{
		// Write zip.sh file 
		RandomAccessFile zipShellFile = null;
		try{
			File zipShellFile2 = new File(workingDir+"/zip.sh");
			if (zipShellFile2.exists()) {
				zipShellFile2.delete();
				zipShellFile2.createNewFile();
			}	
			zipShellFile = new RandomAccessFile(zipShellFile2,"rw");
			zipShellFile.writeBytes("cd "+workingDir+"\n");
			zipShellFile.writeBytes("rm -rf "+compressedFileName+"\n");
			zipShellFile.writeBytes("zip -r "+compressedFileName+" "+compressFileList+"\n");
			zipShellFile.close();
		}catch(IOException e){ 
			System.out.println(e);
		}
		// chmod +x zip.sh and run zip.sh
		try{
			Runtime.getRuntime().exec("chmod +x "+workingDir+"/zip.sh");
			Process process = Runtime.getRuntime().exec(workingDir+"/zip.sh");
			InputStreamReader is=new InputStreamReader(process.getInputStream());
			BufferedReader input = new BufferedReader(is);
			String line;
			while ((line = input.readLine()) != null){
				System.out.println(line);
			}
		}catch(IOException e){
			System.err.println("IOException " + e.getMessage());
		}
		return true;
	}
	
	// Run dos2unix over a file. 	
	public static String dos2unix( String fileName )
	{
		try{
			Runtime rt=Runtime.getRuntime(); 
			Process pcs;
			pcs=rt.exec("dos2unix "+fileName);			
			InputStream is=pcs.getInputStream();
			BufferedReader br= new BufferedReader(new InputStreamReader(is));
			String line=new String();
			while((line = br.readLine()) != null)
			{
				System.out.println(line+"\n");
			}	
		}
		catch(IOException e){
			System.out.println("public String dos2unix( String fileName)");
			System.out.println(e);
			return "fail";
		}
		return "success";
	}

	// Get words (which is seperated by space or '\t') at position index.  	
	public static String getWordsAt(String line,int index)
	{
		int begin=-1,end=-1;
		char last=' ';
		for(int i=0;i<line.length();i++)
		{
			if(((line.toCharArray()[i]!=' ') && (line.toCharArray()[i]!='\t') && last==' ')||((line.toCharArray()[i]!=' ') && (line.toCharArray()[i]!='\t') && last=='\t'))
			{
				index--;
				if(index==0)
				{
					begin=i;
					break;
				}
			}
			last=line.toCharArray()[i];
		}
		if(begin==-1) return "null";
		for(int j=begin;j<=line.length();j++)
		{
			if( j==line.length() || line.toCharArray()[j]==' ' || line.toCharArray()[j]=='\t' || line.toCharArray()[j]=='\n')
			{
				end=j;break;
			}
		}
		if(begin!=-1 && end!=-1)
			return line.substring(begin,end);
		else
			return "null";
	}

	public static void main(String args[])
	{
		// Utility.compress("/home/linlei/workdir/ansys12.0/vm240", "vm240.zip", ".");
		/*
		if(Utility.isFileExist("/home/linlei/simple2.sh"))
			System.out.println("Exist!");
		else
			System.out.println("Not Exist!");
		*/
		String rootPath = new String("/opt/tomcat/webapps/portal/");
		String users[][] = new String[8][8];
		users = Utility.selectSQL(Initialization.fetch(rootPath+"parameters","dbDriver"), Initialization.fetch(rootPath+"parameters","dbUrl"),Initialization.fetch(rootPath+"parameters","dbUser"), ThreeDES.decryptKeyFromFile(rootPath+"password"), "", "host", "name");
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
				System.out.print(users[i][j]+"\t");
			System.out.print("\n");
		}
	}
}

