package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/1.
 */

public class ToEmployerToDoingBean implements Serializable{

    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
