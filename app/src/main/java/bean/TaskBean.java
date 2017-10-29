package bean;

import java.io.Serializable;

public class TaskBean implements Serializable {

    private String taskId;
    private String collectId;
    private String authorId;
    private String icon;
    private String title;
    private String info;
    private String status;
    private int favorite;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getCollectId() {
        return collectId;
    }

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "TaskBean{" +
                "taskId='" + taskId + '\'' +
                ", collectId='" + collectId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", info='" + info + '\'' +
                ", status='" + status + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
