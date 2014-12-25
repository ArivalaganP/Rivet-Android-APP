package com.rivet.app.core.pojo;

public class FacebookUser {
	
	private boolean isLoggedIn ;
	private String city ;
	private String timestamp ;
	
	private String gender ;
	private String firstName ;
	private int age ;
	
	private int birthMonth ;
	private long loginProviderUID ;
	private long providerUI ;
	private String email ;
	private String UID ;	
	private String lastName ;
	private int birthYear ;
	private String photoURL ;
	private String nickname ;
	private boolean isSiteUser ;
	
	
	
	public long getLoginProviderUID() {
		return loginProviderUID;
	}
	public void setLoginProviderUID(long loginProviderUID) {
		this.loginProviderUID = loginProviderUID;
	}
	public long getProviderUI() {
		return providerUI;
	}
	public void setProviderUI(long providerUI) {
		this.providerUI = providerUI;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}

	public int getBirthMonth() {
		return birthMonth;
	}
	public void setBirthMonth(int birthMonth) {
		this.birthMonth = birthMonth;
	}
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	public String getPhotoURL() {
		return photoURL;
	}
	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public boolean isSiteUser() {
		return isSiteUser;
	}
	public void setSiteUser(boolean isSiteUser) {
		this.isSiteUser = isSiteUser;
	}


}
