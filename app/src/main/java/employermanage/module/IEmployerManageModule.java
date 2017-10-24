package employermanage.module;

import employermanage.listener.EmployerManageListener;

/**
 * Created by Administrator on 2017/10/23.
 */

public interface IEmployerManageModule {

    void load(String url, EmployerManageListener employerManageListener);

    void cancelTask();
}
