package ruolan.com.rxbus;

import ruolan.com.rxbus.bean.User;

/**
 * Created by wuyinlei on 2017/1/5.
 */

public class UserEvent {

    public String mUserName;
    public String mImageUrl;
    public String mUserEmail;

    public UserEvent(String username,String imageUrl,String email){
       this.mUserName = username;
        this.mImageUrl = imageUrl;
        this.mUserEmail = email;
    }
}
