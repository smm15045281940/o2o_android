package myevaluate.adapter;

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

import myevaluate.bean.MyEvaluateBean;
import view.CImageView;

/**
 * Created by Administrator on 2017/10/25.
 */

public class MyEvaluateAdapter extends BaseAdapter {

    private Context context;
    private List<MyEvaluateBean> list;

    public MyEvaluateAdapter(Context context, List<MyEvaluateBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_evaluate, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyEvaluateBean myEvaluateBean = list.get(position);
        if (myEvaluateBean != null) {
            Picasso.with(context).load(myEvaluateBean.getIcon()).into(holder.iconIv);
            holder.countTv.setText(myEvaluateBean.getCount());
            holder.infoTv.setText(myEvaluateBean.getInfo());
            holder.timeTv.setText(myEvaluateBean.getTime());
        }
        return convertView;
    }

    private class ViewHolder {

        private CImageView iconIv;
        private TextView countTv, infoTv, timeTv;

        public ViewHolder(View itemView) {
            iconIv = (CImageView) itemView.findViewById(R.id.iv_item_evaluate_icon);
            countTv = (TextView) itemView.findViewById(R.id.tv_item_evaluate_num_count);
            infoTv = (TextView) itemView.findViewById(R.id.tv_item_evaluate_content);
            timeTv = (TextView) itemView.findViewById(R.id.tv_item_evaluate_time);
        }
    }
}
