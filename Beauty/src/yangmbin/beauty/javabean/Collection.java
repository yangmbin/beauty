package yangmbin.beauty.javabean;

import cn.bmob.v3.BmobObject;

public class Collection extends BmobObject {
private String articleID, username;
	
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
}
