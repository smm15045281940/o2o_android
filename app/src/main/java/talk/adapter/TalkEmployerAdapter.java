package talk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjzg.R;
import com.squareup.picasso.Picasso;

import talk.bean.TalkEmployerBean;
import talk.bean.TalkEmployerWorkerBean;
import view.CImageView;

/**
 * Created by Administrator on 2017/10/26.
 */

public class TalkEmployerAdapter extends BaseAdapter {

    private Context context;
    private TalkEmployerBean talkEmployerBean;

    public TalkEmployerAdapter(Context context, TalkEmployerBean talkEmployerBean) {
        this.context = context;
        this.talkEmployerBean = talkEmployerBean;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return talkEmployerBean == null ? 0 : talkEmployerBean.getTalkEmployerWorkerBeanList().size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return talkEmployerBean;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder0 holder0;
        ViewHolder1 holder1;
        switch (getItemViewType(position)) {
            case 0:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_talk_employer_type_0, null);
                    holder0 = new ViewHolder0(convertView);
                    convertView.setTag(holder0);
                } else {
                    holder0 = (ViewHolder0) convertView.getTag();
                }
                Picasso.with(context).load(talkEmployerBean.getIcon()).into(holder0.iconIv);
                if (talkEmployerBean.getSex().equals("0")) {
                    holder0.sexIv.setImageResource(R.mipmap.female);
                } else if (talkEmployerBean.getSex().equals("1")) {
                    holder0.sexIv.setImageResource(R.mipmap.male);
                }
                holder0.nameTv.setText(talkEmployerBean.getName());
                holder0.addressTv.setText(talkEmployerBean.getAddress());
                holder0.descTv.setText(talkEmployerBean.getDesc());
                break;
            case 1:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_talk_employer_type_1, null);
                    holder1 = new ViewHolder1(convertView);
                    convertView.setTag(holder1);
                } else {
                    holder1 = (ViewHolder1) convertView.getTag();
                }
                TalkEmployerWorkerBean talkEmployerWorkerBean = talkEmployerBean.getTalkEmployerWorkerBeanList().get(position - 1);
                if (talkEmployerWorkerBean != null) {
                    holder1.skillTv.setText("招" + talkEmployerWorkerBean.getSkill() + talkEmployerWorkerBean.getAmount() + "人");
                    holder1.priceTv.setText("工资" + talkEmployerWorkerBean.getPrice() + "元/天");
                    holder1.timeTv.setText(talkEmployerWorkerBean.getStartTime() + "-" + talkEmployerWorkerBean.getEndTime());
                }
                break;
        }
        return convertView;
    }

    private class ViewHolder0 {

        private CImageView iconIv;
        private ImageView sexIv;
        private TextView nameTv, addressTv, descTv;

        public ViewHolder0(View itemView) {
            iconIv = (CImageView) itemView.findViewById(R.id.iv_item_talk_employer_type_0_icon);
            sexIv = (ImageView) itemView.findViewById(R.id.iv_item_talk_employer_type_0_sex);
            nameTv = (TextView) itemView.findViewById(R.id.tv_item_talk_employer_type_0_name);
            addressTv = (TextView) itemView.findViewById(R.id.tv_item_talk_employer_type_0_address);
            descTv = (TextView) itemView.findViewById(R.id.tv_item_talk_employer_type_0_desc);
        }
    }

    private class ViewHolder1 {

        private TextView skillTv, priceTv, timeTv;

        public ViewHolder1(View itemView) {
            skillTv = (TextView) itemView.findViewById(R.id.tv_item_talk_employer_type_1_skill);
            priceTv = (TextView) itemView.findViewById(R.id.tv_item_talk_employer_type_1_price);
            timeTv = (TextView) itemView.findViewById(R.id.tv_item_talk_employer_type_1_time);
        }
    }
}
