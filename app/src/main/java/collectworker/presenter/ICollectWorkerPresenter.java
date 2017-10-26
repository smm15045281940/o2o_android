package collectworker.presenter;


public interface ICollectWorkerPresenter {

    void load(String id);

    void cancelCollect(String url);

    void destroy();
}
