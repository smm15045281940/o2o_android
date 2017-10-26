package task.presenter;


public interface ITaskPresenter {

    void load(String url);

    void taskCollect(String url);

    void destroy();
}
