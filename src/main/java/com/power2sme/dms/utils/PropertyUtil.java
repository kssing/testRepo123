package com.power2sme.dms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

  public static final Properties properties=new Properties();
  public static final Properties serverProp=new Properties();
  
  static{
    PropertyUtil p=new PropertyUtil();
    InputStream dms=p
        .getClass().getClassLoader().getResourceAsStream("dms.properties");
    InputStream local=p
            .getClass().getClassLoader().getResourceAsStream("dev.properties");
    InputStream uat=p
            .getClass().getClassLoader().getResourceAsStream("uat.properties");
    InputStream ebf=p
            .getClass().getClassLoader().getResourceAsStream("ebf.properties");
    InputStream live=p
            .getClass().getClassLoader().getResourceAsStream("live.properties");
    
    
        //getResourceAsStream("../conf/kyc.properties");
    try {
      properties.load(dms);
      String env = properties.getProperty("API_MODE_TESTING");
      System.out.println("Environment:: "+ env);
      if(env.equalsIgnoreCase("local")){
    	  serverProp.load(local);
      } else if(env.equalsIgnoreCase("uat")){
    	  serverProp.load(uat);
      } else if(env.equalsIgnoreCase("ebf")){
    	  serverProp.load(ebf);
      } else if(env.equalsIgnoreCase("live")){
    	  serverProp.load(live);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  public static void main(String a[]){
	  
  }
}
