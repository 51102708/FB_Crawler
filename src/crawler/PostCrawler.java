package crawler;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import com.restfb.types.Comment;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.Post;
import com.restfb.types.Post.Likes;

import db.Handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class PostCrawler {
	
	public static void main1(String[] args)
	{
		new Handler().getPostDataFromMongo();
	}
	
	public static void main(String[] args) throws IOException {

		String MY_ACCESS_TOKEN = "CAACEdEose0cBAP1ShtZA7GO24Ohg4vNIuBVDNkY39q3pWORW2Ik6zzEuFJRevjNZBuXeqVU0WipizHLCT6dliPUETFCBktD02PSNwOlXE1Gda0xZBZBcMCjEV8b6oYe7eos1ZATCoZBxKWmpG61M9ANd1YmFZBcLcp3UCsbozB0y6v1WvNvXL8DJGw4g0AgnAVkT847gnOIEqF9j03j6ZCZBx";
		if (args.length > 0) {
			MY_ACCESS_TOKEN = args[0];
		}
		
		FacebookClient facebookClient = new DefaultFacebookClient(
				MY_ACCESS_TOKEN);

		Connection<Post> pagePosts = facebookClient.fetchConnection(
				"262700667105773/feed", Post.class);
		
		int countPost = 0;
		Handler handler = new Handler();

		while (true) {
			for (List<Post> posts : pagePosts) {
				for (Post post : posts) {
					String id = post.getId();
					List<Object> postStr = postToString(post);
					List<String> cmtStr = getCommentFromPost(facebookClient, id);
					postStr.addAll(cmtStr);
					
					handler.run(postStr);

					countPost++;
					System.out.println("Number of post: " + countPost);
					if (countPost == 1000) {
						System.out.println("Number of post: " + countPost);
					}

					if (countPost == 10000) {
						System.out.println("Number of post: " + countPost);
					}

					if (countPost == 100000) {
						System.out.println("Number of post: " + countPost);
					}

					if (countPost == 1000000) {
						System.out.println("Number of post: " + countPost);
					}
				}
			}
			
			if (pagePosts.hasNext()) {
				pagePosts = facebookClient.fetchConnection(
						pagePosts.getNextPageUrl(), Post.class);

				System.out.println("Next page......................");
			} else {
				System.out.println("Out of data......................");
				break;
			}

		}
	}

	private static List<String> getCommentFromPost(FacebookClient client,
			String post_id) {
		List<String> comments = new ArrayList<String>();

		Connection<Comment> allComments = client.fetchConnection(post_id
				+ "/comments", Comment.class);
		for (List<Comment> postcomments : allComments) {
			for (Comment comment : postcomments) {
				List<String> cmtList = commentToString(comment);
				comments.addAll(cmtList);
			}
		}

		return comments;
	}

	public static List<String> commentToString(Comment cm) {
		List<String> arr = new ArrayList<String>();
		arr.add(".commentId");
		arr.add(cm.getId());
		arr.add(".commentUserId");
		arr.add(cm.getFrom().getId());
		arr.add(".commentUserName");
		arr.add(cm.getFrom().getName());
		arr.add(".commentMessage");
		arr.add(cm.getMessage());
		arr.add(".commentCreatedDate");
		arr.add(cm.getCreatedTime().toString());
		arr.add(".commentLikeCount");
		arr.add(String.valueOf(cm.getLikeCount()));
		
		return arr;
	}

	public static List<Object> postToString(Post cm) {
		List<Object> arr = new ArrayList<>();
		arr.add(".postId");
		arr.add(cm.getId());
		arr.add(".postUser");
		arr.add(cm.getFrom().getId());
		arr.add(".postMessage");
		arr.add(cm.getMessage());
		arr.add(".postCreatedDate");
		arr.add(cm.getCreatedTime().toString());
		arr.add(".postLikeList");

		HashMap<String, String> hm = new HashMap<String, String>();
		Likes lk = cm.getLikes();
		if (lk != null) {
			for (NamedFacebookType l : lk.getData()) {
				hm.put(l.getId(), l.getName());
			}
		}
		arr.add(hm);
		
		return arr;
	}
}
