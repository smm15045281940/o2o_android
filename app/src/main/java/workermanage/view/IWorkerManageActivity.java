package workermanage.view;


import java.util.List;

import workermanage.bean.WorkerManageBean;

public interface IWorkerManageActivity {

    void showWorkerManageSuccess(List<WorkerManageBean> workerManageBeanList);

    void showWorkerManageFailure(String failure);
}
