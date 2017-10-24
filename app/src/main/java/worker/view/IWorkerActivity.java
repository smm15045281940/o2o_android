package worker.view;

import java.util.List;

import worker.bean.WorkerBean;

public interface IWorkerActivity {

    void success(List<WorkerBean> workerBeanList);

    void failure(String failure);
}
