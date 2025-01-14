package com.power2sme.dms.externaldto;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class ResponseDtoService {
	
	  public static String getJson(ResponseDto responseDto) {
		    GsonBuilder gb = new GsonBuilder();
		    gb.setDateFormat("dd MMM yyyy");
		    Gson gson = gb.create();
		    String json = gson.toJson(responseDto);
		    return json;
		  }


		  public static String getJson(List<ResponseDto> ls) {

		    GsonBuilder gb = new GsonBuilder();
		    gb.setDateFormat("dd MMM yyyy");
		    Gson gson = gb.create();
		    Type typeOfSrc = new TypeToken<List<ResponseDto>>() {}.getType();
		    String json = gson.toJson(ls, typeOfSrc);
		    return json;
		  }


		  public static ResponseDto toObject(String jsonParam) {
		    try {
		      GsonBuilder gb = new GsonBuilder();
		      gb.setDateFormat("dd MMM yyyy");
		      Gson gson = gb.create();
		      Type typeOfSrc = new TypeToken<ResponseDto>() {}.getType();
		      ResponseDto responseDto = gson.fromJson(jsonParam, typeOfSrc);
		      return responseDto;
		    } catch (Exception e) {
		      e.printStackTrace();
		      throw e;
		    }
		  }
		  
//		  public static String getXML(ResponseDto responseDto) {
//			  try {
//					   XmlMapper xmlMapper = new XmlMapper();
//						 
//				String  xml = xmlMapper.writeValueAsString(responseDto);
//				  return xml;
//				  
//				} catch (JsonProcessingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			  return null;
//			   }


}
