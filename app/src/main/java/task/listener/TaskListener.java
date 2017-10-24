package task.listener;


import java.util.List;

import task.bean.TaskBean;

public interface TaskListener {

    void success(List<TaskBean> taskBeanList);

    void failure(String failure);
}
