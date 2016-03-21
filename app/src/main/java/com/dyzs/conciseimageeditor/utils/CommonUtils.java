package com.dyzs.conciseimageeditor.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dyzs.conciseimageeditor.view.MovableTextView2;
import com.dyzs.conciseimageeditor.view.MovableTextViewVer3;

/**
 * Created by maidou on 2016/3/16.
 */
public class CommonUtils {
    public static void closeKeyboard(MovableTextViewVer3 mtv, Context context) {
        InputMethodManager inputManger = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManger.hideSoftInputFromWindow(mtv.getWindowToken(), 0);
    }

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
}
