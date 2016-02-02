package carsandco.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
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

	private static final Logger LOGGER = Logger.getLogger(MongoClass.class);
	protected static String DB_NAME = "carsandco-database";
	private static MongoClient mongoClient;
	private static DB db;

	public static String insertJSON(String collection, InputStream incomingData) throws UnknownHostException {

		StringBuilder builder = new StringBuilder();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
		} catch (Exception e) {
			LOGGER.error("Error Parsing data into database: - ");
			e.printStackTrace();
		}

		try {
			DBCollection coll = db.getCollection(collection);
			mongoClient.setWriteConcern(WriteConcern.JOURNALED);
			BasicDBObject doc = (BasicDBObject) JSON.parse(builder.toString());
			coll.insert(doc);
			ObjectId id = (ObjectId) doc.get("_id");

			return id.toString();
		} catch (Exception e) {
			LOGGER.error("Error adding data into database collection " + collection);
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getJSON(String collection, String key, String value)
			throws UnknownHostException, JSONException {

		String json = new String("");
		try {
			DBCollection coll = db.getCollection(collection);
			BasicDBObject query = new BasicDBObject(key, value);
			DBCursor cursor = coll.find(query);

			while (cursor.hasNext()) {
				json += cursor.next().toString() + "\n";
			}
			cursor.close();
		} catch (Exception e) {
			LOGGER.error("Error querying database in " + collection + " where " + key + " = " + value);
			e.printStackTrace();
		}

		return new JSONObject(json);

	}

	public static void addFieldWithValueToDoc(String collection, String docID, String key, String value)
			throws UnknownHostException {

		try {
			DBCollection coll = db.getCollection(collection);
			coll.update(new BasicDBObject("_id", docID), new BasicDBObject("$set", new BasicDBObject(key, value)));
		} catch (Exception e) {
			LOGGER.error("Error updating data in database: - ");
		}
	}
	
	public static void createDatabaseConnection() throws UnknownHostException{
		mongoClient = new MongoClient();
		db = mongoClient.getDB(DB_NAME);
		LOGGER.info("Database connection initialized successfully.");
	}
	
	public static void closeDatabaseConnection(){
		mongoClient.close();
		LOGGER.info("Database connection closed successfully.");
	}

}
