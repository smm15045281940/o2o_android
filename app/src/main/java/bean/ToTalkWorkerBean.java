package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/31.
 */

public class ToTalkWorkerBean implements Serializable {

    private int toTalkState;
    private String workerId;
    private String skillId;
    private String orderId;
    private String tewId;
    private String taskId;
    private String amount;
    private String price;
    private String startTime;
    private String endTime;

    public int getToTalkState() {
        return toTalkState;
    }

    public void setToTalkState(int toTalkState) {
        this.toTalkState = toTalkState;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

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
}
