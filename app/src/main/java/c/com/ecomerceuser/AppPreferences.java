package c.com.ecomerceuser;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ramu on 25-06-2017.
 */

public class AppPreferences {
    SharedPreferences pref;
    SharedPreferences.Editor    editor;
    Context context;
    int PRIVATE_MODE=0;
    private static final String PREF_NAME="app_flags";
    private String userType = "userType";


    private String isChatActive,userId;

    public void setIsChatActive(String isChatActive)
    {
        editor.putString(isChatActive,isChatActive.toString());
        editor.commit();
    }

    public String getIsChatActive()
    {
        String flag = pref.getString(isChatActive, "");
        return flag;
    }

    public void setUserId(String userId)
    {
        editor.putString(userId,userId.toString());
        editor.commit();
    }

    public String getUserId()
    {
        String flag = pref.getString(userId, "");
        return flag;
    }



    // Constructor
    public AppPreferences(Context context)
    {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setUserType(String value)
    {
        editor.putString(userType,value.toString());
        editor.commit();
    }
    public String getUserType()
    {
        String flag = pref.getString(userType, "");
        return flag;
    }

    public void clear()
    {
        editor.clear().commit();
    }

}