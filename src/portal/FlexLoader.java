// 
package portal;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class FlexLoader{
	public String rootPath;	
	public String dbDriver;
	public String dbUrl;
	public String dbUser;
	public String dbPassword;
	public String language;
	
	LicenseStatusShowBean lssb;
	public String LastFeatureStatusUpdateTime;
	public int updateFeatureStatusInterval = 300;
	LicenseStatusShowBean lssbPrevious;
	LicenseStatusShowBean lssbCurrent;
	
	public void setRootPath(String rp)
	{
		this.rootPath = rp;
	}	
	public String getRootPath()
	{
		return this.rootPath;
	}
  	
	public FlexLoader(){
		this.rootPath = new String("./");
		initialization();
	}
  
	public FlexLoader( String prootPath){
		this.rootPath = prootPath;
		this.updateFeatureStatusInterval = 300;
		this.lssb = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		this.lssbPrevious = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		this.lssbCurrent = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		initialization();
	}

	public FlexLoader( String prootPath, int pinterval){
		this.rootPath = prootPath;
		this.updateFeatureStatusInterval = pinterval;
		this.lssb = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		this.lssbPrevious = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		this.lssbCurrent = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		initialization();
	}

	public void initialization(){
		// For Database.
		dbDriver = Initialization.fetch(rootPath+"parameters","dbDriver");
		dbUrl = Initialization.fetch(rootPath+"parameters","dbUrl");
		dbUser = Initialization.fetch(rootPath+"parameters","dbUser");
		dbPassword = ThreeDES.decryptKeyFromFile(rootPath+"password");
		this.language = Utility.fetch(dbDriver,dbUrl,dbUser,dbPassword,"name=\'language\'","constant","value");
		this.LastFeatureStatusUpdateTime = new String("");
		// this.language = new String("cn");
	}	  
	
	/*
	 * 
	 	create table license_server
		(
		site varchar(32),
		license_server varchar(128),
		port varchar(16),
		first_license_server varchar(32),
		second_license_server varchar(32),
		third_license_server varchar(32),
		license_server_master varchar(32),
		license_file_path varchar(256),
		option_file_path varchar(256),
		port_at_license_server varchar(256),
		vendor_daemon varchar(32),
		update_time datetime,
		number_of_features int,
		unique key (port_at_license_server)
		);
	 */
	public void updateLicenseServer(String pportAndLicenseServer)
	{
		this.lssb = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		this.lssb.setPortAndLicenseServer(pportAndLicenseServer);
		this.lssb.fetchLicenseStatus();
		// this.lssb.printLicenseStatus();
		String statement = "insert into license_server(site,license_server,port,first_license_server,second_license_server,third_license_server,license_server_master,license_file_path,option_file_path,port_at_license_server,vendor_daemon,update_time,number_of_features) "
				+ "values(\'"+ this.lssb.site +"\',\'"+ this.lssb.licenseServer +"\',\'"+ this.lssb.port +"\',\'"
				+ this.lssb.firstLicenseServer +"\',\'"+ this.lssb.secondLicenseServer +"\',\'"+ this.lssb.thirdLicenseServer +"\',\'"+ this.lssb.licenseServerMaster +"\',\'"
				+ this.lssb.licenseFilePath +"\',\'"+ this.lssb.optionFilePath +"\',\'"+ this.lssb.portAndLicenseServer +"\',\'"
				+ this.lssb.vendorDaemon +"\',\'"+ this.lssb.checkLicenseStatusTime +"\',\'"+ this.lssb.featureNumber +"\')";
		// System.out.println(statement);
		Utility.executeSQL(this.dbDriver, this.dbUrl, this.dbUser, this.dbPassword, statement);	
	}
	
	/*
	 * 
		create table license_feature
		(
		site varchar(32),
		license_server varchar(128),
		port varchar(16),
		first_license_server varchar(32),
		second_license_server varchar(32),
		third_license_server varchar(32),
		license_server_master varchar(32),
		license_file_path varchar(256),
		option_file_path varchar(256),
		port_at_license_server varchar(128),
		vendor_daemon varchar(32),
		update_time datetime,
		feature varchar(64),
		feature_total_number int,
		unique key (port_at_license_server,feature,feature_total_number)
		);
	 */
	public void updateLicenseFeature(String pportAndLicenseServer)
	{
		this.lssb = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		this.lssb.setPortAndLicenseServer(pportAndLicenseServer);
		this.lssb.fetchLicenseStatus();
		// this.lssb.printLicenseStatus();
		for(int i=0; i<this.lssb.featureNumber; i++)
		{
			String statement = "insert into license_feature(site, license_server, port, first_license_server, second_license_server, third_license_server, license_server_master, license_file_path, option_file_path, port_at_license_server, vendor_daemon, update_time, feature, feature_total_number) "
					+ "values(\'"+ this.lssb.site +"\',\'"+ this.lssb.licenseServer +"\',\'"+ this.lssb.port +"\',\'"
					+ this.lssb.firstLicenseServer +"\',\'"+ this.lssb.secondLicenseServer +"\',\'"+ this.lssb.thirdLicenseServer +"\',\'"+ this.lssb.licenseServerMaster +"\',\'"
					+ this.lssb.licenseFilePath +"\',\'"+ this.lssb.optionFilePath +"\',\'"+ this.lssb.portAndLicenseServer +"\',\'"
					+ this.lssb.vendorDaemon +"\',\'"+ this.lssb.checkLicenseStatusTime +"\',\'"+ this.lssb.featureStatus[i][1] +"\',\'"+ this.lssb.featureStatus[i][2] +"\')";
			// System.out.println(statement);
			Utility.executeSQL(this.dbDriver, this.dbUrl, this.dbUser, this.dbPassword, statement);				
		}
	}
	
	/*
	 * 
	  	create table feature_status
		(
		site varchar(32),
		port varchar(16),
		license_server_master varchar(32),
		port_at_license_server varchar(256),
		vendor_daemon varchar(32),
		update_time datetime,
		number_of_features int,
		number_of_usages int,
		feature varchar(64),
		feature_total_number int,
		feature_used_number int,
		feature_left_number int,
		unique key (port_at_license_server,update_time,feature)
		);
	 */
	public void updateFeatureStatus(String pportAndLicenseServer)
	{
		this.lssb = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		this.lssb.setPortAndLicenseServer(pportAndLicenseServer);
		this.lssb.fetchLicenseStatus();
		// this.lssb.printLicenseStatus();
		if(this.lssb.featureStatusUpdateTime!=null && !this.lssb.featureStatusUpdateTime.equals("") )
		{
			if(!this.lssb.featureStatusUpdateTime.equals(this.LastFeatureStatusUpdateTime))
			{
				System.out.println(new Date() + " Update feature status now! ");
				for(int i=0; i<this.lssb.featureNumber;i++)
				{
					String statement = "insert into feature_status(site,port,license_server_master,port_at_license_server,vendor_daemon,update_time,number_of_features,number_of_usages,feature,feature_total_number,feature_used_number,feature_left_number) "
							+ "values(\'"+ this.lssb.site +"\',\'"+ this.lssb.port +"\',\'"+ this.lssb.licenseServerMaster +"\',\'"
							+ this.lssb.portAndLicenseServer +"\',\'"+ this.lssb.vendorDaemon +"\',\'"
							+ this.lssb.featureStatusUpdateTime +"\',\'"+ this.lssb.featureNumber +"\',\'"+ this.lssb.usageNumber +"\',\'"
							+ this.lssb.featureStatus[i][1] +"\',\'"+ this.lssb.featureStatus[i][2] +"\',\'"+ this.lssb.featureStatus[i][3] +"\',\'"+ this.lssb.featureStatus[i][4] +"\')";
					// System.out.println(statement);
					Utility.executeSQL(this.dbDriver, this.dbUrl, this.dbUser, this.dbPassword, statement);	
				}
				this.LastFeatureStatusUpdateTime=new String(this.lssb.featureStatusUpdateTime);
			}
			else
			{
				System.out.println(new Date() + " Update feature status in next time interval! ");
			}
		}
	}

	/*
		create table feature_usage_log
		(
		site varchar(32),
		port varchar(16),
		license_server_master varchar(32),
		port_at_license_server varchar(128),
		vendor_daemon varchar(32),
		feature varchar(64),
		user varchar(64),
		client varchar(64),
		version varchar(64),
		check_out_time_lmstat varchar(64),
		begin datetime,
		end datetime,
		weight float,
		wall_time int,
		total_time int,
		feature_number int,
		expense float,
		session_id int
		);
	 */
	public void updateUsageStatus(String pportAndLicenseServer)
	{
		this.lssbCurrent = new LicenseStatusShowBean(this.updateFeatureStatusInterval);
		this.lssbCurrent.setPortAndLicenseServer(pportAndLicenseServer);
		this.lssbCurrent.fetchLicenseStatus();
		// this.lssbCurrent.printLicenseStatus();
		for(int i=0;i<this.lssbPrevious.usageNumber;i++)
		{
			boolean tcontain = this.lssbCurrent.containUsage(this.lssbPrevious.usageStatus[i]);
			if(!tcontain)
			{
				System.out.println("Usage record in Database!");
				this.lssbCurrent.printUsage(this.lssbPrevious.usageStatus[i]);
				Date d1 = Utility.stringToDate(this.lssbPrevious.usageStatus[i][9]);
				Date d2 = Utility.stringToDate(this.lssbPrevious.usageStatus[i][8]);
				float weight = 1;
				long wall_time = (d1.getTime() - d2.getTime())/1000;
				long total_time = wall_time * Integer.parseInt(this.lssbPrevious.usageStatus[i][6]);
				float expense = total_time * weight;
				// acfd_par_proc   root    master  v2012.0814      Sun 4/7 13:45   4       213     2022-4-7 13:45:00       2022-04-07 13:46:52
				String statement = "insert into feature_usage_log(site ,port ,license_server_master ,port_at_license_server ,vendor_daemon ,feature ,user ,client ,version ,check_out_time_lmstat ,begin ,end ,weight ,wall_time, total_time ,feature_number ,expense ,session_id ) "
						+ "values(\'"+ this.lssbPrevious.site +"\',\'"+ this.lssbPrevious.port +"\',\'"+ this.lssbPrevious.licenseServerMaster +"\',\'"
						+ this.lssbPrevious.portAndLicenseServer +"\',\'"+ this.lssbPrevious.vendorDaemon +"\',\'"
						+ this.lssbPrevious.usageStatus[i][1] +"\',\'"+ this.lssbPrevious.usageStatus[i][2] +"\',\'"+ this.lssbPrevious.usageStatus[i][3] +"\',\'"
						+ this.lssbPrevious.usageStatus[i][4] +"\',\'"+ this.lssbPrevious.usageStatus[i][5] +"\',\'"+ this.lssbPrevious.usageStatus[i][8] +"\',\'"
						+ this.lssbPrevious.usageStatus[i][9] +"\',\'"+ weight +"\',\'"+ wall_time +"\',\'"+ total_time +"\',\'"+ this.lssbPrevious.usageStatus[i][6] +"\',\'"
						+ expense +"\',\'"+ this.lssbPrevious.usageStatus[i][7] +"\')";
				// System.out.println(statement);
				Utility.executeSQL(this.dbDriver, this.dbUrl, this.dbUser, this.dbPassword, statement);					
			}
		}
		this.lssbPrevious = this.lssbCurrent;
		this.lssbCurrent = null;		
	}
	
	public static void main(String args[])
	{
		// System.out.println(args[0],args[1],args[2],args[3]);
		/* Parameters: root directory, port@licenseServer, updateFeatureStatusInterval, time interval. 
		 * /opt/tomcat/java/FlexLoader/ 1055@master 300 60
		 * /opt/tomcat/java/FlexLoader/ 1055@master 60  20
		 */

		FlexLoader fl = new FlexLoader(args[0],Integer.parseInt(args[2]));
		fl.updateLicenseServer(args[1]);
		fl.updateLicenseFeature(args[1]);
		// fl.updateFeatureStatus(args[1]);
		// fl.updateUsageStatus(args[1]);
		
		while(true)
		{
			fl.updateFeatureStatus(args[1]);
			fl.updateUsageStatus(args[1]);
			try{
				Thread.sleep(Integer.parseInt(args[3]) * 1000);
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
		
	}
}
