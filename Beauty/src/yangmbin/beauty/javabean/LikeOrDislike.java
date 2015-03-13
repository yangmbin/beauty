package yangmbin.beauty.javabean;

import cn.bmob.v3.BmobObject;

public class LikeOrDislike extends BmobObject {
	private String articleID, username, like, dislike;
	
	public void setArticleID(String articleID) {
		this.articleID = articleID;
	}
	public String getArticleID() {
		return this.articleID;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return this.username;
	}
	
	public void setLike(String like) {
		this.like = like;
	}
	public String getLike() {
		return this.like;
	}
	
	public void setDislike(String dislike) {
		this.dislike = dislike;
	}
	public String getDislike() {
		return this.dislike;
	}
}
