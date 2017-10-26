package getevaluate.view;

import java.util.List;

import myevaluate.bean.MyEvaluateBean;

/**
 * Created by Administrator on 2017/10/25.
 */

public interface IGetEvaluateFragment {

    void success(List<MyEvaluateBean> myEvaluateBeanList);

    void failure(String failure);
}
