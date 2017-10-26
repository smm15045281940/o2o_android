package collectworker.listener;

/**
 * Created by Administrator on 2017/10/26.
 */

public interface CancelCollectListener {

    void success(String success);

    void failure(String failure);
}
