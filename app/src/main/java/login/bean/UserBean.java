package login.bean;


import java.io.Serializable;

public class UserBean implements Serializable {

    private String id;
    private String name;
    private String sex;
    private String online;
    private String icon;
    private String token;
    private String pass;
    private String idcard;

    public UserBean() {
    }

    public UserBean(String id, String name, String sex, String online, String icon, String token, String pass, String idcard) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.online = online;
        this.icon = icon;
        this.token = token;
        this.pass = pass;
        this.idcard = idcard;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", online='" + online + '\'' +
                ", icon='" + icon + '\'' +
                ", token='" + token + '\'' +
                ", pass='" + pass + '\'' +
                ", idcard='" + idcard + '\'' +
                '}';
    }
}
