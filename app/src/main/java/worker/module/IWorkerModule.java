package worker.module;


import bean.PositionBean;
import worker.listener.WorkerListener;

public interface IWorkerModule {

    void load(String workerKindId, PositionBean positionBean, WorkerListener workerListener);

    void cancelTask();
}
