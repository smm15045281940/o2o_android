package myevaluate.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/25.
 */

public class MyEvaluateBean implements Serializable {

    private String count;
    private String icon;
    private String info;
    private String time;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MyEvaluateBean{" +
                "count='" + count + '\'' +
                ", icon='" + icon + '\'' +
                ", info='" + info + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
