package changeprice.presenter;

/**
 * Created by Administrator on 2017/10/31.
 */

public interface IChangePricePresenter {

    void load(String url);

    void change(String url);

    void destroy();
}
