package ruolan.com.rxbus.bean;

import com.google.gson.Gson;

/**
 * Created by wuyinlei on 2017/1/5.
 */

public class User {


    /**
     * token : 72b50064-533b-44ea-8659-149df4389347
     * data : {"id":14125,"email":"1069584784@qq.com","logo_url":"http://qzapp.qlogo.cn/qzapp/100358052/F166DACE9B4532BC55D25862896C37AA/100","username":"若兰明月","mobi":"18503942380"}
     * status : 1
     * message : success
     */

    private String token;
    private DataBean data;
    private int status;
    private String message;

    public static User objectFromData(String str) {

        return new Gson().fromJson(str, User.class);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * id : 14125
         * email : 1069584784@qq.com
         * logo_url : http://qzapp.qlogo.cn/qzapp/100358052/F166DACE9B4532BC55D25862896C37AA/100
         * username : 若兰明月
         * mobi : 18503942380
         */

        private String id;
        private String email;
        private String logo_url;
        private String username;
        private String mobi;

        public static DataBean objectFromData(String str) {

            return new Gson().fromJson(str, DataBean.class);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getLogo_url() {
            return logo_url;
        }

        public void setLogo_url(String logo_url) {
            this.logo_url = logo_url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMobi() {
            return mobi;
        }

        public void setMobi(String mobi) {
            this.mobi = mobi;
        }
    }
}
