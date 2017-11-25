package withdraw.module;

import com.gjzg.bean.WithDrawBean;
import listener.JsonListener;

/**
 * Created by Administrator on 2017/10/24.
 */

public interface IWithDrawModule {

    void withdraw(WithDrawBean withDrawBean, JsonListener jsonListener);

    void cancelTask();
}
