package task.module;


import task.listener.TaskListener;

public interface ITaskModule {

    void load(String url,TaskListener taskListener);

    void cancelTask();
}
