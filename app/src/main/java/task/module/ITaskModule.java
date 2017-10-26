package task.module;


import task.listener.TaskCollectListener;
import task.listener.TaskListener;

public interface ITaskModule {

    void load(String url, TaskListener taskListener);

    void taskCollect(String url, TaskCollectListener taskCollectListener);

    void cancelTask();
}
