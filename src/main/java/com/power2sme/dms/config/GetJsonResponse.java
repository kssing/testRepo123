package com.power2sme.dms.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;


public class GetJsonResponse {
	
	 	
		public static void main(String[] args) {
			try {
				URL url = new URL("http://localhost:8080/dms/api/v1/v2/api-docs");
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				File file = new File("/Users/p2s/Documents/workspace/dms/src/main/webapp/swagger.json");
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				String strTemp = "";
				while (null != (strTemp = br.readLine())) {
			
					bw.write(strTemp);
	
				}
				bw.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

}
