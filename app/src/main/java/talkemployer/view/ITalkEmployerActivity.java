package talkemployer.view;


/**
 * Created by Administrator on 2017/10/24.
 */

public interface ITalkEmployerActivity {

    void success(String json);

    void failure(String failure);

    void skillSuccess(String json);

    void skillFailure(String failure);
}