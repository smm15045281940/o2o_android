package employermanage.listener;

import java.util.List;

import employermanage.bean.EmployerManageBean;

/**
 * Created by Administrator on 2017/10/23.
 */

public interface EmployerManageListener {

    void success(List<EmployerManageBean> employerManageBeanList);

    void failure(String failure);
}
