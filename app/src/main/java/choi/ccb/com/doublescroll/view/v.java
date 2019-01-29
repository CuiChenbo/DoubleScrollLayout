package choi.ccb.com.doublescroll.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class v extends ViewGroup {
    public v(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
