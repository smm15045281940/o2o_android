package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/3.
 */

public class ToWorkerBean implements Serializable{

    private String s_id;
    private String s_name;

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    @Override
    public String toString() {
        return "ToWorkerBean{" +
                "s_id='" + s_id + '\'' +
                ", s_name='" + s_name + '\'' +
                '}';
    }
}