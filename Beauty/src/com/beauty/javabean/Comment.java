package com.beauty.javabean;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject {
	private String headimgURL, nickname, comment, articleID;
	
	public void setHeadimgURL(String headimgURL) {
		this.headimgURL = headimgURL;
	}
	public String getHeadimgURL() {
		return this.headimgURL;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNickname() {
		return this.nickname;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getComment() {
		return this.comment;
	}
	
	public void setArticleID(String articleID) {
		this.articleID = articleID;
	}
	public String getArticleID() {
		return this.articleID;
	}
}
