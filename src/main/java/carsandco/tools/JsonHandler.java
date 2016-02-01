package carsandco.tools;

import com.google.gson.Gson;

public class JsonHandler {
	
    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }


    public static <T> T toObject(String jsonString, Class<?> targetClass){       
        return new Gson().fromJson(jsonString, targetClass);
    }
}
