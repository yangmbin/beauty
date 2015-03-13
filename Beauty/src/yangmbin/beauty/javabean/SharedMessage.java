package yangmbin.beauty.javabean;

import cn.bmob.v3.BmobObject;

public class SharedMessage extends BmobObject {
	private String username, sharedimgURL, nickname, headimgURL;
	private Integer like, dislike, comment;
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return this.username;
	}
	
	public void setSharedimgURL(String sharedimgURL) {
		this.sharedimgURL = sharedimgURL;
	}
	public String getSharedimgURL() {
		return this.sharedimgURL;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNickname() {
		return this.nickname;
	}
	
	public void setHeadimgURL(String headimgURL) {
		this.headimgURL = headimgURL;
	}
	public String getHeadimgURL() {
		return this.headimgURL;
	}
	
	public void setLike(Integer like) {
		this.like = like;
	}
	public Integer getLike() {
		return this.like;
	}
	
	public void setDislike(Integer dislike) {
		this.dislike = dislike;
	}
	public Integer getDislike() {
		return this.dislike;
	}
	
	public void setComment(Integer comment) {
		this.comment = comment;
	}
	public Integer getComment() {
		return this.comment;
	}
}
