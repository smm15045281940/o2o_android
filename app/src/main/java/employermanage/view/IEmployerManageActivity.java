package employermanage.view;

import java.util.List;

import employermanage.bean.EmployerManageBean;

/**
 * Created by Administrator on 2017/10/23.
 */

public interface IEmployerManageActivity {

    void showSuccess(List<EmployerManageBean> employerManageBeanList);

    void showFailure(String failure);
}
