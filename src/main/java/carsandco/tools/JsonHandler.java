package carsandco.tools;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHandler {
	
	private static final Logger LOGGER = Logger.getLogger(JsonHandler.class);
	
    public static String toJson(Object object) {
    	try{
    		return new Gson().toJson(object);
    	}catch(Exception e){
    		LOGGER.error("Error parsing object from type " + object.getClass().getName() + " to JSON String.");
    		e.printStackTrace();
    		return null;
    	}
    }


    public static <T> T toObject(String jsonString, Class<T> targetClass){
    	try{
    		 return new Gson().fromJson(jsonString, targetClass);
    	}catch(Exception e){
    		LOGGER.error("Error parsing JSON String to " + targetClass.getName() + " object.");
    		e.printStackTrace();
    		return null;
    	}
       
    }
    
    public static String printJSON(String jsonString) throws JSONException{
    	try{
    	JSONObject niceJson = new JSONObject(jsonString);
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	return gson.toJson(niceJson);
    	} catch(Exception e){
    		LOGGER.error("Error creating readable JSON String.");
    		e.printStackTrace();
    		return null;
    	}
    }
}
