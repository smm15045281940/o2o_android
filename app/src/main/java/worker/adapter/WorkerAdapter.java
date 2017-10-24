package worker.adapter;


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

import worker.bean.WorkerBean;

public class WorkerAdapter extends BaseAdapter {

    private Context context;
    private List<WorkerBean> list;

    public WorkerAdapter(Context context, List<WorkerBean> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_worker, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WorkerBean workerBean = list.get(position);
        if (workerBean != null) {
            Picasso.with(context).load(workerBean.getU_img()).placeholder(holder.iconIv.getDrawable()).into(holder.iconIv);
            if (workerBean.getU_task_status().equals("0")) {
                holder.statusIv.setImageResource(R.mipmap.worker_leisure);
            } else {
                holder.statusIv.setImageResource(R.mipmap.worker_mid);
            }
            holder.nameTv.setText(workerBean.getU_true_name());
            holder.infoTv.setText(workerBean.getUei_info());
        }
        return convertView;
    }

    private class ViewHolder {

        private ImageView iconIv, statusIv, collectIv;
        private TextView nameTv, infoTv, distanceTv;

        public ViewHolder(View itemView) {
            iconIv = (ImageView) itemView.findViewById(R.id.iv_item_worker_icon);
            statusIv = (ImageView) itemView.findViewById(R.id.iv_item_worker_status);
            collectIv = (ImageView) itemView.findViewById(R.id.iv_item_worker_collect);
            nameTv = (TextView) itemView.findViewById(R.id.tv_item_worker_name);
            infoTv = (TextView) itemView.findViewById(R.id.tv_item_worker_info);
            distanceTv = (TextView) itemView.findViewById(R.id.tv_item_worker_distance);
        }
    }
}
