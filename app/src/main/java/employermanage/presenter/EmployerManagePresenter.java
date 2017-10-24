package employermanage.presenter;

import android.os.Handler;

import java.util.List;

import employermanage.bean.EmployerManageBean;
import employermanage.listener.EmployerManageListener;
import employermanage.module.EmployerManageModule;
import employermanage.module.IEmployerManageModule;
import employermanage.view.IEmployerManageActivity;

/**
 * Created by Administrator on 2017/10/23.
 */

public class EmployerManagePresenter implements IEmployerManagePresenter {

    private IEmployerManageActivity employerManageActivity;
    private IEmployerManageModule employerManageModule;
    private Handler handler;

    public EmployerManagePresenter(IEmployerManageActivity employerManageActivity) {
        this.employerManageActivity = employerManageActivity;
        employerManageModule = new EmployerManageModule();
        handler = new Handler();
    }

    @Override
    public void load(String url) {
        employerManageModule.load(url, new EmployerManageListener() {
            @Override
            public void success(final List<EmployerManageBean> employerManageBeanList) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        employerManageActivity.showSuccess(employerManageBeanList);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        employerManageActivity.showFailure(failure);
                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        if (employerManageModule != null) {
            employerManageModule.cancelTask();
            employerManageModule = null;
        }
        if (handler != null) {
            handler = null;
        }
        if (employerManageActivity != null) {
            employerManageActivity = null;
        }
    }
}
