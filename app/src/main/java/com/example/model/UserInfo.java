package com.example.model;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {
    private String username;
    private String email;
    private String uuid;
    @PropertyName("redeemedRewards")
    private List<String> redeemedRewards;
    private String profileImage;
    private String address;
    private String age;
    private String course;
    private String phone;
    private String bio;
    private String description;
    private String points;
    private boolean unlockTheme;


    public UserInfo(){

    }

    public UserInfo(String username, String email, String uuid, List<String> redeemedRewards){
        this.uuid = uuid;
        this.email = email;
        this.username = username;
        this.redeemedRewards = redeemedRewards;
    }

    public UserInfo(String username, String email, String password, String uuid, List<String> redeemedRewards){
        this.uuid = uuid;
        this.email = email;
        this.username = username;
        this.redeemedRewards = redeemedRewards;
    }

    public UserInfo(String username, String email, String password, String uuid, List<String> redeemedRewards, String profileImage){
        this.uuid = uuid;
        this.email = email;
        this.username = username;
        this.redeemedRewards = redeemedRewards;
        this.profileImage = profileImage;
    }

    public UserInfo(String username, String email, String uuid, List<String> redeemedRewards, String profileImage, String address, String age, String course, String phone, String bio, String description, String points) {
        this.username = username;
        this.email = email;
        this.uuid = uuid;
        this.redeemedRewards = redeemedRewards;
        this.profileImage = profileImage;
        this.address = address;
        this.age = age;
        this.course = course;
        this.phone = phone;
        this.bio = bio;
        this.description = description;
        this.points = points;
    }

    public UserInfo(UserInfo userInfo){
        this.username = userInfo.getUsername();
        this.email = userInfo.getEmail();
        this.uuid = userInfo.getUuid();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String toString(){
        String info = "Name: " + this.username + " /Email: " + this.email + "/uuid: " + this.uuid;
        return info;
    }

    public List<String> getRedeemedRewards() {
        return redeemedRewards;
    }

    public void setRedeemedRewards(List<String> redeemedRewardsName) {
        this.redeemedRewards = redeemedRewardsName;
    }

    public String getPassword(){
        return "";
    }

    public void setPassword(String password) {

    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String userImageUrl) {
        this.profileImage = userImageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public boolean isUnlockTheme() {
        return unlockTheme;
    }

    public void setUnlockTheme(boolean unlockTheme) {
        this.unlockTheme = unlockTheme;
    }
}
