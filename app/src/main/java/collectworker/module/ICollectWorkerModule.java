package collectworker.module;


import collectworker.listener.CancelCollectListener;
import collectworker.listener.OnLoadCollectWorkerListener;

public interface ICollectWorkerModule {

    void load(String id, OnLoadCollectWorkerListener onLoadCollectWorkerListener);

    void cancelCollect(String url, CancelCollectListener cancelCollectListener);

    void cancelTask();
}
