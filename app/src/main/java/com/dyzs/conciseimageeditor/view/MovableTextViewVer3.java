package com.dyzs.conciseimageeditor.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dyzs.conciseimageeditor.R;
import com.dyzs.conciseimageeditor.utils.ScreenUtils;

/**
 * Created by wjk on 2015/8/10.
 * 可在屏幕来回拖动的TextView
 */
public class MovableTextViewVer3 extends EditText {

    private Context context;
    private OnMovingListener mOnMovinglistener;

    public OnMovingListener getmOnMovinglistener() {
        return mOnMovinglistener;
    }

    public void setmOnMovinglistener(OnMovingListener mOnMovinglistener) {
        this.mOnMovinglistener = mOnMovinglistener;
    }

    public MovableTextViewVer3(Context context) {
        super(context, null);
        this.context = context;
        init();
    }

    public MovableTextViewVer3(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        init();
    }

    public MovableTextViewVer3(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }
    private void init() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
//        this.setHintTextColor(Color.MAGENTA);
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.pic_bg_edit_text_9patch_3x3));
        this.setBackgroundResource(R.drawable.shape_dotted);
        this.setText("请输入文字~");
        this.setTextColor(Color.RED);
        this.setClickable(true);
        this.setFocusable(true);
        this.setTextSize(getResources().getDimension(R.dimen.movable_text_view_default_text_size));
        this.setLayoutParams(layoutParams);
    }


    private float startx;// down事件发生时，手指相对于view左上角x轴的距离
    private float starty;// down事件发生时，手指相对于view左上角y轴的距离

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指刚触摸到屏幕的那一刻，手指相对于View左上角水平和竖直方向的距离:startX startY
                setFocusable(true);//添加
                setFocusableInTouchMode(true);//添加
                startx = event.getX();
                starty = event.getY();
                setParentScrollAble(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if(mOnMovinglistener != null){
                    mOnMovinglistener.onMovingComplete();//接口回调
                }
                // 手指停留在屏幕或移动时，手指相对与View左上角水平和竖直方向的距离:endX endY
                float endX = event.getX();
                float endY = event.getY();
                // 获取此时刻 View的位置。
                int left = getLeft();
                int top = getTop();
                int right = getRight();
                int bottom = getBottom();
                // 手指移动的水平距离
                int offsetX = (int) (endX - startx);
                // 手指移动的竖直距离
                int offsetY = (int) (endY - starty);
                // 当手指在水平或竖直方向上发生移动时，重新设置View的位置（layout方法）
                if (offsetX != 0 || offsetY != 0) {
                    if (parentHeight > 0 || parentWidth > 0) {
                        if (offsetY < 0 && top < 20) {//如果是向上拖动，快要靠近上边的时候自动贴边
                            if (offsetX > 0 && left > parentWidth - getMeasuredWidth() / 2 || offsetX < 0 && right < getMeasuredWidth() / 2) {
                                //如果是向左右拖动，只允许拖出当前View的一半宽度
                                break;
                            }
                            layout(left + offsetX, 0, right + offsetX, getMeasuredHeight());
                            return true;
                        }
                        if (offsetY > 0 && parentHeight - bottom < 20) {//如果是向下拖动，快要靠近下边的时候自动贴边
                            if (offsetX > 0 && left > parentWidth - getMeasuredWidth() / 2 || offsetX < 0 && right < getMeasuredWidth() / 2) {
                                //如果是向左右拖动，只允许拖出当前View的一半宽度
                                break;
                            }
                            layout(left + offsetX, parentHeight - getMeasuredHeight(), right + offsetX, parentHeight);
                            return true;
                        }
                        if (offsetX > 0 && left > parentWidth - getMeasuredWidth() / 2) {//如果是向右拖动，只允许拖出当前View的一半宽度
                            return true;
                        }
                        if (offsetX < 0 && right < getMeasuredWidth() / 2) {//如果是向左拖动，只允许拖出当前View的一半宽度
                            return true;
                        }
                    }
                    layout(left + offsetX, top + offsetY, right + offsetX, bottom + offsetY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (onMoveListener != null) {
                    onMoveListener.onMoveComplete((double)getLeft() / ScreenUtils.getScreenWidth(context)
                            , (double)getTop() / ScreenUtils.getScreenHeight(context));
                }
                //当手指松开时，让父 ViewGroup 重新拿到onTouch权限
                setParentScrollAble(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                setParentScrollAble(true);
                break;
        }
        return super.onTouchEvent(event);
    }

    private int parentWidth;
    private int parentHeight;

    /**
     * 设置父控件或者参考控件的宽高
     *
     * @param width  w
     * @param height h
     */
    public void setParentSize(int width, int height) {
        parentWidth = width;
        parentHeight = height;
    }

    public int getParentWidth(){
        return parentWidth;
    }

    private OnMoveListener onMoveListener;

    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    public interface OnMoveListener {
        void onMoveComplete(double x, double y);
    }

    private ViewGroup parentLayout;

    public void setParentLayout(ViewGroup parent) {
        this.parentLayout = parent;
    }

    /**
     * 是否把滚动事件交给父scrollview
     * @param flag boolean
     */
    private void setParentScrollAble(boolean flag) {
        parentLayout.requestDisallowInterceptTouchEvent(!flag);
    }

    //按下正在移动中还没抬起
    public interface OnMovingListener {
        void onMovingComplete();
    }

}

