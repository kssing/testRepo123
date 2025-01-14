//package com.power2sme.dms.config;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;
//
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import com.power2sme.dms.utils.PropertyUtil;
//
//
//public class DataSourceManagerImpl {
//
//	 private static Context ctx;
//	  private static Context envCtx;
//	  
//	  public static DataSource getDataSource(String datasourceName)throws NamingException{    
//		  DataSource datasource=null;
//			if("local".equalsIgnoreCase(PropertyUtil.properties.getProperty("API_MODE_TESTING"))){
//				DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//					driverManagerDataSource.setDriverClassName(PropertyUtil.properties.getProperty("MYSQL_DRIVERNAME"));
//					driverManagerDataSource.setUrl(PropertyUtil.properties.getProperty("DMS_DB_URL"));
//					driverManagerDataSource.setUsername(PropertyUtil.properties.getProperty("DMS_DB_USERNAME"));
//					driverManagerDataSource.setPassword(PropertyUtil.properties.getProperty("DMS_DB_PASSWORD"));
//
//					datasource = driverManagerDataSource;
//					
//			}else{
//				DataSource ds=(DataSource)envCtx.lookup(datasourceName);
//				datasource = ds;
//			}	  
//			return datasource;
//	  }
//	  
//	  
//	  static{
//	     try {
//	      ctx=new InitialContext();
//	      envCtx=(Context)ctx.lookup("java:comp/env");
//	    } catch (NamingException e) {
//	      // TODO Auto-generated catch block
//	      e.printStackTrace();
//	    }
//	  }
//
//
//
//}
