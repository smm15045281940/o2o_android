package task.presenter;


import android.os.Handler;

import java.util.List;

import task.bean.TaskBean;
import task.listener.TaskCollectListener;
import task.listener.TaskListener;
import task.module.ITaskModule;
import task.module.TaskModule;
import task.view.ITaskActivity;

public class TaskPresenter implements ITaskPresenter {

    private ITaskActivity iTaskActivity;
    private ITaskModule iTaskModule;
    private Handler mHandler = new Handler();

    public TaskPresenter(ITaskActivity iTaskActivity) {
        this.iTaskActivity = iTaskActivity;
        iTaskModule = new TaskModule();
    }

    @Override
    public void load(String url) {
        iTaskModule.load(url, new TaskListener() {
            @Override
            public void success(final List<TaskBean> taskBeanList) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iTaskActivity.showSuccess(taskBeanList);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iTaskActivity.showFailure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void taskCollect(String url) {
        iTaskModule.taskCollect(url, new TaskCollectListener() {
            @Override
            public void success(String success) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void failure(String failure) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        iTaskModule.cancelTask();
        if (iTaskModule != null)
            iTaskModule = null;
        if (iTaskActivity != null)
            iTaskActivity = null;
        if (mHandler != null)
            mHandler = null;
    }
}
