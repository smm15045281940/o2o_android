package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import bean.TInfoTaskBean;
import listener.IdPosClickHelp;

/**
 * Created by Administrator on 2017/11/5.
 */
//任务详情
public class TInfoTaskAdapter extends BaseAdapter {

    private Context context;
    private TInfoTaskBean tInfoTaskBean;
    private IdPosClickHelp idPosClickHelp;

    public TInfoTaskAdapter(Context context, TInfoTaskBean tInfoTaskBean, IdPosClickHelp idPosClickHelp) {
        this.context = context;
        this.tInfoTaskBean = tInfoTaskBean;
        this.idPosClickHelp = idPosClickHelp;
    }

    @Override
    public int getCount() {
        return tInfoTaskBean.gettInfoWorkerBeanList() == null ? 0 : tInfoTaskBean.gettInfoWorkerBeanList().size();
    }

    @Override
    public Object getItem(int position) {
        return tInfoTaskBean;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }

    private class ViewHolder{



        public ViewHolder(View itemView) {
        }
    }
}
