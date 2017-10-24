package workermanage.module;


import workermanage.listener.WorkerManageListener;

public interface IWorkerManageModule {

    void load(String url, WorkerManageListener workerManageListener);

    void cancelTask();
}
