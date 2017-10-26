package collectjob.view;


import java.util.List;

import collectjob.bean.CollectJobBean;

public interface ICollectJobFragment {

    void showLoadSuccess(List<CollectJobBean> collectJobBeanList);

    void showLoadFailure(String failure);
}
