package com.dyzs.conciseimageeditor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dyzs.conciseimageeditor.utils.ScreenUtils;
import com.dyzs.conciseimageeditor.view.MovableTextView;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;
import com.dyzs.conciseimageeditor.utils.BitmapUtils;
import com.dyzs.conciseimageeditor.view.MovableTextView2;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUIActivity extends Activity {
    private Context mContext;
    // topToolbar
    private ImageView bt_save;
    // mainContent
    private ImageView iv_main_image;
    private RecyclerView mRecyclerView_filter_list;
    // bottomToolbar
    private RadioGroup main_radio;
    private RadioButton rb_word;

    // tempTest
    private ArrayList<MovableTextView> mMtvLists;
    private HashMap<Integer, MovableTextView> mMtvHashmap;


    private Bitmap mainBitmap;
    private Bitmap copyBitmap;
    private Bitmap filterSampleIconBitmap;
    private float scaleXX;
    private float scaleYY;

    private FrameLayout fl_main_content;
    private RelativeLayout rl_work_panel;

    // 保存最后一次点击的滤镜的item
    private int lastClickPosition = 0;


    private static int mCurrImgId = R.mipmap.pic_bg_ch_style;
    public int keyboardHeight = 0;
    private LinearLayout ll_edit_panel;
    private View edit_panel_not_used;
    private LinearLayout ll_edit_panel_head;

    private EditText et_input_text;


    private SeekBar sb_fontSize;

    private boolean writeClickCount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置启动不弹出软键盘, 这样就可以监听到键盘的高度了, 得到键盘高度后再重新绘制
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main_ui);
        mContext = this;
        mMtvLists = new ArrayList<>();
        mMtvHashmap = new HashMap<>();
        initView();
        // 加载图片
        loadBitmap();

        handleListener();
    }
    private void initView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = (int) ((float) metrics.widthPixels / 1.5);
        imageHeight = (int) ((float) metrics.heightPixels / 1.5);
        System.out.println("imageWidth------:" + imageWidth + ":" + imageHeight);

        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        rl_work_panel = (RelativeLayout) findViewById(R.id.rl_work_panel);
        // topBar
        bt_save = (ImageView) findViewById(R.id.bt_save);
        // mainContent
        mRecyclerView_filter_list = (RecyclerView) findViewById(R.id.rv_filter_list);
        mRecyclerView_filter_list.setHasFixedSize(true);
        int spacingInPixels = 10; //getResources().getDimensionPixelSize(R.dimen.space);
        mRecyclerView_filter_list.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView_filter_list.setLayoutManager(layoutManager);

        mRecyclerView_filter_list.setAdapter(new FilterAdapter(this));

        iv_main_image = (ImageView) findViewById(R.id.iv_main_image);
        // bottomToolbar
        main_radio = (RadioGroup) findViewById(R.id.main_radio);
        rb_word = (RadioButton) findViewById(R.id.rb_word);


        ll_edit_panel = (LinearLayout) findViewById(R.id.ll_edit_panel);
        edit_panel_not_used = findViewById(R.id.edit_panel_not_used);
        ll_edit_panel_head = (LinearLayout) findViewById(R.id.ll_edit_panel_head);
        et_input_text = (EditText) findViewById(R.id.et_input_text);

        sb_fontSize = (SeekBar) findViewById(R.id.sb_fontSize);


        ll_edit_panel.setVisibility(View.INVISIBLE);
    }
    private void loadBitmap() {
//        String filePath = "file:/" + Environment.getExternalStorageDirectory().getPath()
//                            + "/PictureTest/saveTemp.jpg";
//        System.out.println("=======" + filePath);
//        loadImage(filePath);
//        mainBitmap = loadImage();
        mainBitmap = BitmapUtils.loadImage(this, mCurrImgId, imageWidth, imageHeight);
        copyBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
        System.out.println("mainBitmap------:" + mainBitmap.getWidth() + ":" + mainBitmap.getHeight());
        iv_main_image.setImageBitmap(copyBitmap);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
//                copyBitmap.getWidth(), copyBitmap.getHeight());
//        rl_work_panel.setLayoutParams(layoutParams);
//        rl_work_panel.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private void handleListener() {
        bt_save.setOnClickListener(new SaveClickListener());
        iv_main_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 先关闭软盘
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Toast.makeText(mContext, "image view is click", Toast.LENGTH_SHORT).show();
                addMovableTextView();
                return false;
            }
        });

        main_radio.setOnCheckedChangeListener(new CurrentRadioGroupOnCheckChangeListener());
        rb_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataAndOpenPanelEdit();
            }
        });


        // 监听获取键盘高度, 只能监听到打开与关闭
        SoftKeyBoardListener.setListener(MainUIActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if (height != 0) {
                    keyboardHeight = height;
                }
            }
            @Override
            public void keyBoardHide(int height) {
                if (height != 0) {
                    keyboardHeight = height;
                }
            }
        });

        edit_panel_not_used.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_edit_panel.getVisibility() == View.VISIBLE) {
                    ll_edit_panel.setVisibility(View.INVISIBLE);
                }
            }
        });

        et_input_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                System.out.println("hasFocus value:" + hasFocus);
                if (hasFocus == false) {
                    // 关闭软键盘
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_input_text.getWindowToken(),0);
                }
            }
        });
    }

    private void getDataAndOpenPanelEdit() {
        ll_edit_panel.setVisibility(View.VISIBLE);
        for (MovableTextView mtv:mMtvLists) {
            if (mtv.hasFocus()) {
                float textSize = mtv.getTextSize();
                sb_fontSize.setProgress(25);
            }
        }
    }

    private class SaveClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (mMtvLists.size() <= 0) return;

            int saveLeft, saveBottom;
            float leaveW = 0.0f, leaveH = 0.0f;

            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(0);
            mPaint.setColor(Color.BLUE);
//            处理滤镜
//            copyBitmap = PhotoProcessing.filterPhoto(copyBitmap, lastClickPosition);
            Canvas canvas = new Canvas(copyBitmap);
            for (MovableTextView mtv:mMtvLists){
                mtv.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST));
                int textViewL = mtv.getLeft();
                int textViewT = mtv.getTop();
                int textViewR = mtv.getRight();
                int textViewB = mtv.getBottom();
                float textViewW = mtv.getWidth() * 1.0f;
                float textViewH = mtv.getHeight() * 1.0f;

                float imgW = iv_main_image.getWidth() * 1.0f;
                float imgH = iv_main_image.getHeight() * 1.0f;
                float bitW = copyBitmap.getWidth() * 1.0f;
                float bitH = copyBitmap.getHeight() * 1.0f;
                scaleXX = bitW / imgW;
                scaleYY = bitH / imgH;
                float scale = scaleXX > scaleYY ? scaleXX : scaleYY;
                System.out.println("scale------:" + scaleXX + ":" + scaleYY);
                if (scaleXX > scaleYY) {
                    leaveH = (imgH - bitH / scale) / 2;
                } else {
                    leaveW = (imgW - bitW / scale) / 2;
                }

                float textSize = mtv.getTextSize() * scale;
                mPaint.setTextSize(textSize);
                float textSpacing = (textViewH - textSize / scale);    // 获取画笔要绘画的文本的高度
                System.out.println("leave:" + leaveW + ":" + leaveH + "//spacing:" + textSpacing);
                saveLeft = (int) ((textViewL - leaveW) * scale);
                saveBottom = (int) (((textViewB - leaveH) - textSpacing) * scale);
                System.out.println("save:" + saveLeft + ":" + saveBottom);
                canvas.drawText(
                        mtv.getText().toString(),
                        saveLeft, saveBottom,
                        mPaint
                );
                // 把mtv从父控件中移除
//                fl_main_content.removeView(mtv);
            }
            iv_main_image.setImageBitmap(copyBitmap);
            mMtvLists.clear();
            // 保存到本地目录中
            String fileName = "save" + System.currentTimeMillis();
            String doneWithFileAbs = BitmapUtils.saveBitmap(copyBitmap, fileName);
        }
    }


    private class MTVClickListener implements MovableTextView2.OnCustomClickListener {
        @Override
        public void onCustomClick() {
            writeClickCount = true;
            if (writeClickCount) {
//                ll_edit_panel.setVisibility(View.VISIBLE);
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            } else {
//                ll_edit_panel.setVisibility(View.INVISIBLE);
            }
        }
    }


    // ====================task line
    private LoadImageTask mLoadImageTask;
    private int imageWidth, imageHeight;		// 展示图片控件 宽 高
    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }
    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.loadImageByPath(params[0], imageWidth, imageHeight);
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
            iv_main_image.setImageBitmap(result);
        }
    }// end inner class

    private int addMtvChildCount = 0;
    private String text = "请输入文字~";
    private void addMovableTextView() {
        final MovableTextView mtv = new MovableTextView(mContext);
        mtv.setTextColor(Color.RED);
        mtv.setParentFrameLayout(fl_main_content);
        mtv.setLongClickable(false);    //取消长按出现剪贴，复制，粘贴
        mtv.addTextChangedListener(new TextInputListener());//添加文本监听器
        mtv.setOnMoveListener(new MovableTextView.OnMoveListener() {
            @Override
            public void onMoveComplete(double x, double y) {
                x *= ScreenUtils.getScreenWidth(mContext);
                y *= ScreenUtils.getScreenHeight(mContext);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mtv.getLayoutParams();
                lp.gravity = -1;
                lp.leftMargin = (int) x;
                lp.topMargin = (int) y;
                lp.rightMargin = (int) (mtv.getParentWidth() - x - mtv.getMeasuredWidth());
                lp.bottomMargin = 0;
                mtv.setLayoutParams(lp);
            }
        });
        // 拖动中（按下后抬起前）关闭软键盘
        mtv.setmOnMovinglistener(new MovableTextView.OnMovingListener() {
            @Override
            public void onMovingComplete() {
                mtv.clearFocus();
                // 关闭虚拟键盘
                InputMethodManager inputManger = (InputMethodManager) mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputManger.hideSoftInputFromWindow(mtv.getWindowToken(), 0);
            }
        });


        fl_main_content.addView(mtv);
        mMtvLists.add(mtv);
        System.out.println("mMtvLists count:" + mMtvLists.size());
    }

    /**
     * 监控用户输入
     */
    private class TextInputListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }










    // --- listener 底部的 RadioGroup, 切换监听
    private class CurrentRadioGroupOnCheckChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_word:
                    // Show Text Editor
                    break;
                case R.id.rb_sticker:
                    // Show Sticker ImageList RecycleView
                    break;
                case R.id.rb_filter:
                    // Show Filter ImageList RecycleView
                    break;
                default:
                    System.out.println("出现未知错误：" + checkedId);
                    break;
            }
        }
    }


    // inner class start 滤镜的处理方法，暂时不用
    private class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private Context context;
        public FilterAdapter(Context context) {
            this.context = context;
        }
        @Override
        public int getItemCount() {
            return PhotoProcessing.FILTERS.length;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_view_filter, null);
            FilterHolder holder = new FilterHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            FilterHolder h = (FilterHolder) holder;
            String name = PhotoProcessing.FILTERS[position];
            h.text.setText(name);
            if (filterSampleIconBitmap != null && !filterSampleIconBitmap.isRecycled()) {
                filterSampleIconBitmap = null;
            }
            filterSampleIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_icon_filter_sample);
            if (position == 0) {
                h.icon.setImageBitmap(filterSampleIconBitmap);
            }
            else {
                h.icon.setImageBitmap(PhotoProcessing.filterPhoto(filterSampleIconBitmap, position));
            }
            h.icon.setOnClickListener(new FilterClickListener(position));
        }
        public class FilterClickListener implements View.OnClickListener{
            private int clickPosition;
            public FilterClickListener(int position) {
                this.clickPosition = position;
            }
            @Override
            public void onClick(View v) {
                lastClickPosition = clickPosition;
                FilterTask filterTask = new FilterTask();
                filterTask.execute(clickPosition + "");
            }
        }
        public class FilterHolder extends RecyclerView.ViewHolder {
            public ImageView icon;
            public TextView text;
            public FilterHolder(View itemView) {
                super(itemView);
                this.icon = (ImageView) itemView.findViewById(R.id.icon);
                this.text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
    // inner class end
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        public SpaceItemDecoration(int space) {
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

    private final class FilterTask extends AsyncTask<String, Void, Bitmap> {
        private ProgressDialog loadDialog;
        public FilterTask() {
            super();
            loadDialog = new ProgressDialog(MainUIActivity.this, R.style.MyDialog);
            loadDialog.setCancelable(false);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadDialog.show();
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            int position = Integer.parseInt(params[0]);
            return PhotoProcessing.filterPhoto(
                    BitmapUtils.loadImage(MainUIActivity.this, mCurrImgId, fl_main_content),
                    position
            );
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            loadDialog.dismiss();
            iv_main_image.setImageBitmap(result);
            System.gc();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loadDialog.dismiss();
        }
    }// end inner class



    // 显示dialog     useless
//    private void showQuickOption() {
//        final QuickOptionDialog dialog = new QuickOptionDialog(
//                MainUIActivity.this, movableTextView2);
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();
//    }
}
