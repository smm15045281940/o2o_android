package changeprice.module;

import listener.JsonListener;

/**
 * Created by Administrator on 2017/10/31.
 */

public interface IChangePriceModule {

    void load(String url, JsonListener jsonListener);

    void change(String url,JsonListener jsonListener);

    void cancelTask();
}
