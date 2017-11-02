package employertotalk.presenter;

import android.os.Handler;

import employertotalk.module.EmployerToTalkModule;
import employertotalk.module.IEmployerToTalkModule;
import employertotalk.view.IEmployerToTalkActivity;
import listener.JsonListener;

/**
 * Created by Administrator on 2017/10/31.
 */

public class EmployerToTalkPresenter implements IEmployerToTalkPresenter {

    private IEmployerToTalkActivity employerToTalkActivity;
    private IEmployerToTalkModule employerToTalkModule;
    private Handler handler;

    public EmployerToTalkPresenter(IEmployerToTalkActivity employerToTalkActivity) {
        this.employerToTalkActivity = employerToTalkActivity;
        employerToTalkModule = new EmployerToTalkModule();
        handler = new Handler();
    }

    @Override
    public void load(String url) {
        employerToTalkModule.load(url, new JsonListener() {
            @Override
            public void success(final String json) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        employerToTalkActivity.loadSuccess(json);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        employerToTalkActivity.loadFailure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void skill(String url) {
        employerToTalkModule.skill(url, new JsonListener() {
            @Override
            public void success(final String json) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        employerToTalkActivity.skillSuccess(json);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        employerToTalkActivity.skillFailure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        if (employerToTalkModule != null) {
            employerToTalkModule.cancelTask();
            employerToTalkModule = null;
        }
        if (handler != null) {
            handler = null;
        }
        if (employerToTalkActivity != null) {
            employerToTalkActivity = null;
        }
    }
}
