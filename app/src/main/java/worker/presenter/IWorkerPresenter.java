package worker.presenter;


public interface IWorkerPresenter {

    void load(String url);

    void favoriteAdd(String url);

    void favoriteDel(String url);

    void destroy();
}
