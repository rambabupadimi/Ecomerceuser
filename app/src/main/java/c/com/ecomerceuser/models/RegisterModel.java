package c.com.ecomerceuser.models;

import java.util.HashMap;

/**
 * Created by Ramu on 25-06-2017.
 */

public class RegisterModel {
    public String name,phone,userid,imgurl,userType,userStatus,lastSeen;
    HashMap<String,String> scannedUsers;
    HashMap<String,String> scannedGroups;

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public HashMap<String, String> getScannedUsers() {
        return scannedUsers;
    }

    public HashMap<String, String> getScannedGroups() {
        return scannedGroups;
    }

    public void setScannedGroups(HashMap<String, String> scannedGroups) {
        this.scannedGroups = scannedGroups;
    }

    public void setScannedUsers(HashMap<String, String> scannedUsers) {
        this.scannedUsers = scannedUsers;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
