//package com.dyzs.conciseimageeditor.view;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.Typeface;
//import android.graphics.drawable.Drawable;
//import android.text.SpannableString;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//
//import com.anybeen.mark.app.R;
//import com.anybeen.mark.common.utils.DensityUtils;
//import com.anybeen.mark.common.utils.ScreenUtils;
//
///**
// * Created by wjk on 2015/8/10.
// * 可在屏幕来回拖动的TextView
// */
//public class MovableTextViewVer3 extends EditText {
//
//    private Context context;
//    private OnMovingListener mOnMovinglistener;
//
//    public OnMovingListener getmOnMovinglistener() {
//        return mOnMovinglistener;
//    }
//
//    public void setmOnMovinglistener(OnMovingListener mOnMovinglistener) {
//        this.mOnMovinglistener = mOnMovinglistener;
//    }
//
//    public MovableTextViewVer3(Context context) {
//        super(context, null);
//        this.context = context;
//        init();
//    }
//
//    public MovableTextViewVer3(Context context, AttributeSet attrs) {
//        super(context, attrs, 0);
//        this.context = context;
//        init();
//    }
//
//    public MovableTextViewVer3(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        this.context = context;
//        init();
//    }
//    private void init() {
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.pic_bg_edit_text_9patch_3x3));
//        this.setBackgroundResource(R.drawable.shape_dotted);
//        this.setHintTextColor(Color.WHITE);
//        this.setHint("在此输入标注文字");// 长按添加或删除~
//        this.setTextColor(Color.WHITE);
//        this.setClickable(true);
//        this.setFocusable(true);
//        this.setLongClickable(false);    //取消长按出现剪贴，复制，粘贴
//        this.setTextSize(DensityUtils.px2sp(context, getResources().getDimension(R.dimen.movable_text_view_default_text_size)));
//        this.setTypefaceName("default");
//        this.setTypeface(Typeface.DEFAULT);
//        this.setLayoutParams(layoutParams);
//        // this.setHorizontallyScrolling(true);
//        this.setSingleLine(true);
//        this.setMaxEms(12);
//        this.setColorR(255);
//        this.setColorG(255);
//        this.setColorB(255);
//        this.hasContent = false;
//
////        deleteIcon = getResources().getDrawable(R.mipmap.icon_text_del);
////        deleteIcon.setBounds(
////                0,
////                0,
////                (int) DensityUtils.px2dp(getContext(), 70),
////                (int) DensityUtils.px2dp(getContext(), 70)
////                //deleteIcon.getIntrinsicHeight() + (int) DensityUtils.px2dp(getContext(), 5)
////        );
////        setCompoundDrawables(null, null, deleteIcon, null);
//    }
//
////    private Drawable deleteIcon;
//    private int startX;// down事件发生时，手指相对于view左上角x轴的距离
//    private int startY;// down事件发生时，手指相对于view左上角y轴的距离
//    private long hitTimeMillis;
//    private long upTimeMillis;
//    private int endX, endY;
//    private int mCurOffsetLeft, mCurOffsetTop;
//    private int downLeft, downTop;
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
//        // Drawable rightIcon = getCompoundDrawables()[2];
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                hitTimeMillis = System.currentTimeMillis();
//                // 手指刚触摸到屏幕的那一刻，手指相对于View左上角水平和竖直方向的距离:startX startY
////                setFocusable(true);//添加
////                setFocusableInTouchMode(true);//添加
//                startX = (int) event.getX();
//                startY = (int) event.getY();
//                downLeft = getLeft();
//                downTop = getTop();
////                System.out.println("downLeft:" + getLeft());
////                System.out.println("downTop:" + getTop());
//                setParentScrollAble(false);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if(mOnMovinglistener != null){
//                    mOnMovinglistener.onMovingComplete();//接口回调
//                }
//                endX = (int) event.getX();
//                endY = (int) event.getY();
//                // 手指移动的水平距离
//                int offsetX = (endX - startX);
//                // 手指移动的竖直距离
//                int offsetY = (endY - startY);
//                // 获取此时刻 View的位置。
//                int left = getLeft();
//                int top = getTop();
//                int right;
//                int bottom;
//                left += offsetX;
//                top += offsetY;
//                right = left + getWidth();
//                bottom = top + getHeight();
////                if (offsetX != 0 || offsetY != 0) {
//                    layout(left, top, right, bottom);
////                }
//                int upLeft = getLeft();
//                int upTop = getTop();
//                mCurOffsetLeft = upLeft - downLeft;
//                mCurOffsetTop = upTop - downTop;
////                System.out.println("upLeft:" + getLeft());
////                System.out.println("upTop:" + getTop());
//                upTimeMillis = System.currentTimeMillis();
//                long offsetTimeMillis = upTimeMillis - hitTimeMillis;
//                if(offsetTimeMillis < 2000 && offsetTimeMillis> 500 && mOnDeleteListener != null
//                        && Math.abs(mCurOffsetLeft) < 5 && Math.abs(mCurOffsetTop) < 5)
//                {
//                    // go custom longClick
//                    mOnDeleteListener.onLongClickDelete();
////                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                if (onMoveListener != null) {
//                    onMoveListener.onMoveComplete((double) getLeft() / ScreenUtils.getScreenWidth(context)
//                            , (double) getTop() / ScreenUtils.getScreenHeight(context));
//                }
//                // 删除图标
////                if (deleteIcon != null && event.getAction() == MotionEvent.ACTION_UP) {
////                    if (touchable) {
////                        if (getText().length() == 0) {
////                            this.setText("");
////                            break;
////                        }
////                        String text = getText().toString();
////                        if (text != null) {
////                            this.setText(text.substring(0, text.length() - 1));
////                        }
////                    }
////                }
//                //当手指松开时，让父 ViewGroup 重新拿到onTouch权限
//                setParentScrollAble(true);
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                setParentScrollAble(true);
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
//
//    private int parentWidth;
//    private int parentHeight;
//
//    /**
//     * 设置父控件或者参考控件的宽高
//     *
//     * @param width  w
//     * @param height h
//     */
//    public void setParentSize(int width, int height) {
//        parentWidth = width;
//        parentHeight = height;
//    }
//
//    public int getParentWidth(){
//        return parentWidth;
//    }
//    public int getParentHeight() { return parentHeight; }
//
//    private OnMoveListener onMoveListener;
//
//    public void setOnMoveListener(OnMoveListener onMoveListener) {
//        this.onMoveListener = onMoveListener;
//    }
//
//
//    public interface OnMoveListener {
//        void onMoveComplete(double x, double y);
//    }
//
//    private ViewGroup parentLayout;
//
//    public void setParentLayout(ViewGroup parent) {
//        this.parentLayout = parent;
//    }
//
//    /**
//     * 是否把滚动事件交给父scrollview
//     * @param flag boolean
//     */
//    private void setParentScrollAble(boolean flag) {
//        parentLayout.requestDisallowInterceptTouchEvent(!flag);
//    }
//
//    //按下正在移动中还没抬起
//    public interface OnMovingListener {
//        void onMovingComplete();
//    }
//
//    // 标记当前控件是否有文本内容
//    private boolean hasContent;
//    public boolean isHasContent() {
//        return hasContent;
//    }
//    public void setHasContent(boolean hasContent) {
//        this.hasContent = hasContent;
//    }
//
//    private String typefaceName;
//    public String getTypefaceName() {
//        return typefaceName;
//    }
//    public void setTypefaceName(String typeface) {
//        this.typefaceName = typeface;
//    }
//
//    private int ColorR;
//    private int ColorG;
//    private int ColorB;
//
//    public int getColorR() {
//        return ColorR;
//    }
//
//    public void setColorR(int colorR) {
//        ColorR = colorR;
//    }
//
//    public int getColorG() {
//        return ColorG;
//    }
//
//    public void setColorG(int colorG) {
//        ColorG = colorG;
//    }
//
//    public int getColorB() {
//        return ColorB;
//    }
//
//    public void setColorB(int colorB) {
//        ColorB = colorB;
//    }
//
//    /**
//     * @details 执行长按删除的监听事件
//     */
//    public interface OnDeleteListener{
//        void onLongClickDelete();
//    }
//    private OnDeleteListener mOnDeleteListener;
//    public void setOnLongClickDeleteListener(OnDeleteListener l) {
//        this.mOnDeleteListener = l;
//    }
//}
//
