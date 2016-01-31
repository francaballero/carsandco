package carsandco.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.bson.types.ObjectId;
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
	
	protected static String DB_NAME = "carsandcoDB";
	
	public static String insertJSON(String collection, InputStream incomingData)
			throws UnknownHostException {
		
		StringBuilder builder = new StringBuilder();
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
		} catch (Exception e) {
				System.err.println("Error Parsing data into database: - ");
		}
		
		MongoClient mongoClient = new MongoClient();    
    	DB db = mongoClient.getDB(DB_NAME);    	
    	DBCollection coll = db.getCollection(collection);    	
    	mongoClient.setWriteConcern(WriteConcern.JOURNALED);    	
    	BasicDBObject doc = (BasicDBObject) JSON.parse(builder.toString());
    	coll.insert(doc);
    	ObjectId id = (ObjectId)doc.get( "_id" );
    	 	
    	mongoClient.close();
    	return id.toString();
	}
	
	public static JSONObject getJSON(String collection,String key, String value)
			throws UnknownHostException, JSONException {
		
		String json = new String("");
		MongoClient MongoClient = new MongoClient(); 
    	DB db = MongoClient.getDB(DB_NAME);    	
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
	
	public static void addFieldWithValueToDoc(String collection, String docID, String key, String value) throws UnknownHostException {
		MongoClient MongoClient = new MongoClient(); 
    	DB db = MongoClient.getDB(DB_NAME);    	
    	DBCollection coll = db.getCollection(collection); 
    	try{
    	coll.update(new BasicDBObject("_id", docID), new BasicDBObject("$set", new BasicDBObject(key, value)));
    	} catch(Exception e){
    		System.err.println("Error updating data in database: - ");
    	}
	}
		
}
