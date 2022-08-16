// 
// PeraLS,Portal.

package portal;
//import javax.servlet.http.*;
import java.io.*;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.Date;
import java.util.Calendar;

public class LicenseStatusShowBean{
	public static int MaxFeatureNumber = 1000;
	public static int MaxUsageNumber = 10000;
	public static int FeatureColumnNumber = 10;
	public static int UsageColumnNumber = 10;
	public static int DefaultUpdateFeatureStatusInterval = 300;
	
	public String site = new String("");
	public String licenseServer = new String("");				// License Server Name, such as head.kylin.com. 
	public String port = new String("");						// port number, such as 1055, 7241.
	public String firstLicenseServer = new String("");			// equals to licenseServer if one server mode, get the first license server if three license servers mode. 
	public String secondLicenseServer = new String("");
	public String thirdLicenseServer = new String("");
	public String licenseServerMaster = new String("");			// master: license server UP (MASTER) v11.9
	public String licenseFilePath = new String("");				// 
	public String optionFilePath = new String("");				// 	
	public String portAndLicenseServer = new String("");		// port@licenseServer, such as 1055@master, 5280@10.84.248.51:5280@10.84.248.9:5280@10.84.248.52.
	public String vendorDaemon = new String("");
	public String lmstatCheckLicenseStatusTime = new String("");// lmutil output check time.
	public String checkLicenseStatusTime = new String("");		// time when fetchLicenseStatus() invoked. Almost equal to this.dateTime.
	public int featureNumber = 0;								// feature number, or count.
	public String featureStatus[][] = new String[MaxFeatureNumber][FeatureColumnNumber];		
																// feature status array:
																// featureStatus[][0] = ( save for Module )
																// featureStatus[][1] = feature
																// featureStatus[][2] = total
																// featureStatus[][3] = used
																// featureStatus[][4] = left
																// featureStatus[][5] = rUse
																// featureStatus[][6] = wUse
																// featureStatus[][7] = oUse
	public String featureStatusUpdateTime = new String("");
	public int updateFeatureStatusInterval = 300;				// Update feature status every 300 seconds.
	
	public int usageNumber = 0;									// feature usage number, or count.
	public String usageStatus[][] = new String[MaxUsageNumber][UsageColumnNumber];		
																// feature usage array:
																// usageStatus[][0] = ( save for Module )
																// usageStatus[][1] = feature
																// usageStatus[][2] = user
																// usageStatus[][3] = host
																// usageStatus[][4] = version
																// usageStatus[][5] = start time (using this feature)
																// usageStatus[][6] = license usage number
																// usageStatus[][7] = license feature usage session id number.
																// usageStatus[][8] = start time (Revised). 
																// usageStatus[][9] = time (Continued using the feature till this time).
	
	public LicenseStatusShowBean(){
		featureNumber=0;
		usageNumber = 0;
		this.updateFeatureStatusInterval = DefaultUpdateFeatureStatusInterval;
		for(int i=0;i<MaxFeatureNumber;i++)
		{
			for(int j=0;j<FeatureColumnNumber;j++)
				featureStatus[i][j] = new String("");
		}
		for(int i=0;i<MaxUsageNumber;i++)
		{
			for(int j=0;j<UsageColumnNumber;j++)
				usageStatus[i][j] = new String("");
		}
	}
	
	public LicenseStatusShowBean(int pupdateFeatureStatusInterval){
		featureNumber=0;
		usageNumber = 0;
		this.updateFeatureStatusInterval = pupdateFeatureStatusInterval;
		for(int i=0;i<MaxFeatureNumber;i++)
		{
			for(int j=0;j<FeatureColumnNumber;j++)
				featureStatus[i][j] = new String("");
		}
		for(int i=0;i<MaxUsageNumber;i++)
		{
			for(int j=0;j<UsageColumnNumber;j++)
				usageStatus[i][j] = new String("");
		}
	}

	public void setLicenseServer(String plicenseServer){
		this.licenseServer=plicenseServer;
		this.portAndLicenseServer=this.port+"@"+this.licenseServer;
	}
	public String getLicenseServer(){
		return this.licenseServer;
	}
	public void setPort(String pport){
		this.port=pport;
		this.portAndLicenseServer=this.port+"@"+this.licenseServer;
	}
	public String getPort(){
		return this.port;
	}
	public void setPortAndLicenseServer(String pportAndLicenseServer){
		this.portAndLicenseServer=pportAndLicenseServer;
	}
	public String getPortAndLicenseServer(){
		return this.portAndLicenseServer;
	}
	public void setLmstatCheckLicenseStatusTime(String plmstatCheckLicenseStatusTime){
		this.lmstatCheckLicenseStatusTime=plmstatCheckLicenseStatusTime;
	}
	public String getLmstatCheckLicenseStatusTime(){
		return this.lmstatCheckLicenseStatusTime;
	}
	public void setCheckLicenseStatusTime(String pcheckLicenseStatusTime){
		this.checkLicenseStatusTime=pcheckLicenseStatusTime;
	}
	public String getCheckLicenseStatusTime(){
		return this.checkLicenseStatusTime;
	}
	public void setFeatureNumber(int pfeatureNumber){
		this.featureNumber=pfeatureNumber;
	}
	public int getFeatureNumber(){
		return this.featureNumber;
	}
	public void setFeatureStatus(String pfeatureStatus[][]){
		this.featureStatus = pfeatureStatus;
	}
	public String[][]  getFeatureStatus(){
		return this.featureStatus;
	}
	public void setUsageNumber(int pusageNumber){
		this.usageNumber=pusageNumber;
	}
	public int getUsageNumber(){
		return this.usageNumber;
	}	
	public void setUsageStatus(String pusageStatus[][]){
		this.usageStatus = pusageStatus;
	}
	public String[][]  getUsageStatus(){
		return this.usageStatus;
	}
	
	public void fetchLicenseStatus()
	{
		// System.out.println("\n### Enter function fetchLicenseStatus() ###");
		String feature = new String();
		BufferedReader ls;		
		try{
			Runtime rt = Runtime.getRuntime();
			Process pcs;
			pcs =  rt.exec("MyLmstat.sh "+ this.portAndLicenseServer);
			InputStream is = pcs.getInputStream();
			ls = new BufferedReader(new InputStreamReader(is));
			String line=new String();
			while((line = ls.readLine()) != null)
			{
				// System.out.println(line);
				
				if(line.contains("Flexible License Manager status on"))
				{
					this.lmstatCheckLicenseStatusTime=line.substring(line.indexOf("on")+3,line.length());
					// System.out.println("Flexible License Manager status on "+dateTime);
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
				Date d1 = new Date();
				this.checkLicenseStatusTime = sdf.format(d1);
				long d2 = d1.getTime();
				long d3 = d2 - d2 % (this.updateFeatureStatusInterval*1000L);
				Date d4 = new Date(d3);
				this.featureStatusUpdateTime = sdf.format(d4);
				
				// License server status: 1055@master
				// License server status: 1717@lenovo-2b310535,1717@B4Circuit3,1717@B4Circuit4
				if(line.contains("License server status"))
				{
					if(line.contains(","))
					{
						this.firstLicenseServer=line.substring(line.indexOf("@")+1,line.indexOf(","));
						String s5=line.substring(line.indexOf(",")+1, line.lastIndexOf(","));
						this.secondLicenseServer=s5.substring(s5.indexOf("@")+1, s5.length());
						String s6=line.substring(line.lastIndexOf(",")+1, line.length());
						this.thirdLicenseServer=s6.substring(s6.indexOf("@")+1, s6.length());
						this.licenseServer=this.firstLicenseServer+" "+this.secondLicenseServer+" "+this.thirdLicenseServer;
					}
					else
					{
						this.firstLicenseServer=line.substring(line.indexOf("@")+1,line.length());
						this.licenseServer=this.firstLicenseServer;
					}
					this.port=line.substring(line.indexOf(":")+2,line.indexOf("@"));
					// System.out.println("License Server: "+licenseServer+" \tPort: "+port);
				}
				 
				if(line.contains("License file(s) on"))
				{
					String s8 = line.substring(line.indexOf(":")+2,line.length()-1);
					// Replace '\' to '\\' in windows file path. 
					this.licenseFilePath=s8.replaceAll("\\\\", "\\\\\\\\");				
				}
				
				if(line.contains("license server UP (MASTER)"))
				{
					String s1=Utility.getWordsAt(line, 1);
					this.licenseServerMaster=s1.substring(0, s1.length()-1);
				}
				
				// ??? Vendor Daemon may have more than one.
				if(line.contains(": UP "))
				{
					String s2=Utility.getWordsAt(line, 1);
					this.vendorDaemon=s2.substring(0, s2.length()-1);
				}
				
				if(line.contains("Users of ") && !line.contains("No such feature exists") && !line.contains("Error"))
				{
					String featureUsed = new String();
					String featureTotal = new String();
					String featureLeft = new String();
					feature = line.substring(line.indexOf("Users of ")+9,line.indexOf(":"));				
					if(line.contains("Uncounted"))
					{
						featureTotal = "999";
						featureUsed = "0";					
						featureLeft = "999";					
						featureStatus[featureNumber][1]=feature;
						featureStatus[featureNumber][2]=featureTotal;
						featureStatus[featureNumber][3]=featureUsed;
						featureStatus[featureNumber][4]=featureLeft;							
					}
					else{
						featureTotal = line.substring(line.indexOf("Total of ")+9,line.indexOf("license")-1);
						featureUsed = line.substring(line.lastIndexOf("Total of ")+9,line.lastIndexOf("license")-1);					
						featureLeft = String.valueOf(Integer.parseInt(featureTotal) - Integer.parseInt(featureUsed));				
						featureStatus[featureNumber][1]=feature;
						featureStatus[featureNumber][2]=featureTotal;
						featureStatus[featureNumber][3]=featureUsed;
						featureStatus[featureNumber][4]=featureLeft;
					}
					// System.out.println("feature: "+feature+"\t"+"Total: "+featureTotal+"\t"+"Used: "+featureUsed+"\t"+"Left: "+featureLeft);	
					featureNumber++;
				}
				
				//     linlei master master (v2022.0814) (master/1055 103), start Fri 4/5 22:21
				//     linlei master master (v2022.0814) (master/1055 203), start Fri 4/5 22:21, 4 licenses
				//     126447 B4PC126447 B4PC126447 (v2021.05) (B4Circuit3/1717 3408), start Wed 1/2 15:19
				//     jiaming.wu@iluvatar.local s-10-111-2-25.iluvatar.local /dev/tty (v2022.7) (license02/1818 114), start Thu 2/21 1:17, 2 licenses
				//     jiaming.wu@iluvatar.local s-10-111-2-29.iluvatar.local /dev/tty (v2022.7) (license02/1818 338), start Fri 2/22 10:05

				if( line.contains(this.licenseServerMaster+"/"+this.port) && line.contains("start") )
				{
					int ste = -1; //Start Time End point.
					String user = new String("");
					String host = new String("");
					String version = new String("");
					String startTime = new String("");
					String licenseUsageNumber = new String(""); 
					String sessionID = new String("");
					String startTimeRevised = new String("");
					String timeUsedTill = new String("");
					user = Utility.getWordsAt(line,1);
					host = Utility.getWordsAt(line,2);
					{
						if(line.contains("(v"))
						{
							String s7=line.substring(line.indexOf("(v"),line.length());
							s7=Utility.getWordsAt(s7, 1);
							if(!s7.isEmpty() && s7.contains("(") && s7.contains(")"))
								version = s7.substring(s7.indexOf("(")+1, s7.indexOf(")"));							
						}
						else
							version = new String("");
					}
					if(line.contains("license") && line.lastIndexOf("license") > line.lastIndexOf("start"))
					{
						ste = line.lastIndexOf(",");
						licenseUsageNumber = line.substring(line.lastIndexOf(",")+2,line.lastIndexOf("license")-1);
					}
					else
					{
						ste = line.length();
						licenseUsageNumber = "1";
					}
					{
						String s3 = line.substring(line.indexOf(this.port),line.length());
						String s4 = Utility.getWordsAt(s3, 2);
						sessionID = s4.substring(0, s4.indexOf(")"));
					}
					startTime=line.substring(line.indexOf("start ")+6,ste);
					{
						String s11 = startTime.replace("/", "-");
						Calendar c11 = Calendar.getInstance();						
						String s12 = String.valueOf(c11.get(Calendar.YEAR));
						startTimeRevised = new String(s12+"-"+Utility.getWordsAt(s11, 2)+" "+Utility.getWordsAt(s11, 3)+":00");
						
						SimpleDateFormat sdf1 = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
						Date d12 = new Date();
						timeUsedTill = sdf1.format(d12);
					}
					usageStatus[usageNumber][1]=feature;
					usageStatus[usageNumber][2]=user;
					usageStatus[usageNumber][3]=host;
					usageStatus[usageNumber][4]=version;
					usageStatus[usageNumber][5]=startTime;
					usageStatus[usageNumber][6]=licenseUsageNumber;
					usageStatus[usageNumber][7]=sessionID;
					usageStatus[usageNumber][8]=startTimeRevised;
					usageStatus[usageNumber][9]=timeUsedTill;
					// System.out.println("\t"+"User: "+user+"\t"+"Host: "+host+"\t"+"Start Time: "+startTime+"\t"+"License Usage Number: "+licenseUsageNumber);
					usageNumber++;
				}
			}
		}
		catch(IOException e){	
			System.out.println("public void fetchLicenseStatus()"); 
			System.out.println(e);		
		}
		// System.out.println("### Leave function fetchLicenseStatus() ###");				
	}
	
	public void printLicenseStatus()
	{
		System.out.println("License server: " + this.licenseServer);
		System.out.println("Port number: " + this.port);
		System.out.println("First license server: " + this.firstLicenseServer);
		System.out.println("Second license server: " + this.secondLicenseServer);
		System.out.println("Third license server: " + this.thirdLicenseServer);
		System.out.println("License server master: " + this.licenseServerMaster);
		System.out.println("License file path: " + this.licenseFilePath);
		System.out.println("Port@LicenseServer: " + this.portAndLicenseServer);
		System.out.println("Vendor daemon: " + this.vendorDaemon);
		System.out.println("Checking time(lmstat): " + this.lmstatCheckLicenseStatusTime);
		System.out.println("Checking time(fetchLicenseStatus): " + this.checkLicenseStatusTime);	
		System.out.println("Updating Database feature status time: " + this.featureStatusUpdateTime);
		System.out.println("Number of features: " + this.featureNumber);
		System.out.println("Number of usages: " + this.usageNumber);
		
		System.out.println("Features Status: ");
		for( int i=0; i<featureNumber; i++)
		{
			System.out.println( featureStatus[i][1] +"\t"+ featureStatus[i][2] +"\t"+ featureStatus[i][3] +"\t"+ featureStatus[i][4] );
		}
		System.out.println("Usages Status: ");
		for( int i=0; i<usageNumber; i++)
		{
			System.out.println( usageStatus[i][1] +"\t"+ usageStatus[i][2] +"\t"+ usageStatus[i][3] +"\t"+ usageStatus[i][4] +"\t"+ usageStatus[i][5] +"\t"+ usageStatus[i][6] +"\t"+ usageStatus[i][7] +"\t"+ usageStatus[i][8] +"\t"+ usageStatus[i][9] );
		}
		System.out.println("");
	}
	
	// 
	public boolean containUsage(String pusage[])
	{
		boolean contain = false;
		for(int i=0;i<this.usageNumber;i++)
		{
			if(	   this.usageStatus[i][1]!=null && !this.usageStatus[i][1].equals("") && this.usageStatus[i][1].equals(pusage[1])
				&& this.usageStatus[i][2]!=null && !this.usageStatus[i][2].equals("") && this.usageStatus[i][2].equals(pusage[2])
				&& this.usageStatus[i][3]!=null && !this.usageStatus[i][3].equals("") && this.usageStatus[i][3].equals(pusage[3])
				&& this.usageStatus[i][4]!=null && !this.usageStatus[i][4].equals("") && this.usageStatus[i][4].equals(pusage[4])
				&& this.usageStatus[i][5]!=null && !this.usageStatus[i][5].equals("") && this.usageStatus[i][5].equals(pusage[5])
				&& this.usageStatus[i][6]!=null && !this.usageStatus[i][6].equals("") && this.usageStatus[i][6].equals(pusage[6])
				&& this.usageStatus[i][7]!=null && !this.usageStatus[i][7].equals("") && this.usageStatus[i][7].equals(pusage[7])
					)
			{
				contain = true;
			}
		}
		return contain;
	}
	
	public void printUsage(String pusage[])
	{
		System.out.println( pusage[1] +"\t"+ pusage[2] +"\t"+ pusage[3] +"\t"+ pusage[4] +"\t"+ pusage[5] +"\t"+ pusage[6] +"\t"+ pusage[7] +"\t"+ pusage[8] +"\t"+ pusage[9] );
	}
	
	// fetch total/used/left/rUse/wUse/oUse/ license number.
	/*
		First, run fetchLicenseStatus(), to fetch total/used/left license number.
		Seconde, fetch rUse/wUse. (rUse is the number used by SGE running jobs, wUse is the number used by SGE waiting jobs.)
		Third, fetch oUse. (oUse is the number used by other (not SGE) jobs.)
		Then, run this function.
	*/
	public int getLicenseNumber( String pfeature, String pname )
	{
		for(int i=0; i<featureNumber; i++)
		{
			String feature = new String();
			String total = new String();
			String used = new String();
			String left = new String();
			String rUse = new String();
			String wUse = new String();
			String oUse = new String();			
			feature = featureStatus[i][1];
			total = featureStatus[i][2];
			used = featureStatus[i][3];
			left = featureStatus[i][4];
			rUse = featureStatus[i][5];
			wUse = featureStatus[i][6];
			oUse = featureStatus[i][7];
			if( pfeature.equals(feature) )
			{
				if( pname.equals("total") ) return Integer.parseInt(total);
				if( pname.equals("used") ) return Integer.parseInt(used);
				if( pname.equals("left") ) return Integer.parseInt(left);
				if( pname.equals("rUse") ) return Integer.parseInt(rUse);
				if( pname.equals("wUse") ) return Integer.parseInt(wUse);
				if( pname.equals("oUse") ) return Integer.parseInt(oUse);	
			}
		}
		return -1 ;
	}
	
	public static void main(String args[])
	{
		/*
		LicenseStatusShowBean lssb = new LicenseStatusShowBean();
		lssb.setLicenseServer("master");
		lssb.setPort("1055");
		lssb.setPortAndLicenseServer("1055@master");
		lssb.fetchLicenseStatus();
		lssb.printLicenseStatus();
		*/
		
		LicenseStatusShowBean lssb = new LicenseStatusShowBean(300);
		lssb.setPortAndLicenseServer(args[1]);
		lssb.fetchLicenseStatus();
		lssb.printLicenseStatus();

	}
}
