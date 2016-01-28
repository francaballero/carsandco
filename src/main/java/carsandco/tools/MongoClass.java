package carsandco.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

public class MongoClass {
	
	public static void insertJSON(String collection, InputStream incomingData)
			throws UnknownHostException {
		
		StringBuilder builder = new StringBuilder();
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
		} catch (Exception e) {
				System.err.println("Error Parsing: - ");
		}
		
		MongoClient mongoClient = new MongoClient();    
    	DB db = mongoClient.getDB("mydb");    	
    	DBCollection coll = db.getCollection(collection);    	
    	mongoClient.setWriteConcern(WriteConcern.JOURNALED);    	
    	BasicDBObject doc = (BasicDBObject) JSON.parse(builder.toString());
    	coll.insert(doc);
    	
    	mongoClient.close();
	}
	
	public static JSONObject getJSON(String collection,String key, String value)
			throws UnknownHostException, JSONException {
		
		String json = new String("");
		MongoClient MongoClient = new MongoClient(); 
    	DB db = MongoClient.getDB("mydb");    	
    	DBCollection coll = db.getCollection(collection);    	
    	BasicDBObject query = new BasicDBObject(key, value);
    	DBCursor cursor = coll.find(query);

    	try {
     	   while(cursor.hasNext()) {
     	       json += cursor.next().toString()+"\n";
     	   }
     	} finally {
     	   cursor.close();
     	}
     	
 		return new JSONObject(json);
 		
 	}
		
}
