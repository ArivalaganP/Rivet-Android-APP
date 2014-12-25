package com.rivet.app.core.pojo;

/**
 * Created by Brian on 12/6/14.
 */
public class UserInfo extends  Object{

   private
    int is_fresh;
    private String username;
    private boolean isLogin;
    
    
        
    public boolean getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

        private int userID;


    public int getIs_fresh() {
        return is_fresh;
    }

    public void setIs_fresh(int is_fresh) {
        this.is_fresh = is_fresh;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
