package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/31.
 */

public class ToChangePriceBean implements Serializable {

    private String tewId;
    private String taskId;
    private String authorId;
    private String workerId;
    private String skillName;
    private String amount;
    private String price;
    private String startTime;
    private String endTime;

    public String getTewId() {
        return tewId;
    }

    public void setTewId(String tewId) {
        this.tewId = tewId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    @Override
    public String toString() {
        return "ToChangePriceBean{" +
                "tewId='" + tewId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", workerId='" + workerId + '\'' +
                ", skillName='" + skillName + '\'' +
                ", amount='" + amount + '\'' +
                ", price='" + price + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
