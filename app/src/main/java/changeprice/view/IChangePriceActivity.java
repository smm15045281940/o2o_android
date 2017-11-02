package changeprice.view;

/**
 * Created by Administrator on 2017/10/31.
 */

public interface IChangePriceActivity {

    void loadSuccess(String json);

    void loadFailure(String failure);

    void changeSuccess(String json);

    void changeFailure(String failure);
}
