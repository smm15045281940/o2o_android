package task.view;


import java.util.List;

import task.bean.TaskBean;

public interface ITaskActivity {

    void showSuccess(List<TaskBean> taskBeanList);

    void showFailure(String failure);
}
