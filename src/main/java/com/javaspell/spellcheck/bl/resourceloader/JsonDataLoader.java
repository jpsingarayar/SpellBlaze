package com.javaspell.spellcheck.bl.resourceloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Named("jsonLoader")
public class JsonDataLoader {
	
	private static final Logger LOGGER = Logger.getLogger(JsonDataLoader.class);
	
	public List<String> loadJsonFromURL(String URL) 
	{
		List<String> jsonStringList = new ArrayList<>();
		LOGGER.info("Loading Json from "+URL);
		try {
		URL url = new URL(URL);
	    HttpURLConnection request = (HttpURLConnection) url.openConnection();
	    
			request.connect();
		
	    JsonParser jp = new JsonParser(); //from gson
	    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
	    JsonArray rootobj = root.getAsJsonArray(); //May be an array, may be an object.
	    
	    if(rootobj!=null &&  !rootobj.isJsonNull())
	    {
	    	for(JsonElement brand: rootobj)
	    	{
	    		jsonStringList.add(brand.getAsString());
	    	}
	    }
	    
	    return jsonStringList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStringList;
	}

}
