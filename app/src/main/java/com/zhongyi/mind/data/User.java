package com.zhongyi.mind.data;

public class User {
    private int user_id;
    private String sex;
    private String account_mobile;
    private String nickname;
    private String head_pic;
    private String school;
    private String goldcoin;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAccount_mobile() {
        return account_mobile;
    }

    public void setAccount_mobile(String account_mobile) {
        this.account_mobile = account_mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGoldcoin() {
        return goldcoin;
    }

    public void setGoldcoin(String goldcoin) {
        this.goldcoin = goldcoin;
    }
}
