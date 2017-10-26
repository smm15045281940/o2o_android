package worker.module;


import listener.JsonListener;
import worker.listener.FavoriteAddListener;
import worker.listener.WorkerListener;

public interface IWorkerModule {

    void load(String url, WorkerListener workerListener);

    void favoriteAdd(String url, FavoriteAddListener favoriteAddListener);

    void favoriteDel(String url, JsonListener jsonListener);

    void cancelTask();
}
