package carsandco.tools;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHandler {
	
    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }


    public static <T> T toObject(String jsonString, Class<T> targetClass){       
        return new Gson().fromJson(jsonString, targetClass);
    }
    
    public static String printJSON(String jsonString) throws JSONException{
    	JSONObject niceJson = new JSONObject(jsonString);
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	return gson.toJson(niceJson);
    }
}
