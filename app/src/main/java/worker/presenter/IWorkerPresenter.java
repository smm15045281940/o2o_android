package worker.presenter;


import bean.PositionBean;

public interface IWorkerPresenter {

    void load(String workerKindId, PositionBean positionBean);

    void destroy();
}
