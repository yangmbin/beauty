package com.beauty.javabean;

import cn.bmob.v3.BmobUser;

public class BeautyUser extends BmobUser {
	String nickname, headimgURL, signature;
	
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
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getSignature() {
		return this.signature;
	}
}
