package com.dyzs.conciseimageeditor.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dyzs.conciseimageeditor.R;

/**
 * Created by maidou on 2016/2/19.
 */
public class MovableTextView2 extends TextView{
    public MovableTextView2(Context context) {
        this(context, null);
    }

    public MovableTextView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovableTextView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // this.setHint("请输入文字~");
        this.setBackgroundResource(R.drawable.dotted_shape);
        this.setHintTextColor(Color.MAGENTA);
        this.setTextColor(Color.RED);
        this.setClickable(true);
        this.setFocusable(true);
        this.setTextSize(getResources().getDimension(R.dimen.movable_text_view_default_text_size));
        this.setLayoutParams(layoutParams);
    }

    public enum OperateState{
        STATE_MOVING, STATE_SELECTED, STATE_UNSELECTED
    }
    private static final int ACTION_STATE_DOWN = 1;
    private static final int ACTION_STATE_MOVE = 2;
    private static final int ACTION_STATE_UP = 3;
    private OperateState operateState = OperateState.STATE_UNSELECTED;
    public void setBackgroundRes() {
        // 判断如果是在选中状态或者是滑动状态，则添加背景
        if(operateState == OperateState.STATE_MOVING || operateState == OperateState.STATE_SELECTED) {
            this.setBackgroundResource(R.drawable.dotted_shape);
        } else if (operateState == OperateState.STATE_UNSELECTED) {
            this.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private int startX;
    private int startY;

    private int checkStartX;
    private int checkStartY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // this.getParentParams();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            startX = (int) event.getRawX();
            startY = (int) event.getRawY();
            checkStartX = (int) event.getRawX();
            checkStartY = (int) event.getRawY();
            operateState = OperateState.STATE_SELECTED;
            return true;
        case MotionEvent.ACTION_MOVE:
            int movingX = (int) event.getRawX();
            int movingY = (int) event.getRawY();
            // System.out.println("moving:" + movingX + "," + movingY);
            // 开始计算移动时的偏移量
            int offsetX = movingX - startX;
            int offsetY = movingY - startY;
            // 获取控件没有重新绘制时控件距离父控件左边框和顶边框的距离
            int l = this.getLeft();
            int t = this.getTop();
            // 移动相应的偏移量
            l += offsetX;
            t += offsetY;
            int r = l + this.getWidth();
            int b = t + this.getHeight();
            // 允许超过屏幕,要不然会出现卡顿的现象
//            if( l < 0 || r > mParentWidth || t < 0 || b > mParentHeight){
//                break;
//            }
            this.layout(l, t, r, b);
            startX = movingX;
            startY = movingY;
            operateState = OperateState.STATE_MOVING;
            return true;
        case MotionEvent.ACTION_UP:
            operateState = OperateState.STATE_UNSELECTED;
            int upX = (int) event.getRawX();
            int upY = (int) event.getRawY();
            if (Math.abs(upX - checkStartX) < 15 && Math.abs(upY - checkStartY) < 15) {
                System.out.println("go click event");
                if (mOnCustomClickListener != null) {
                    mOnCustomClickListener.onCustomClick();
                }
                // 不让滑动事件继续消费当前事件
                return false;
            }
            finalLeft = this.getLeft();
            finalTop = this.getTop();
            // 保存值集合,可以用来做reload
            if (mOnActionUpListener != null) {
                mOnActionUpListener.getStartPosition(finalLeft, finalTop);
            }
            // return true;
            break;
        default:
            break;
        }
        return super.onTouchEvent(event);
    }
    private int finalLeft;
    private int finalTop;
    public int getFinalLeft() {
        return finalLeft;
    }

    public int getFinalTop() {
        return finalTop;
    }
    /**
     * 用来代替点击事件
     */
    private OnCustomClickListener mOnCustomClickListener;



    public interface OnCustomClickListener {
        void onCustomClick();
    }
    public void setOnCustomClickListener(OnCustomClickListener listener) {
        if (listener != null) {
            this.mOnCustomClickListener = listener;
        }
    }


    private OnActionUpListener mOnActionUpListener;
    public interface OnActionUpListener {
        void getStartPosition(int startX, int startY);
    }
    public void setOnActionUpListener(OnActionUpListener listener) {
        if (listener != null) {
            this.mOnActionUpListener = listener;
        }
    }







//    //标题的双击事件
//    public void tv_btnnnnnnnnnnn(View v){
//        /**
//         * src the source array to copy the content. 拷贝的原数组 srcPos the
//         * starting index of the content in src. 从原数组的那个位置开始拷贝 dst the
//         * destination array to copy the data into. 拷贝目标数组 dstPos the
//         * starting index for the copied content in dst. 从目标数组的那个位置去写
//         * length the number of elements to be copied. 拷贝的长度
//         */
//        //表示从原数组的 1 拷贝给 0 的位置，拷贝一个，所以长度为 length -1
//        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//        //获取离开机的时间，毫秒值，不包含手机休眠时间
//        mHits[mHits.length-1] = SystemClock.uptimeMillis();
//        //判断数组的第一个元素去是否再次获取离开机的时间再减去300毫秒大，如果大于执行点击操作，小于则不执行
//        if(mHits[0] >= (SystemClock.uptimeMillis() - 300)){
//            System.out.println("我被点击了");
//            if(longClickCount == 0){
//                tv_softwaremanager_storage.setVisibility(View.INVISIBLE);
//                longClickCount ++ ;
//            }else{
//                tv_softwaremanager_storage.setVisibility(View.VISIBLE);
//                longClickCount -- ;
//            }
//        }
//    }





























//    private int mParentWidth;
//    private int mParentHeight;
//    private int pLeft, pRight, pTop, pBottom;
//    private ViewGroup parent;
//    private void getParentParams() {
//        ViewParent parent = this.getParent();
//        this.parent = (ViewGroup) parent;
//        mParentWidth = this.parent.getWidth();
//        mParentHeight = this.parent.getHeight();
//        pLeft = this.parent.getLeft();
//        pRight = this.parent.getRight();
//        pTop = this.parent.getTop();
//        pBottom = this.parent.getBottom();
//        // System.out.println("坐上右下:"+pLeft+"/"+pTop+"/"+pRight+"/"+pBottom);
//    }
}
