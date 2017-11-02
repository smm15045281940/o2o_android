package employertotalk.view;

/**
 * Created by Administrator on 2017/10/31.
 */

public interface IEmployerToTalkActivity {

    void loadSuccess(String json);

    void loadFailure(String failure);

    void skillSuccess(String json);

    void skillFailure(String failure);
}
