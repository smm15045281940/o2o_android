package bean;


import java.io.Serializable;

public class WorkerBean implements Serializable {

    private String id;
    private String icon;
    private String title;
    private String info;
    private String status;
    private int favorite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "WorkerBean{" +
                "id='" + id + '\'' +
                ", icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
