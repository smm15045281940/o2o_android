package talk.listener;

/**
 * Created by Administrator on 2017/10/26.
 */

public interface TalkEmployerListener {

    void success(String json);

    void failure(String failure);
}
