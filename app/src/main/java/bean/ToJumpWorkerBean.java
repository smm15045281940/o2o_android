package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/1.
 */

public class ToJumpWorkerBean implements Serializable {

    private String orderId;
    private String tewId;
    private String taskId;
    private String workerId;
    private String skillId;

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
}
