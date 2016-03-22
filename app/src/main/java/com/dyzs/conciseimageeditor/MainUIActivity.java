package com.dyzs.conciseimageeditor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dyzs.conciseimageeditor.entity.MatrixInfo;
import com.dyzs.conciseimageeditor.utils.ColorUtil;
import com.dyzs.conciseimageeditor.utils.CommonUtils;
import com.dyzs.conciseimageeditor.utils.DensityUtils;
import com.dyzs.conciseimageeditor.utils.FileUtils;
import com.dyzs.conciseimageeditor.utils.ToastUtil;
import com.dyzs.conciseimageeditor.view.CustomSeekBar;
import com.dyzs.conciseimageeditor.view.ProgressItem;
import com.dyzs.conciseimageeditor.view.StickerView;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
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
    private RadioButton rb_sticker;

    // tempTest
    private ArrayList<MovableTextView2> mMtvLists;

    private int imageWidth, imageHeight;
    private Bitmap mainBitmap;
    private Bitmap copyBitmap;
    private Bitmap filterSampleIconBitmap;

    private FrameLayout fl_main_content;



    private static int mCurrImgId = R.mipmap.pic_bg_1920x1080x003;
    public int keyboardHeight = 0;      // 记录键盘高度

    //存储贴纸列表
    private ArrayList<View> mViews;

    //当前处于编辑状态的贴纸
    private StickerView mCurrentView;

    private ArrayList<StickerView> mStickerViewLists;
    private float[] scaleAndLeaveSize;
    private FrameLayout fl_image_editor_base_layout;


    // edit panel params
    private LinearLayout edit_panel;            // 文字编辑面板，使用 LayoutInflate
    private ImageView ivKeyboardOptions;
    private EditText etOperateText;
    private Button btnComplete;
    private SeekBar sbFontSize;
    private CustomSeekBar csbFontColor;
    private float totalSpan = 9;
    private float[] colorSpan = {1, 1, 1, 1, 1, 1, 1, 1, 1};
    private int[] colorValues = {R.color.rainbow_red, R.color.rainbow_orange, R.color.rainbow_yellow,
            R.color.rainbow_green, R.color.rainbow_blue, R.color.rainbow_cyan,
            R.color.rainbow_purple, R.color.rainbow_black, R.color.rainbow_white};    // 红橙黄绿蓝靛紫黑白
    private ArrayList<ProgressItem> progressItemList;
    private ProgressItem mProgressItem;

    // edit panel params


    private boolean openKeyboardOnLoading;
    private boolean createMtvOnLoading;
    private KeyboardState mCurKeyboardState;

    private enum KeyboardState {
        STATE_OPEN, STATE_HIDE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.activity_main_ui);
        mContext = this;
        mMtvLists = new ArrayList<>();
        mViews = new ArrayList<>();
        mStickerViewLists = new ArrayList<>();
        openKeyboardOnLoading = true;
        createMtvOnLoading = true;


        fl_image_editor_base_layout = (FrameLayout) findViewById(R.id.fl_image_editor_base_layout);

        initView();
        // 加载图片
        loadBitmap();

        handleListener();

        // reloadSticker();
        reloadStickerMore();
    }


    private void initView() {
        imageWidth = BitmapUtils.getScreenPixels(mContext).widthPixels;
        imageHeight = BitmapUtils.getScreenPixels(mContext).heightPixels;

        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
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
        rb_sticker = (RadioButton) findViewById(R.id.rb_sticker);



        // ObjectAnimator.ofFloat(ll_edit_panel, "translationY", 0.0F, 360.0F).setDuration(500).start();
//        Animation translateOut = AnimationUtils.loadAnimation(mContext, R.anim.dialog_exit_anim);
////        ViewHelper.setTranslationY(ll_edit_panel, 30f);
//        ll_edit_panel.startAnimation(translateOut);


    }
    private void loadBitmap() {
//        String filePath = "file:/" + Environment.getExternalStorageDirectory().getPath()
//                            + "/PictureTest/saveTemp.jpg";
//        System.out.println("=======" + filePath);
//        loadImage(filePath);
//        mainBitmap = loadImage();
        mainBitmap = BitmapUtils.loadImage(this, mCurrImgId, imageWidth, imageHeight);
        copyBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
        iv_main_image.setImageBitmap(copyBitmap);
        System.out.println("Image View:" + iv_main_image.getHeight());
    }

    private void handleListener() {
        // 监听获取键盘高度, 只能监听到打开与关闭
        SoftKeyBoardListener.setListener(MainUIActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                System.out.println("keyboard open");
                mCurKeyboardState = KeyboardState.STATE_OPEN;
                if (height != 0) {
                    if (createMtvOnLoading && openKeyboardOnLoading) {
                        keyboardHeight = height;
                        System.out.println("-->" + keyboardHeight);
                        SystemClock.sleep(300);
                        edit_panel = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_edit_panel, null);
                        fl_image_editor_base_layout.addView(edit_panel);

                        csbFontColor = (CustomSeekBar) edit_panel.findViewById(R.id.csb_edit_panel_font_color);

                        // 获取编辑 panel 的头高度
                        LinearLayout ll_edit_panel_head = (LinearLayout) edit_panel.findViewById(R.id.ll_edit_panel_head);
                        ll_edit_panel_head.measure(0, 0);
                        int headHeight = ll_edit_panel_head.getMeasuredHeight();
                        System.out.println("headHeight:" + headHeight);
                        // 获取系统的状态栏高度
                        Rect frame = new Rect();
                        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                        int statusBarHeight = frame.top;
                        System.out.println("statusBarHeight:" + statusBarHeight);
                        FrameLayout.LayoutParams lps = (FrameLayout.LayoutParams) edit_panel.getLayoutParams();
                        lps.height = keyboardHeight + headHeight;
                        lps.gravity = Gravity.BOTTOM;
                        edit_panel.setLayoutParams(lps);

                        addMovableTextView();
                        // TODO 获取文本，和文字颜色
                        createMtvOnLoading = false;
                        openKeyboardOnLoading = false;

                        initDataToSeekBar();
                    } else {
                        edit_panel.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void keyBoardHide(int height) {
                System.out.println("keyboard hide");
                mCurKeyboardState = KeyboardState.STATE_HIDE;
            }
        });


        bt_save.setOnClickListener(new SaveClickListener());
        // main_radio.setOnCheckedChangeListener(new CurrentRadioGroupOnCheckChangeListener());
        rb_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovableTextView();
            }
        });

        rb_sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStickerView();
            }
        });

        iv_main_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEditPanelAndCloseKb();
            }
        });
    }

    private void hideEditPanelAndCloseKb() {
        if (edit_panel.getVisibility() == View.VISIBLE) {
            edit_panel.setVisibility(View.INVISIBLE);
        }
        if (mCurKeyboardState == KeyboardState.STATE_OPEN) {
            mCurKeyboardState = KeyboardState.STATE_HIDE;
            CommonUtils.hitKeyboardOpenOrNot(mContext);
        }
    }


    private class SaveClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            // 在图片全部逻辑加载完成后，计算图片加载后与屏幕的缩放比与留白区域
            calcScaleAndLeaveSize();
            Canvas canvas = new Canvas(copyBitmap);
            saveStickerViews(canvas);

        }
    }

    private class MTVClickListener implements MovableTextView2.OnCustomClickListener {
        MovableTextView2 mMtv;
        public MTVClickListener(MovableTextView2 mtv2) {
            this.mMtv = mtv2;
        }
        @Override
        public void onCustomClick() {
            // 键盘状态是打开，同时还未执行任何点击，点击后关闭键盘，状态调整为关闭
            if (mCurKeyboardState == KeyboardState.STATE_OPEN && mMtv.getFirstClick()) {
                mMtv.setFirstClick(false);                       // 关闭当前控件的第一次点击
                edit_panel.setVisibility(View.INVISIBLE);       // 隐藏编辑面板
                CommonUtils.hitKeyboardOpenOrNot(mContext);     // 自动关闭键盘
            }

            // 键盘打开状态，同时第一次点击已经被消费，点击后关闭键盘，状态调整为关闭
            else if (mCurKeyboardState == KeyboardState.STATE_OPEN && !mMtv.getFirstClick()) {
                if (edit_panel.getVisibility() == View.VISIBLE) {
                    edit_panel.setVisibility(View.INVISIBLE);
                    CommonUtils.hitKeyboardOpenOrNot(mContext);     // 自动关闭键盘
                } else {
                    edit_panel.setVisibility(View.VISIBLE);
                }
            }

            // 键盘关闭状态，同时当前控件的第一次点击已经被消费，点击后打开键盘，状态恢复打开
            else if (mCurKeyboardState == KeyboardState.STATE_HIDE && mMtv.getFirstClick()) {
                if (edit_panel.getVisibility() == View.VISIBLE) {
                    edit_panel.setVisibility(View.INVISIBLE);
                } else {
                    edit_panel.setVisibility(View.VISIBLE);
                }
            }

            // 键盘关闭状态，同时当前控件的第一次点击已经被消费，点击后打开键盘，状态恢复打开
            else if (mCurKeyboardState == KeyboardState.STATE_HIDE && !mMtv.getFirstClick()) {
                if (edit_panel.getVisibility() == View.VISIBLE) {
                    edit_panel.setVisibility(View.INVISIBLE);
                } else {
                    edit_panel.setVisibility(View.VISIBLE);
                }
            }





            // 把 MovableTextView2 的数据载入到编辑面板中
            loadMtvDataIntoEditPanel(mMtv);
        }
    }

    private void loadMtvDataIntoEditPanel(MovableTextView2 movableTextView2) {
        if (edit_panel.getVisibility() == View.INVISIBLE) {return;}
        // TODO 遍历 mtv 集合，设置选中状态
        for (MovableTextView2 m : mMtvLists) {
            System.out.println("mMtvLists item selected：" + m.isSelected());
            m.setSelected(false);
            if (m.equals(movableTextView2)) {
                m.setSelected(true);
            }
        }
        System.out.println("movableTextView2：" + movableTextView2.isSelected());

//        movableTextView2

    }






    // ====================task line
    private LoadImageTask mLoadImageTask;
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

    // TODO 怎么判断处理当前的这个 mtv 是第一次点击
    private void addMovableTextView() {
        if (mMtvLists != null && mMtvLists.size() > 0) {
            for (MovableTextView2 m : mMtvLists) {
                m.setSelected(false);
                m.setFirstClick(false);
                System.out.println("mMtvLists :" + m.isSelected());
            }
        }

        final MovableTextView2 mtv = new MovableTextView2(mContext);
        mtv.setText("请输入文字~");
        mtv.setSelected(true);
        mtv.setOnCustomClickListener(new MTVClickListener(mtv));
        mtv.setFirstClick(true);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mtv.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        mtv.setLayoutParams(lp);
        fl_main_content.addView(mtv);
        mMtvLists.add(mtv);
    }

    /**
     * @details 通过参数值返回一个 {@link MovableTextView2} 对象
     * @param text          输入的文字
     * @param textSize      输入的文字大小
     * @param rgb           输入的文字颜色的rgb
     * @param typefaceName  输入的文字的字体名称
     * @param typeface      输入的文字的字体
     * @param parentView
     * @return
     */
    private MovableTextView2 generateMtv(
            String text,
            float textSize,
            int[] rgb,
            String typefaceName,
            Typeface typeface,
            final FrameLayout parentView) {
        final MovableTextView2 mtv = new MovableTextView2(mContext);
        if (text != null) {
            mtv.setText(text);
        }
        if (textSize != 0.0f) {
            mtv.setTextSize(textSize);
        }
        if (rgb != null){
            mtv.setColorR(rgb[0]);
            mtv.setColorG(rgb[1]);
            mtv.setColorB(rgb[2]);
            mtv.setTextColor(ColorUtil.getColorByRGB(rgb));
        }
        mtv.setTypefaceName(typefaceName);
        mtv.setTypeface(typeface);
        parentView.addView(mtv);
        return mtv;
    }






    //添加表情
    private void addStickerView() {
        final StickerView stickerView = new StickerView(this);
        stickerView.setImageResource(R.mipmap.ic_cat);
        // maidou add
        // stickerView.setBitmapReloadMatrix(reloadMatrix);
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mViews.remove(stickerView);
                fl_main_content.removeView(stickerView);
            }

            @Override
            public void onEdit(StickerView stickerView) {
                mCurrentView.setInEdit(false);
                mCurrentView = stickerView;
                mCurrentView.setInEdit(true);
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = mViews.indexOf(stickerView);
                if (position == mViews.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mViews.remove(position);
                mViews.add(mViews.size(), stickerTemp);
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        fl_main_content.addView(stickerView, lp);
        mViews.add(stickerView);
        setCurrentEdit(stickerView);
        mStickerViewLists.add(stickerView);
    }

    /**
     * 设置当前处于编辑模式的贴纸
     */
    private void setCurrentEdit(StickerView stickerView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        mCurrentView = stickerView;
        stickerView.setInEdit(true);
    }

    // --- listener 底部的 RadioGroup, 切换监听
    private class RadioGroupOnCheckChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_word:
                    // Show Text Editor
                    addMovableTextView();
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

    /**
     * @details 计算得到原图图片相对于屏幕的缩放比例和最后缩放后的留白区域
     */
    private void calcScaleAndLeaveSize() {
        if (copyBitmap == null) {return;}
        scaleAndLeaveSize = new float[3];
        float svWidth = iv_main_image.getWidth() * 1.0f;
        float svHeight = iv_main_image.getHeight() * 1.0f;
        float copyBitWidth = copyBitmap.getWidth() * 1.0f;
        float copyBitHeight = copyBitmap.getHeight() * 1.0f;

        float scaleX = copyBitWidth / svWidth;
        float scaleY = copyBitHeight / svHeight;
        float scale = scaleX > scaleY ? scaleX:scaleY;
        float leaveW = 0.0f, leaveH = 0.0f;     // 留白区域
        if (scaleX > scaleY) {
            leaveH = (svHeight -  copyBitHeight / scale) / 2;
        } else {
            leaveW = (svWidth - copyBitWidth / scale) / 2;
        }
        System.out.println(":imageView save params:" + svHeight);
        System.out.println(scale + "scale------:" + scaleX + ":" + scaleY);
        scaleAndLeaveSize[0] = scale;       // 表示图片与屏幕的缩放比
        scaleAndLeaveSize[1] = leaveW;      // 表示图片的X轴留白区域
        scaleAndLeaveSize[2] = leaveH;      // 表示图片的Y轴留白区域
    }

    /**
     * @details 重新载入多个贴纸
     */
    private void reloadStickerMore() {
        ArrayList<MatrixInfo> matrixInfoLists = FileUtils.readFileToMatrixInfoLists();
        if (matrixInfoLists == null)return;
        for (MatrixInfo matrixInfo: matrixInfoLists) {
            float[] floats = matrixInfo.floatArr;
            final StickerView stickerView = new StickerView(this);
            stickerView.setImageResource(R.mipmap.ic_cat);
            stickerView.reloadBitmapAfterOnDraw(floats);
            stickerView.setOperationListener(new StickerView.OperationListener() {
                @Override
                public void onDeleteClick() {
                    mViews.remove(stickerView);
                    fl_main_content.removeView(stickerView);
                }

                @Override
                public void onEdit(StickerView stickerView) {
                    mCurrentView.setInEdit(false);
                    mCurrentView = stickerView;
                    mCurrentView.setInEdit(true);
                }
                @Override
                public void onTop(StickerView stickerView) {
                    int position = mViews.indexOf(stickerView);
                    if (position == mViews.size() - 1) {
                        return;
                    }
                    StickerView stickerTemp = (StickerView) mViews.remove(position);
                    mViews.add(mViews.size(), stickerTemp);
                }
            });
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            fl_main_content.addView(stickerView, lp);
            mViews.add(stickerView);
            setCurrentEdit(stickerView);
        }
    }

    /**
     * @details 在打开的时候载入单个贴纸
     */
    private void reloadSticker() {
        MatrixInfo matrixInfo = FileUtils.readFileToMatrixInfo();
        if (matrixInfo == null)return;
        float[] floats = matrixInfo.floatArr;
        final StickerView stickerView = new StickerView(this);
        stickerView.setImageResource(R.mipmap.ic_cat);
        // maidou add
        stickerView.reloadBitmapAfterOnDraw(floats);
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mViews.remove(stickerView);
                fl_main_content.removeView(stickerView);
            }

            @Override
            public void onEdit(StickerView stickerView) {
//                if (mCurrentEditTextView != null) {
//                    mCurrentEditTextView.setInEdit(false);
//                }
                mCurrentView.setInEdit(false);
                mCurrentView = stickerView;
                mCurrentView.setInEdit(true);
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = mViews.indexOf(stickerView);
                if (position == mViews.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mViews.remove(position);
                mViews.add(mViews.size(), stickerTemp);
            }
        });
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        fl_main_content.addView(stickerView, lp);
        mViews.add(stickerView);
        setCurrentEdit(stickerView);
    }

    /**
     * @details 保存贴纸
     * @param canvas
     */
    private void saveStickerViews(Canvas canvas) {
        if (mStickerViewLists == null || mStickerViewLists.size() <= 0) {
            return;
        }
        float leaveH = 0f, leaveW = 0f, scale = 0f;
        scale  = scaleAndLeaveSize[0];   // 原图与ImageView的缩放比例
        leaveW = scaleAndLeaveSize[1];   // 图片自动缩放时造成的留白区域
        leaveH = scaleAndLeaveSize[2];
        canvas.scale(scale, scale);
        canvas.translate(-leaveW, -leaveH);
        ArrayList<MatrixInfo> matrixInfoArrayList = new ArrayList<>();
        MatrixInfo matrixInfo;
        for(StickerView sv:mStickerViewLists) {
            canvas.drawBitmap(sv.getBitmap(), sv.saveMatrix(), null);
            matrixInfo = new MatrixInfo();
            matrixInfo.floatArr = sv.saveMatrixFloatArray();
            matrixInfoArrayList.add(matrixInfo);
        }
        String imagePath = FileUtils.saveBitmapToLocal(copyBitmap, mContext);
        // add 插入保存图片 matrix
        // FileUtils.saveSerializableMatrix(matrixInfo);   // 保存单个贴纸的矩阵信息
        FileUtils.saveSerializableMatrixLists(matrixInfoArrayList);
        System.out.println("保存成功~~~~~~~" + imagePath);
        ToastUtil.makeText(mContext, "保存成功~~~~~~~" + imagePath);
    }

    private void initDataToSeekBar() {
        progressItemList = new ArrayList<>();
        for (int i = 0; i < colorSpan.length; i++) {
            mProgressItem = new ProgressItem();
            mProgressItem.progressItemPercentage = ((colorSpan[i] / totalSpan) * 100);
            mProgressItem.color = colorValues[i];
            progressItemList.add(mProgressItem);
        }
        csbFontColor.initData(progressItemList);
        csbFontColor.invalidate();

        csbFontColor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println("progress:" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


}
