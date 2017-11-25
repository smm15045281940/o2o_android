package complain.module;


import com.gjzg.bean.ToComplainBean;
import listener.JsonListener;

public interface IComplainModule {

    void userInfo(String url, JsonListener jsonListener);

    void userIssue(String url, JsonListener jsonListener);

    void submit(String url, ToComplainBean toComplainBean, JsonListener jsonListener);

    void cancelTask();
}
