package com.an.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.an.customview.R;

class GradientColorDialog extends Dialog implements View.OnClickListener {

    private Context _context;    // 上下文
    private int _layoutResId;    // 布局文件Id
    private int[] _listenItemId; // 监听的控件Id
    private OnItemClickListener _listener;

    public GradientColorDialog(Context context, int layoutResId, int[] listenItemId) {
        super(context, R.style.MyDialog);

        _context = context;
        _layoutResId = layoutResId;
        _listenItemId = listenItemId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);     // 居中显示
        setContentView(_layoutResId);

        WindowManager windowManager = ((Activity) _context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 3 / 5;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);
        for (int id : _listenItemId) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();    //注意：我在这里加了这句话，表示只要按任何一个控件的id,弹窗都会消失，不管是确定还是取消。
        _listener.OnClick(this, view);
    }

    public interface OnItemClickListener {
        void OnClick(GradientColorDialog dialog, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        _listener = listener;
    }
}
