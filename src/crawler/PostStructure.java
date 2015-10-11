package crawler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class PostStructure {
	public String id;
	public String message;
	public long timestamp;
	public List<String> commentList = new ArrayList<String>();
	public PostStructure(String id, String message, long timestamp,
			List<String> commentList) {
		super();
		this.id = id;
		this.message = message;
		this.timestamp = timestamp;
		this.commentList = commentList;
	}
	
	public PostStructure() {
		// TODO Auto-generated constructor stub
	}

	public static PostStructure getPostStructure(String id, String path) throws IOException {
		List<String> lines = FileUtils.readLines(new File(path+id));
		PostStructure ps = new PostStructure();
		ps.id = id;
		for (int i = 0; i < lines.size(); i++) {
			String s = lines.get(i);
			if (s.equals(".message")) {
				ps.message = lines.get(i+1);
			} else if (s.equals(".timestamp")) {
				ps.timestamp = Long.parseLong(lines.get(i+1));
			} else if (s.equals(".comment")) {
				for (int j = i+i ; j < lines.size(); j++) {
					ps.commentList.add(lines.get(j));
				}
			}
		}
		return ps;
	}
}
