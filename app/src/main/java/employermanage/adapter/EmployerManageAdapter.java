package employermanage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjzg.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import employermanage.bean.EmployerManageBean;

/**
 * Created by Administrator on 2017/10/23.
 */

public class EmployerManageAdapter extends BaseAdapter {

    private Context context;
    private List<EmployerManageBean> list;

    public EmployerManageAdapter(Context context, List<EmployerManageBean> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_employer_manage, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EmployerManageBean employerManageBean = list.get(position);
        if (employerManageBean != null) {
            Picasso.with(context).load(employerManageBean.getIcon()).into(holder.iconIv);
            if (employerManageBean.getStatus().equals("0")) {
                holder.statusIv.setImageResource(R.mipmap.worker_wait);
                holder.waitLl.setVisibility(View.VISIBLE);
                holder.talkLl.setVisibility(View.INVISIBLE);
                holder.doneLl.setVisibility(View.INVISIBLE);
            } else if (employerManageBean.getStatus().equals("1")) {
                holder.statusIv.setImageResource(R.mipmap.worker_talk);
                holder.waitLl.setVisibility(View.INVISIBLE);
                holder.talkLl.setVisibility(View.VISIBLE);
                holder.doneLl.setVisibility(View.INVISIBLE);
            } else if (employerManageBean.getStatus().equals("2")) {
                holder.statusIv.setImageResource(R.mipmap.worker_mid);
                holder.waitLl.setVisibility(View.INVISIBLE);
                holder.talkLl.setVisibility(View.INVISIBLE);
                holder.doneLl.setVisibility(View.INVISIBLE);
            } else if (employerManageBean.getStatus().equals("3")) {
                holder.statusIv.setImageResource(R.mipmap.worker_over);
                holder.waitLl.setVisibility(View.INVISIBLE);
                holder.talkLl.setVisibility(View.INVISIBLE);
                holder.doneLl.setVisibility(View.VISIBLE);
            }
            holder.titleTv.setText(employerManageBean.getTitle());
            holder.infoTv.setText(employerManageBean.getInfo());
        }
        return convertView;
    }

    private class ViewHolder {

        private ImageView iconIv, statusIv;
        private TextView titleTv, infoTv;
        private LinearLayout waitLl, talkLl, doneLl;

        public ViewHolder(View itemView) {
            iconIv = (ImageView) itemView.findViewById(R.id.iv_item_employer_manage_icon);
            statusIv = (ImageView) itemView.findViewById(R.id.iv_item_employer_manage_status);
            titleTv = (TextView) itemView.findViewById(R.id.tv_item_employer_manage_title);
            infoTv = (TextView) itemView.findViewById(R.id.tv_item_employer_manage_info);
            waitLl = (LinearLayout) itemView.findViewById(R.id.ll_item_employer_manage_wait);
            talkLl = (LinearLayout) itemView.findViewById(R.id.ll_item_employer_manage_talk);
            doneLl = (LinearLayout) itemView.findViewById(R.id.ll_item_employer_manage_done);
        }
    }
}
