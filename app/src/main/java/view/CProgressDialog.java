package view;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.gjzg.R;

/**
 * Created by Administrator on 2017/7/27.
 */
//自定义进度对话框
public class CProgressDialog extends Dialog {

    private View dialogView;
    private WindowManager.LayoutParams lp;
    private Context mContext;
    private ImageView imageIv;
    private ObjectAnimator animator;

    public CProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initDialog();
    }

    private void initDialog() {
        dialogView = View.inflate(mContext, R.layout.dialog_cprogress, null);
        imageIv = (ImageView) dialogView.findViewById(R.id.iv_dialog_cprogress);
        animator = ObjectAnimator.ofFloat(imageIv, "rotation", 0f, 359f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(500);
        animator.setRepeatCount(-1);
        this.setContentView(dialogView);
        lp = this.getWindow().getAttributes();
        lp.dimAmount = 0f;
        this.getWindow().setAttributes(lp);
        this.setCanceledOnTouchOutside(false);
    }

    public void cShow() {
        this.show();
        animator.start();
    }

    public void cDismiss() {
        animator.cancel();
        this.dismiss();
    }
}
