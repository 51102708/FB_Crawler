package db;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.CommentData;
import model.PostData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utility.ParseStopWord;

/**
 * 
 * @author root
 */
public class Handler {
	private Logger logger;
	private DB dB;

	public Handler() {
		this.logger = LoggerFactory.getLogger("system.Handler");
		this.dB = (new MongoClient("localhost", 27017)).getDB("thesis");
	}

	private void insertPostDataToMongo(PostData postData) {
		DBCollection dBCollection = dB.getCollection("postData");
		DBObject basicDBObject = new BasicDBObject();
		basicDBObject.put("postId", postData.getPostId());
		basicDBObject.put("postUser", postData.getPostUser());
		basicDBObject.put("postMessage", postData.getPostMessage());
		basicDBObject.put("postCreateDate", postData.getPostCreateDate());

		List<Object> lst = new ArrayList<>();
		HashMap<String, String> hm = postData.getPostLikeList();

		// Retrieve hash map
		Iterator<String> it = hm.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = hm.get(key);
			lst.add(new BasicDBObject(key, value));
		}

		basicDBObject.put("postLikeList", lst);

		dBCollection.insert(basicDBObject);
	}

	private void insertCommentDataToMongo(String postId, CommentData commentData) {
		DBCollection dBCollection = dB.getCollection("commentData");
		DBObject basicDBObject = new BasicDBObject();
		basicDBObject.put("postId", postId);
		basicDBObject.put("commentId", commentData.getCommentId());
		basicDBObject.put("commentUserId", commentData.getCommentUserId());
		basicDBObject.put("commentUserName", commentData.getCommentUserName());
		basicDBObject.put("commentMessage", commentData.getCommentMessage());
		basicDBObject.put("commentCreateDate",
				commentData.getCommentCreateDate());
		basicDBObject
				.put("commentLikeCount", commentData.getCommentLikeCount());

		dBCollection.insert(basicDBObject);
	}

	public void getPostDataFromMongo() {

		HashMap<String, String> hm = new HashMap<String, String>();

		int i = 0;

		DBCollection dbCollection = dB.getCollection("postData");
		DBCollection commentDbCollection = dB.getCollection("commentData");

		DBCursor dbCursor = dbCollection.find();
		BasicDBObject condition;
		BasicDBObject selection;
		DBCursor commentDbCursor;

		ParseStopWord parser = new ParseStopWord();
		parser.initSymbols();
		parser.initStopWordsEng("./src/config/stopWordsEng.txt");
		parser.initStopWordsVN("./src/config/stopWordsVN.txt");

		while (dbCursor.hasNext()) {
			DBObject dbObject = dbCursor.next();
			String id = dbObject.get("postId").toString();
			String postData = dbObject.get("postMessage").toString();

			// get data from comment
			condition = new BasicDBObject("postId", id);
			selection = new BasicDBObject("commentMessage", 1);
			
			commentDbCursor = commentDbCollection.find(condition, selection);
			try {
				while (commentDbCursor.hasNext()) {
					String commentMessage = commentDbCursor.next().get("commentMessage").toString();
					postData += " " + commentMessage;
				}
			} finally {
				commentDbCursor.close();
			}

			// System.out.println("Id: " + id);
			// System.out.println("PostData: " + postData);
			i++;
			System.out.println("Processing at line[" + i + "]");

			hm.put(id, postData);
			if (i % 50 == 0) {
				parser.parseString(hm);
				hm.clear();
			}

			// parser.parseString(hm, postData, id);
			// if (i % 50 == 0) {
			// System.out.println("Processing: " + i + " line........");
			// }
			//
			// if (hm.size() % 50 == 0) {
			// System.out.println("Export 50 files......");
			// ExportDataToFile.exportToFile(hm);
			// hm.clear();
			// }
		}

		if (hm.size() > 0) {
			parser.parseString(hm);
			hm.clear();
		}
	}

	public void run(List<Object> comment) {
		// For post data
		PostData postData = new PostData();
		String postMessage = "";
		String postId = "";

		// For comment data
		CommentData commentData = new CommentData();
		String commentMessage = "";

		Iterator<Object> iterator = comment.iterator();

		while (iterator.hasNext()) {
			Object line = iterator.next();
			if (line.toString().contains(".postId")) {
				line = iterator.next();
				postId = line.toString();
				postData.setPostId(postId);
			} else if (line.toString().contains(".postUser")) {
				line = iterator.next();
				postData.setPostUser(line.toString());
			} else if (line.toString().contains(".postMessage")) {
				line = iterator.next();

				if (line != null) {
					while (!line.toString().contains(".postCreatedDate")) {
						postMessage += line + "\n";
						line = iterator.next();
					}

				} else {
					line = iterator.next();
				}

				postData.setPostMessage(postMessage);

				// CreateDate
				line = iterator.next();
				String createDate = changeDateFormat(line.toString());
				postData.setPostCreateDate(createDate);

			} else if (line.toString().contains(".postLikeList")) {
				line = iterator.next();
				postData.setPostLikeList((HashMap<String, String>) line);
			} else if (line.toString().contains(".commentId")) {
				line = iterator.next();
				commentData.setCommentId(line.toString());
			} else if (line.toString().contains(".commentUserId")) {
				line = iterator.next();
				commentData.setCommentUserId(line.toString());
			} else if (line.toString().contains(".commentUserName")) {
				line = iterator.next();
				commentData.setCommentUserName(line.toString());
			} else if (line.toString().contains(".commentMessage")) {
				line = iterator.next();
				while (!line.toString().contains(".commentCreatedDate")) {
					commentMessage += line + "\n";
					line = iterator.next();
				}

				commentData.setCommentMessage(commentMessage);

				line = iterator.next();
				String createDate = changeDateFormat(line.toString());
				commentData.setCommentCreateDate(createDate);

			} else if (line.toString().contains(".commentLikeCount")) {
				line = iterator.next();
				commentData.setCommentLikeCount(line.toString());

				insertCommentDataToMongo(postId, commentData);

				// reset data of commentMessage
				commentMessage = "";
			}
		}

		insertPostDataToMongo(postData);
	}

	private String changeDateFormat(String dateString) {
		try {
			String date1 = dateString.substring(0, 20);
			String date2 = dateString.substring(24);
			String newDateString = date1 + date2;
			DateFormat formatter = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss yyyy");
			Date date = (Date) formatter.parse(newDateString);
			DateFormat reformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			return reformat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}
}
