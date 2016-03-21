//package com.dyzs.conciseimageeditor;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Rect;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.dyzs.conciseimageeditor.entity.MatrixInfo;
//import com.dyzs.conciseimageeditor.utils.BitmapUtils;
//import com.dyzs.conciseimageeditor.utils.FileUtils;
//import com.dyzs.conciseimageeditor.utils.ToastUtil;
//import com.dyzs.conciseimageeditor.view.MovableTextView2;
//import com.dyzs.conciseimageeditor.view.StickerView;
//import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;
//
//import java.util.ArrayList;
//
//public class MainUIActivityBak0318 extends Activity {
//    private Context mContext;
//    // topToolbar
//    private ImageView bt_save;
//    // mainContent
//    private ImageView iv_main_image;
//    private RecyclerView mRecyclerView_filter_list;
//    // bottomToolbar
//    private RadioGroup main_radio;
//    private RadioButton rb_word;
//    private RadioButton rb_sticker;
//
//    // tempTest
//    private ArrayList<MovableTextView2> mMtvLists;
//
//
//    private Bitmap mainBitmap;
//    private Bitmap copyBitmap;
//    private Bitmap filterSampleIconBitmap;
//
//    private FrameLayout fl_main_content;
//    private RelativeLayout rl_work_panel;
//
//
//    private static int mCurrImgId = R.mipmap.pic_bg_1920x1080x003;
//    public int keyboardHeight = 0;
//    private LinearLayout ll_edit_panel;
//    private View edit_panel_not_used;
//    private LinearLayout ll_edit_panel_head;
//    private LinearLayout ll_edit_panel_content;
//
//    private EditText et_input_text;
//
//    private boolean writeClickCount = false;
//
//    //存储贴纸列表
//    private ArrayList<View> mViews;
//
//    //当前处于编辑状态的贴纸
//    private StickerView mCurrentView;
//
//    private ArrayList<StickerView> mStickerViewLists;
//    private float[] scaleAndLeaveSize;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 设置启动不弹出软键盘, 这样就可以监听到键盘的高度了, 得到键盘高度后再重新绘制
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        setContentView(R.layout.activity_main_ui);
//        mContext = this;
//        mMtvLists = new ArrayList<>();
//        mViews = new ArrayList<>();
//        mStickerViewLists = new ArrayList<>();
//
//
//
//        initView();
//        // 加载图片
//        loadBitmap();
//
//        handleListener();
//
//        // reloadSticker();
//        reloadStickerMore();
//
//        testKeyboard();
//    }
//    private int mVisibleHeight;
//    private void testKeyboard() {
//        et_input_text.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                et_input_text.getWindowVisibleDisplayFrame(r);
//                int visibleHeight = r.height();
//                if (mVisibleHeight == 0) {
//                    mVisibleHeight = visibleHeight;
//                    return;
//                }
//                if (mVisibleHeight == visibleHeight) {
//                    return;
//                }
//                mVisibleHeight = visibleHeight;
//                System.out.println("mVisibleHeight-----:" + mVisibleHeight);
//            }
//        });
//    }
//    private void calcScaleAndLeaveSize() {
//        if (copyBitmap == null) {return;}
//        scaleAndLeaveSize = new float[3];
//        float svWidth = iv_main_image.getWidth() * 1.0f;
//        float svHeight = iv_main_image.getHeight() * 1.0f;
//        float copyBitWidth = copyBitmap.getWidth() * 1.0f;
//        float copyBitHeight = copyBitmap.getHeight() * 1.0f;
//
//        float scaleX = copyBitWidth / svWidth;
//        float scaleY = copyBitHeight / svHeight;
//        float scale = scaleX > scaleY ? scaleX:scaleY;
//        float leaveW = 0.0f, leaveH = 0.0f;     // 留白区域
//        if (scaleX > scaleY) {
//            leaveH = (svHeight -  copyBitHeight/ scale) / 2;
//        } else {
//            leaveW = (svWidth - copyBitWidth / scale) / 2;
//        }
//        System.out.println(copyBitWidth + ":cp params:" + copyBitHeight);
//        System.out.println(svWidth + ":sticker params:" + svHeight);
//        System.out.println("scale------:" + scaleX + ":" + scaleY);
//        scaleAndLeaveSize[0] = scale;       // 表示图片与屏幕的缩放比
//        scaleAndLeaveSize[1] = leaveW;      // 表示图片的X轴留白区域
//        scaleAndLeaveSize[2] = leaveH;      // 表示图片的Y轴留白区域
//    }
//
//    private void initView() {
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        imageWidth = (int) ((float) metrics.widthPixels / 1.5);
//        imageHeight = (int) ((float) metrics.heightPixels / 1.5);
//        System.out.println("imageWidth------:" + imageWidth + ":" + imageHeight);
//
//        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
//        rl_work_panel = (RelativeLayout) findViewById(R.id.rl_work_panel);
//        // topBar
//        bt_save = (ImageView) findViewById(R.id.bt_save);
//        // mainContent
//        mRecyclerView_filter_list = (RecyclerView) findViewById(R.id.rv_filter_list);
//        mRecyclerView_filter_list.setHasFixedSize(true);
//        int spacingInPixels = 10; //getResources().getDimensionPixelSize(R.dimen.space);
//        mRecyclerView_filter_list.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mRecyclerView_filter_list.setLayoutManager(layoutManager);
//
//        mRecyclerView_filter_list.setAdapter(new FilterAdapter(this));
//
//        iv_main_image = (ImageView) findViewById(R.id.iv_main_image);
//        // bottomToolbar
//        main_radio = (RadioGroup) findViewById(R.id.main_radio);
//        rb_word = (RadioButton) findViewById(R.id.rb_word);
//        rb_sticker = (RadioButton) findViewById(R.id.rb_sticker);
//
//        ll_edit_panel = (LinearLayout) findViewById(R.id.ll_edit_panel);
//        edit_panel_not_used = findViewById(R.id.edit_panel_not_used);
//        ll_edit_panel_head = (LinearLayout) findViewById(R.id.ll_edit_panel_head);
//        ll_edit_panel_content = (LinearLayout) findViewById(R.id.ll_edit_panel_content);
//
//        et_input_text = (EditText) findViewById(R.id.et_input_text);
//
//
//        ll_edit_panel.setVisibility(View.INVISIBLE);
//    }
//    private void loadBitmap() {
////        String filePath = "file:/" + Environment.getExternalStorageDirectory().getPath()
////                            + "/PictureTest/saveTemp.jpg";
////        System.out.println("=======" + filePath);
////        loadImage(filePath);
////        mainBitmap = loadImage();
//        mainBitmap = BitmapUtils.loadImage(this, mCurrImgId, imageWidth, imageHeight);
//        copyBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        iv_main_image.setImageBitmap(copyBitmap);
//
//        // 计算图片加载后与屏幕的缩放比与留白区域
//        calcScaleAndLeaveSize();
//    }
//
//    private void handleListener() {
//        bt_save.setOnClickListener(new SaveClickListener());
//        main_radio.setOnCheckedChangeListener(new CurrentRadioGroupOnCheckChangeListener());
//        rb_word.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addMovableTextView();
//            }
//        });
//
//        rb_sticker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addStickerView();
//            }
//        });
//
//
//        // 监听获取键盘高度, 只能监听到打开与关闭
//        SoftKeyBoardListener.setListener(MainUIActivityBak0318.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
//            @Override
//            public void keyBoardShow(int height) {
//                if (height != 0) {
//                    keyboardHeight = height;
//                }
//            }
//            @Override
//            public void keyBoardHide(int height) {
//                if (height != 0) {
//                    keyboardHeight = height;
//                }
//            }
//        });
//
//        edit_panel_not_used.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ll_edit_panel.getVisibility() == View.VISIBLE) {
//                    ll_edit_panel.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//
//        et_input_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                System.out.println("hasFocus value:" + hasFocus);
//                if (hasFocus == false) {
//                    // 关闭软键盘
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(et_input_text.getWindowToken(), 0);
//                }
//            }
//        });
//    }
//
//    /**
//     * @details 重新载入多个贴纸
//     */
//    private void reloadStickerMore() {
//        ArrayList<MatrixInfo> matrixInfoLists = FileUtils.readFileToMatrixInfoLists();
//        if (matrixInfoLists == null)return;
//        for (MatrixInfo matrixInfo: matrixInfoLists) {
//            float[] floats = matrixInfo.floatArr;
//            final StickerView stickerView = new StickerView(this);
//            stickerView.setImageResource(R.mipmap.ic_cat);
//            stickerView.reloadBitmapAfterOnDraw(floats);
//            stickerView.setOperationListener(new StickerView.OperationListener() {
//                @Override
//                public void onDeleteClick() {
//                    mViews.remove(stickerView);
//                    fl_main_content.removeView(stickerView);
//                }
//
//                @Override
//                public void onEdit(StickerView stickerView) {
//                    mCurrentView.setInEdit(false);
//                    mCurrentView = stickerView;
//                    mCurrentView.setInEdit(true);
//                }
//                @Override
//                public void onTop(StickerView stickerView) {
//                    int position = mViews.indexOf(stickerView);
//                    if (position == mViews.size() - 1) {
//                        return;
//                    }
//                    StickerView stickerTemp = (StickerView) mViews.remove(position);
//                    mViews.add(mViews.size(), stickerTemp);
//                }
//            });
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//            fl_main_content.addView(stickerView, lp);
//            mViews.add(stickerView);
//            setCurrentEdit(stickerView);
//        }
//    }
//
//    /**
//     * @details 在打开的时候载入单个贴纸
//     */
//    private void reloadSticker() {
//        MatrixInfo matrixInfo = FileUtils.readFileToMatrixInfo();
//        if (matrixInfo == null)return;
//        float[] floats = matrixInfo.floatArr;
//        final StickerView stickerView = new StickerView(this);
//        stickerView.setImageResource(R.mipmap.ic_cat);
//        // maidou add
//        stickerView.reloadBitmapAfterOnDraw(floats);
//        stickerView.setOperationListener(new StickerView.OperationListener() {
//            @Override
//            public void onDeleteClick() {
//                mViews.remove(stickerView);
//                fl_main_content.removeView(stickerView);
//            }
//
//            @Override
//            public void onEdit(StickerView stickerView) {
////                if (mCurrentEditTextView != null) {
////                    mCurrentEditTextView.setInEdit(false);
////                }
//                mCurrentView.setInEdit(false);
//                mCurrentView = stickerView;
//                mCurrentView.setInEdit(true);
//            }
//
//            @Override
//            public void onTop(StickerView stickerView) {
//                int position = mViews.indexOf(stickerView);
//                if (position == mViews.size() - 1) {
//                    return;
//                }
//                StickerView stickerTemp = (StickerView) mViews.remove(position);
//                mViews.add(mViews.size(), stickerTemp);
//            }
//        });
//        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        fl_main_content.addView(stickerView, lp);
//        mViews.add(stickerView);
//        setCurrentEdit(stickerView);
//    }
//
//    private class SaveClickListener implements View.OnClickListener{
//        @Override
//        public void onClick(View v) {
//            Canvas canvas = new Canvas(copyBitmap);
//            saveStickerViews(canvas);
//
////            movableTextView2.measure(
////                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
////                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
////            int textViewL = movableTextView2.getLeft();
////            int textViewT = movableTextView2.getTop();
////            int textViewR = movableTextView2.getRight();
////            int textViewB = movableTextView2.getBottom();
////            float textViewW = movableTextView2.getWidth() * 1.0f;
////            float textViewH = movableTextView2.getHeight() * 1.0f;
////
////            float imgW = iv_main_image.getWidth() * 1.0f;
////            float imgH = iv_main_image.getHeight() * 1.0f;
////            float bitW = copyBitmap.getWidth() * 1.0f;
////            float bitH = copyBitmap.getHeight() * 1.0f;
////
////            scaleXX = bitW / imgW;
////            scaleYY = bitH / imgH;
////            float scale = scaleXX > scaleYY ? scaleXX:scaleYY;
////            System.out.println("scale------:" + scaleXX + ":" + scaleYY);
////            int saveLeft, saveBottom;
////            float leaveW = 0.0f, leaveH = 0.0f;
////            if (scaleXX > scaleYY) {
////                leaveH = (imgH - bitH / scale) / 2;
////            } else {
////                leaveW = (imgW - bitW / scale) / 2;
////            }
////
////            float textSize = movableTextView2.getTextSize() * scale;
////            Paint mPaint = new Paint();
////            mPaint.setAntiAlias(true);
////            mPaint.setStrokeWidth(0);
////            mPaint.setTextSize(textSize);
////            mPaint.setColor(Color.BLUE);
////
////            float textSpacing = (textViewH - textSize / scale);    // 获取画笔要绘画的文本的高度
////            System.out.println("leave:" + leaveW + ":" + leaveH + "//spacing:" + textSpacing);
////            saveLeft = (int) ((textViewL - leaveW) * scale);
////            saveBottom = (int) (((textViewB - leaveH) - textSpacing) * scale);
////            System.out.println("save:" + saveLeft + ":" + saveBottom);
//////            处理滤镜
//////            copyBitmap = PhotoProcessing.filterPhoto(copyBitmap, lastClickPosition);
////            Canvas canvas = new Canvas(copyBitmap);
////            canvas.drawText(
////                    movableTextView2.getText().toString(),
////                    saveLeft, saveBottom,
////                    mPaint
////            );
//////            canvas.scale(scale, scale);
////            iv_main_image.setImageBitmap(copyBitmap);
////            // 保存到本地目录中
////            String fileName = "save" + System.currentTimeMillis();
////            String doneWithFileAbs = BitmapUtils.saveBitmap(copyBitmap, fileName);
////            //
//        }
//    }
//
//    /**
//     * @details 保存贴纸啦
//     * @param canvas
//     */
//    private void saveStickerViews(Canvas canvas) {
//        if (mStickerViewLists == null || mStickerViewLists.size() <= 0) {
//            return;
//        }
//        // TODO
//        float leaveH = 0f, leaveW = 0f, scale = 0f;
//        ArrayList<MatrixInfo> matrixInfoArrayList = new ArrayList<>();
//        MatrixInfo matrixInfo;
//        for(StickerView sv:mStickerViewLists) {
//            scale  = scaleAndLeaveSize[0];
//            leaveW = scaleAndLeaveSize[1];   // 图片自动缩放时造成的留白区域
//            leaveH = scaleAndLeaveSize[2];
//            canvas.scale(scale, scale);
//            canvas.translate(-leaveW, -leaveH);
//            canvas.drawBitmap(sv.getBitmap(), sv.saveMatrix(), null);
//            matrixInfo = new MatrixInfo();
//            matrixInfo.floatArr = sv.saveMatrixFloatArray();
//            matrixInfoArrayList.add(matrixInfo);
//        }
//        String imagePath = FileUtils.saveBitmapToLocal(copyBitmap, mContext);
//        // add 插入保存图片 matrix
//        // FileUtils.saveSerializableMatrix(matrixInfo);   // 保存单个贴纸的矩阵信息
//        FileUtils.saveSerializableMatrixLists(matrixInfoArrayList);
//        System.out.println("保存成功~~~~~~~" + imagePath);
//        ToastUtil.makeText(mContext, "保存成功~~~~~~~" + imagePath);
//    }
//
//
//    private class MTVClickListener implements MovableTextView2.OnCustomClickListener {
//        @Override
//        public void onCustomClick() {
//            writeClickCount = true;
//            if (writeClickCount) {
//                ll_edit_panel.setVisibility(View.VISIBLE);
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//
//            } else {
//                ll_edit_panel.setVisibility(View.INVISIBLE);
//            }
//        }
//    }
//
//
//    // ====================task line
//    private LoadImageTask mLoadImageTask;
//    private int imageWidth, imageHeight;		// 展示图片控件 宽 高
//    public void loadImage(String filepath) {
//        if (mLoadImageTask != null) {
//            mLoadImageTask.cancel(true);
//        }
//        mLoadImageTask = new LoadImageTask();
//        mLoadImageTask.execute(filepath);
//    }
//    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            return BitmapUtils.loadImageByPath(params[0], imageWidth, imageHeight);
//        }
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            super.onPostExecute(result);
//            if (mainBitmap != null) {
//                mainBitmap.recycle();
//                mainBitmap = null;
//                System.gc();
//            }
//            mainBitmap = result;
//            iv_main_image.setImageBitmap(result);
//        }
//    }// end inner class
//
//    private int addMtvChildCount = 0;
//    private String text = "请输入文字~";
//    private void addMovableTextView() {
//        MovableTextView2 mtv = new MovableTextView2(getApplicationContext());
//        mtv.setText(text);
//        mtv.setOnCustomClickListener(new MTVClickListener());
//        fl_main_content.addView(mtv);
//        addMtvChildCount += 1;
//        mMtvLists.add(mtv);
//        System.out.println("mMtvLists count:" + mMtvLists.size());
//    }
//
//
//
//
//
//
//    //添加表情
//    private void addStickerView() {
//        final StickerView stickerView = new StickerView(this);
//        stickerView.setImageResource(R.mipmap.ic_cat);
//        // maidou add
//        // stickerView.setBitmapReloadMatrix(reloadMatrix);
//        stickerView.setOperationListener(new StickerView.OperationListener() {
//            @Override
//            public void onDeleteClick() {
//                mViews.remove(stickerView);
//                fl_main_content.removeView(stickerView);
//            }
//
//            @Override
//            public void onEdit(StickerView stickerView) {
//                mCurrentView.setInEdit(false);
//                mCurrentView = stickerView;
//                mCurrentView.setInEdit(true);
//            }
//
//            @Override
//            public void onTop(StickerView stickerView) {
//                int position = mViews.indexOf(stickerView);
//                if (position == mViews.size() - 1) {
//                    return;
//                }
//                StickerView stickerTemp = (StickerView) mViews.remove(position);
//                mViews.add(mViews.size(), stickerTemp);
//            }
//        });
//        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        fl_main_content.addView(stickerView, lp);
//        mViews.add(stickerView);
//        setCurrentEdit(stickerView);
//        mStickerViewLists.add(stickerView);
//    }
//
//    /**
//     * 设置当前处于编辑模式的贴纸
//     */
//    private void setCurrentEdit(StickerView stickerView) {
//        if (mCurrentView != null) {
//            mCurrentView.setInEdit(false);
//        }
//        mCurrentView = stickerView;
//        stickerView.setInEdit(true);
//    }
//
//    // --- listener 底部的 RadioGroup, 切换监听
//    private class CurrentRadioGroupOnCheckChangeListener implements RadioGroup.OnCheckedChangeListener {
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            switch (checkedId) {
//                case R.id.rb_word:
//                    // Show Text Editor
//                    break;
//                case R.id.rb_sticker:
//                    // Show Sticker ImageList RecycleView
//                    break;
//                case R.id.rb_filter:
//                    // Show Filter ImageList RecycleView
//                    break;
//                default:
//                    System.out.println("出现未知错误：" + checkedId);
//                    break;
//            }
//        }
//    }
//
//
//    // inner class start 滤镜的处理方法，暂时不用
//    private class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
//        private Context context;
//        public FilterAdapter(Context context) {
//            this.context = context;
//        }
//        @Override
//        public int getItemCount() {
//            return PhotoProcessing.FILTERS.length;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return 1;
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_view_filter, null);
//            FilterHolder holder = new FilterHolder(view);
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            FilterHolder h = (FilterHolder) holder;
//            String name = PhotoProcessing.FILTERS[position];
//            h.text.setText(name);
//            if (filterSampleIconBitmap != null && !filterSampleIconBitmap.isRecycled()) {
//                filterSampleIconBitmap = null;
//            }
//            filterSampleIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_icon_filter_sample);
//            if (position == 0) {
//                h.icon.setImageBitmap(filterSampleIconBitmap);
//            }
//            else {
//                h.icon.setImageBitmap(PhotoProcessing.filterPhoto(filterSampleIconBitmap, position));
//            }
//            h.icon.setOnClickListener(new FilterClickListener(position));
//        }
//        public class FilterClickListener implements View.OnClickListener{
//            private int clickPosition;
//            public FilterClickListener(int position) {
//                this.clickPosition = position;
//            }
//            @Override
//            public void onClick(View v) {
//                FilterTask filterTask = new FilterTask();
//                filterTask.execute(clickPosition + "");
//            }
//        }
//        public class FilterHolder extends RecyclerView.ViewHolder {
//            public ImageView icon;
//            public TextView text;
//            public FilterHolder(View itemView) {
//                super(itemView);
//                this.icon = (ImageView) itemView.findViewById(R.id.icon);
//                this.text = (TextView) itemView.findViewById(R.id.text);
//            }
//        }
//    }
//    // inner class end
//    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
//        private int space;
//        public SpaceItemDecoration(int space) {
//            this.space = space;
//        }
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            if (parent.getChildPosition(view) != 0)
//                outRect.top = space;
//        }
//    }
//
//    private final class FilterTask extends AsyncTask<String, Void, Bitmap> {
//        private ProgressDialog loadDialog;
//        public FilterTask() {
//            super();
//            loadDialog = new ProgressDialog(MainUIActivityBak0318.this, R.style.MyDialog);
//            loadDialog.setCancelable(false);
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            loadDialog.show();
//        }
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            int position = Integer.parseInt(params[0]);
//            return PhotoProcessing.filterPhoto(
//                    BitmapUtils.loadImage(MainUIActivityBak0318.this, mCurrImgId, fl_main_content),
//                    position
//            );
//        }
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            super.onPostExecute(result);
//            loadDialog.dismiss();
//            iv_main_image.setImageBitmap(result);
//            System.gc();
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            loadDialog.dismiss();
//        }
//    }// end inner class
//
//
//
//    // 显示dialog     useless
////    private void showQuickOption() {
////        final QuickOptionDialog dialog = new QuickOptionDialog(
////                MainUIActivity.this, movableTextView2);
////        dialog.setCancelable(true);
////        dialog.setCanceledOnTouchOutside(true);
////        dialog.show();
////    }
//}
