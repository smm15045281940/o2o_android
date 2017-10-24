package workermanage.listener;


import java.util.List;

import workermanage.bean.WorkerManageBean;

public interface WorkerManageListener {

    void success(List<WorkerManageBean> workerManageBeanList);

    void failure(String failure);
}
