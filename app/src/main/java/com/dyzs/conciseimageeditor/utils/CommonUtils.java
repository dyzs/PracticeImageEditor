package com.dyzs.conciseimageeditor.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dyzs.conciseimageeditor.R;
import com.dyzs.conciseimageeditor.view.MovableTextView2;

/**
 * Created by maidou on 2016/3/16.
 */
public class CommonUtils {

    public static void closeKeyboard(MovableTextView2 mtv, Context context) {
        InputMethodManager inputManger = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(mtv.getWindowToken(), 0);
    }

    public static void closeKeyboard(EditText et, Context context) {
        InputMethodManager inputManger = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static void closeKeyboard(View view, Context context) {
        InputMethodManager inputManger = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * @details 当键盘打开的时候，点击则关闭 && 当键盘关闭的时候，点击则打开
     */
    public static void hitKeyboardOpenOrNot(Context context) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


//    public static int listenKeyboard(final View view) {
//        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                view.getWindowVisibleDisplayFrame(r);
//                int visibleHeight = r.height();
//                if (mVisibleHeight == 0) {
//                    mVisibleHeight = visibleHeight;
//                    return;
//                }
//                if (mVisibleHeight == visibleHeight) {
//                    return;
//                }
//                return visibleHeight;
//                System.out.println("mVisibleHeight-----:" + mVisibleHeight);
//            }
//        });
//        return 0;
//    }

    public static int matchColor(int seekBarProgress) {
        int ret = 0;
        if (seekBarProgress >= 0 && seekBarProgress < 10) {
            ret = 0;
        }
        if (seekBarProgress >= 10 && seekBarProgress < 20) {
            ret = 1;
        }
        if (seekBarProgress >= 20 && seekBarProgress < 30) {
            ret = 2;
        }
        if (seekBarProgress >= 30 && seekBarProgress < 40) {
            ret = 3;
        }
        if (seekBarProgress >= 40 && seekBarProgress < 50) {
            ret = 4;
        }
        if (seekBarProgress >= 50 && seekBarProgress < 60) {
            ret = 5;
        }
        if (seekBarProgress >= 60 && seekBarProgress < 70) {
            ret = 6;
        }
        if (seekBarProgress >= 70 && seekBarProgress < 80) {
            ret = 7;
        }
        if (seekBarProgress >= 80 && seekBarProgress < 90) {
            ret = 8;
        }
        return ret;
    }

}
