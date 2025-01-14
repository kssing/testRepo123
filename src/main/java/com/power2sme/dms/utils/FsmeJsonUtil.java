package com.power2sme.dms.utils;

import java.lang.reflect.Type;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FsmeJsonUtil {

	private FsmeJsonUtil() {
	}

	public static boolean isValidJson(String jsonString) {
		if (Objects.isNull(jsonString)) {
			return false;
		}
		try {
			JsonParser parser = new JsonParser();
			JsonElement je = parser.parse(jsonString);
			return !je.isJsonNull();
		} catch (JsonSyntaxException jse) {
			log.error( "" , jse);
			return false;
		}
	}
	
	public static <T> T jsonToObject(String jsonStr, Class<T> type) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonStr, type);
	}

	public static String getJsonStr(Object obj) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	public static String getXmlStr(Object obj) throws JsonProcessingException
	{
		XmlMapper xmlMapper = new XmlMapper();
		return xmlMapper.writeValueAsString(obj);
	}

	public static <T> T convertJsonStringToObject(String jsonString, TypeToken<T> typeToken) {
		try {
			GsonBuilder gb = new GsonBuilder();
			Gson gson = gb.create();
			Type typeOfSrc = typeToken.getType();			
			return gson.fromJson(jsonString,typeOfSrc);			
		} catch (Exception e) {
			log.error("convertJsonStringToObject exception::",e);
			throw e;
		}
	}
}
