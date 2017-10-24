package withdraw.view;

/**
 * Created by Administrator on 2017/10/24.
 */

public interface IWithDrawActivity {

    void showLoading();

    void hideLoading();

    void showSuccess(String success);

    void showFailure(String failure);
}
