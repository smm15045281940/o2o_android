package accountdetail.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gjzg.R;

import java.util.List;

import accountdetail.bean.AccountDetailBean;
import adapter.CommonAdapter;
import utils.DataUtils;

/**
 * 创建日期：2017/8/9 on 10:55
 * 作者:孙明明
 * 描述:明细适配器
 */

public class DetailAdapter extends CommonAdapter<AccountDetailBean> {

    public DetailAdapter(Context context, List<AccountDetailBean> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler holer;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_detail, null);
            holer = new ViewHoler(convertView);
            convertView.setTag(holer);
        } else {
            holer = (ViewHoler) convertView.getTag();
        }
        AccountDetailBean accountDetailBean = list.get(position);
        if (accountDetailBean != null) {
            String title = accountDetailBean.getType();
            if (TextUtils.isEmpty(title)) {
            } else {
                String money = accountDetailBean.getDes();
                if (!TextUtils.isEmpty(money))
                    holer.desTv.setText(money);
                if (title.equals("recharge")) {
                    holer.titleTv.setText("充值");
                } else if (title.equals("withdraw")) {
                    holer.titleTv.setText("提现");
                } else if (title.equals("income")) {
                    holer.titleTv.setText("收入");
                } else if (title.equals("pay")) {
                    holer.titleTv.setText("支出");
                }
                holer.timeTv.setText(DataUtils.msgTimes(accountDetailBean.getTime()));
            }
        }
        return convertView;
    }

    private class ViewHoler {

        private TextView titleTv, timeTv, desTv;

        public ViewHoler(View itemView) {
            titleTv = (TextView) itemView.findViewById(R.id.tv_item_detail_title);
            timeTv = (TextView) itemView.findViewById(R.id.tv_item_detail_time);
            desTv = (TextView) itemView.findViewById(R.id.tv_item_detail_des);
        }
    }
}
