package worker.listener;


import java.util.List;

import worker.bean.WorkerBean;

public interface WorkerListener {

    void success(List<WorkerBean> workerBeanList);

    void failure(String failure);
}
