package withdraw.module;

import withdraw.bean.WithDrawBean;
import withdraw.listener.WithDrawListener;

/**
 * Created by Administrator on 2017/10/24.
 */

public interface IWithDrawModule {

    void withdraw(WithDrawBean withDrawBean, WithDrawListener withDrawListener);

    void cancelTask();
}
