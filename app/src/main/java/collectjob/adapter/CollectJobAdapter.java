package collectjob.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjzg.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import collectjob.bean.CollectJobBean;
import view.CImageView;

/**
 * Created by Administrator on 2017/10/25.
 */

public class CollectJobAdapter extends BaseAdapter {

    private Context context;
    private List<CollectJobBean> list;

    public CollectJobAdapter(Context context, List<CollectJobBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CollectJobBean collectJobBean = list.get(position);
        if (collectJobBean != null) {
            holder.titleTv.setText(collectJobBean.getTitle());
            holder.infoTv.setText(collectJobBean.getDuration());
            Picasso.with(context).load(collectJobBean.getIcon()).into(holder.iconIv);
            if (collectJobBean.getStatus().equals("0")) {
                holder.statusIv.setImageResource(R.mipmap.worker_wait);
            } else if (collectJobBean.getStatus().equals("2")) {
                holder.statusIv.setImageResource(R.mipmap.worker_mid);
            } else if (collectJobBean.getStatus().equals("3")) {
                holder.statusIv.setImageResource(R.mipmap.worker_over);
            }
            holder.collectIv.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {

        private CImageView iconIv;
        private ImageView statusIv, collectIv;
        private TextView titleTv, infoTv;

        public ViewHolder(View itemView) {
            iconIv = (CImageView) itemView.findViewById(R.id.iv_item_task_icon);
            statusIv = (ImageView) itemView.findViewById(R.id.iv_item_task_status);
            collectIv = (ImageView) itemView.findViewById(R.id.iv_item_task_collect);
            titleTv = (TextView) itemView.findViewById(R.id.tv_item_task_name);
            infoTv = (TextView) itemView.findViewById(R.id.tv_item_task_info);
        }
    }
}
