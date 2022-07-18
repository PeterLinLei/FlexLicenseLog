package portal;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class FlexLicenseLog{
  public String rootPath;	
  public String logFileName;
  public long logFilePointer;
  public long logFilePointer2;
  public String dbDriver;
  public String dbUrl;
  public String dbUser;
  public String dbPassword;

  public void setRootPath(String rp)
  {
    this.rootPath = rp;
  }	
  public String getRootPath()
  {
    return this.rootPath;
  }
  public void setLogFileName(String lfn)
  {
    this.logFileName = lfn;
  }
  public String getLogFileName()
  {
    return this.logFileName;
  }  
  public void setLogFilePointer(long lfp)
  {
    this.logFilePointer = lfp;
  }	
  public long getLogFilePointer()
  {
    return this.logFilePointer;
  }
  	
  public FlexLicenseLog(){
	  this.rootPath = new String("./");
	  initialization();
  }
  
  public FlexLicenseLog( String prootPath){
	this.rootPath = prootPath;  
	initialization();
  }

   public void initialization(){
    // For Database.
    dbDriver = Initialization.fetch(rootPath+"parameters","dbDriver");
    dbUrl = Initialization.fetch(rootPath+"parameters","dbUrl");
    dbUser = Initialization.fetch(rootPath+"parameters","dbUser");
    dbPassword = ThreeDES.decryptKeyFromFile(rootPath+"password");
  }	  
	
  public void logToDB()
  {
    String line = new String();
    RandomAccessFile logFile = null;
    try{
      logFile = new RandomAccessFile(logFileName,"r");
      if(logFile.length() < this.logFilePointer){
    	  System.out.println("Flex LM has restarted!");
    	  logFile.seek(0);
      }
      else
    	  logFile.seek(this.logFilePointer);
      while((line = logFile.readLine ()) != null)
      {
        long file_pointer = 0 ;
        String date_time = new String();
        String feature = new String();
        String account = new String();
        String host_name = new String();
        String feature_num = new String();
        // System.out.println(line);
        if(line.contains("OUT:")||line.contains("IN:"))
        {
          // System.out.println(line);
          // Machince date yyyy-MM-dd plus log hour HH:mm:ss .
          Date date = new Date(); 
          date_time = Utility.getWordsAt(line,1);
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");                   
		  date_time = sdf.format(date)+" "+date_time;	
		  // Compare hour and decide whether at midnight turn, and adjust date.
		  String hour1 = new String();
		  String hour2 = new String();
		  hour1 = line.substring(0,2);
		  SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");
		  hour2 = sdf_hour.format(date);
		  SimpleDateFormat sdf_all = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  try{
            Date date_time2 = sdf_all.parse(date_time);
		    if( hour1.equals("23") && hour2.equals("00") )
		    {
		      date_time2 = new Date(date_time2.getTime() - 24*3600*1000 );
			  date_time = sdf_all.format(date_time2);
		    }
		    if( hour1.equals(" 0") && hour2.equals("23") )
		    {
			  date_time2 = new Date(date_time2.getTime() + 24*3600*1000 );
			  date_time = sdf_all.format(date_time2);
		    }
          }catch(Exception e){
		  	System.out.println("1"+e);
          }
		  // fetch feature, account, host_name and feature_num. 
          feature = line.substring(line.indexOf("\"")+1,line.lastIndexOf("\""));
          account = line.substring(line.lastIndexOf("\"")+2,line.indexOf("@"));
          if(line.contains("license"))
		  {
            feature_num = line.substring(line.lastIndexOf("(")+1,line.indexOf("license")-1);
          	host_name = line.substring(line.indexOf("@") + 1 ,line.lastIndexOf("(") - 2);
		  }
		  else
		  { 
            feature_num = "1";
			host_name = line.substring(line.indexOf("@") + 1 ,line.length());
          }
		  // System.out.println(date_time+"\t"+feature+"\t"+account+"\t"+host_name+"\t"+feature_num);
          Connection conn;
          Statement stmt;										
          try{
            Class.forName(dbDriver);
            conn = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
            stmt = conn.createStatement();
            if(line.contains("OUT:"))
            {
			  // Get file pointer.
			  file_pointer = logFile.getFilePointer(); 
			  // Insert. 
              String sql = "insert into license_usage_log(file_pointer,user_name,host_name,feature,feature_num,begin) values(\'"+String.valueOf(file_pointer)+"\',\'"+account+"\',\'"+host_name+"\',\'"+feature+"\',\'"+feature_num+"\',\'"+date_time+"\')";
              stmt.executeUpdate(sql);
            }
            if(line.contains("IN:"))
            {
			  // fetch company and remain token. 
              String company = new String("NULL");
			  long token = 0 ;
              String sql_fetch_company_and_token = "select company,token from user where account=\'"+account+"\'";
              ResultSet rs = stmt.executeQuery(sql_fetch_company_and_token);
              if(rs.first())
              {
                company = rs.getString("company");
				token = rs.getInt("token");
                //System.out.println(company + "\t" + token);
              }
			  else
			  {
			    company = "NULL";
				token = -1;
			  }
              rs.close();
			
			  // fetch feature_token.									
              String feature_token = new String("0");
              String sql_fetch_feature_token = "select token from product_feature where feature=\'"+feature+"\'";
              rs = stmt.executeQuery(sql_fetch_feature_token);
              if(rs.first())
              {
                feature_token = rs.getString("token");
                //System.out.println(feature_token);
              }
			  else{
			  	feature_token = "0";
			  }
              rs.close();		
											
              // fetch begin time and calculate total used time(s). 
			  String begin_time = new String("1970-01-01 00:00:00.0");
              String sql_fetch_begin_time = "select begin, file_pointer from license_usage_log where user_name=\'"+account+"\' and host_name=\'"+host_name+"\' and feature=\'"+feature+"\' and feature_num=\'"+feature_num+"\' and end is null order by file_pointer";
              rs = stmt.executeQuery(sql_fetch_begin_time);
              if(rs.first())
              {
                begin_time = rs.getString("begin");
				file_pointer = rs.getLong("file_pointer");
                // System.out.println(begin_time);
				// System.out.println(file_pointer);
              }
			  else
			  {
			    begin_time = "1970-01-01 00:00:00.0";
				file_pointer = -1;
			  }
              rs.close();			
              
              String end_time = new String(date_time);
												
              long total_time=0; //second
											
              SimpleDateFormat bdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");						
              SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
              try{
                Date begin = bdf.parse(begin_time);
                Date end = edf.parse(end_time);
                total_time = (end.getTime()-begin.getTime())/1000;
                //System.out.println(total_time);
              }catch(Exception e){
			    System.out.println("Date begin = bdf.parse(begin_time);");
                System.out.println(e.getMessage());
              }
				
			  // calculate total token. 								
              long total_token=0;
              total_token = total_time * Integer.parseInt(feature_num) * (Integer.parseInt(feature_token)); 
              //System.out.println(total_token);									
												
              String sql = "update license_usage_log set company=\'"+company+"\', feature_token=\'"+feature_token+"\', end=\'"+date_time+"\', total_time=\'"+String.valueOf(total_time)+"\', total_token=\'"+String.valueOf(total_token)+"\' where file_pointer=\'"+String.valueOf(file_pointer)+"\' and user_name=\'"+account+"\' and host_name=\'"+host_name+"\' and feature=\'"+feature+"\' and feature_num=\'"+feature_num+"\' and end is null";
              stmt.executeUpdate(sql);	
			  
			  // Decrease user's remain tooken. 
			  token = token - total_token;
			  String sql_decrease_token = "update user set token=\'"+String.valueOf(token)+"\' where account=\'"+account+"\'";
			  stmt.executeUpdate(sql_decrease_token);		  											
            }
            stmt.close();
            conn.close();
          }
		  catch(Exception e)
          {
            System.out.println(e);
          }										
        }		// end of if. 
      }			// end of while. 
	  this.logFilePointer = logFile.getFilePointer();
	  if(this.logFilePointer != this.logFilePointer2)
	  {
		  System.out.println((new Date())+" File Pointer: "+this.logFilePointer);
		  this.logFilePointer2 = this.logFilePointer;
	  }
	  logFile.close();
    }
    catch(IOException e)
	{
      System.out.println(e);
    }
  }

  public static void main(String args[])
  {
	FlexLicenseLog fls = new FlexLicenseLog(args[0]);
	fls.setLogFileName(args[1]); 
	fls.setLogFilePointer(Integer.parseInt(args[2]));
    while(true)
    {
      fls.logToDB();
      try{
        Thread.sleep(10000);
      }
      catch(Exception e)
      {
	    System.out.println(e);
	  }
    }
  }
}
