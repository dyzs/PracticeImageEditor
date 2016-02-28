package com.dyzs.conciseimageeditor;

import android.app.Activity;
import android.app.Dialog;
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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;
import com.dyzs.conciseimageeditor.utils.BitmapUtils;
import com.dyzs.conciseimageeditor.view.MovableTextView2;
public class MainUIActivity extends Activity {
    private Context mContext;
    // topToolbar
    private ImageView bt_save;
    // mainContent
    private ImageView iv_main_image;
    private RecyclerView mRecyclerView_filter_list;
    // bottomToolbar
    private RadioGroup main_radio;

    // tempTest
    private MovableTextView2 movableTextView2;
    private Bitmap mainBitmap;
    private Bitmap copyBitmap;
    private Bitmap filterSampleIconBitmap;
    private float scaleXX;
    private float scaleYY;

    private FrameLayout fl_main_content;
    private RelativeLayout rl_work_panel;

    // 保存最后一次点击的滤镜的item
    private int lastClickPosition = 0;


    private static int mCurrImgId = R.mipmap.pic_bg_1920x1080x001;
    private PopupWindow mPppw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_ui);
        mContext = this;

        initView();
        // 加载图片
        loadBitmap();

        handleEvent();

        initTextEditPanel();

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
        movableTextView2 = new MovableTextView2(getApplicationContext());
        movableTextView2.setTextSize(getResources().getDimension(R.dimen.movable_text_view_default_text_size));
        movableTextView2.setText("请输入文字~");
        movableTextView2.setOnActionUpListener(new MovableTextView2.OnActionUpListener() {
            @Override
            public void getStartPosition(int startX, int startY) {
                System.out.println(startX + ":" + startY);

            }
        });
        movableTextView2.setOnCustomClickListener(new MovableTextView2.OnCustomClickListener() {
            @Override
            public void onCustomClick() {
                Toast.makeText(getApplicationContext(), "=.=.=.=", Toast.LENGTH_SHORT).show();
            }
        });
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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                copyBitmap.getWidth(), copyBitmap.getHeight());
//        rl_work_panel.setLayoutParams(layoutParams);
//        rl_work_panel.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        fl_main_content.addView(movableTextView2);
    }

    private void handleEvent() {
        bt_save.setOnClickListener(new SaveClickListener());
        main_radio.setOnCheckedChangeListener(new CurrentRadioGroupOnCheckChangeListener());
    }

    private void initTextEditPanel() {



    }

        // inner class start
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
            View view = LayoutInflater.from(context).inflate(R.layout.view_filter_item, null);
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
//                if (posi == 0) {
//                    iv_main_image.setImageBitmap(mainBitmap);
//                } else {
//                    Bitmap bitmap = PhotoProcessing.filterPhoto(
//                            BitmapUtils.loadImage(MainUIActivity.this),
//                            posi
//                    );
//                    iv_main_image.setImageBitmap(bitmap);
//                }
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

    private class SaveClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            movableTextView2.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int textViewL = movableTextView2.getLeft();
            int textViewT = movableTextView2.getTop();
            int textViewR = movableTextView2.getRight();
            int textViewB = movableTextView2.getBottom();
            float textViewW = movableTextView2.getWidth() * 1.0f;
            float textViewH = movableTextView2.getHeight() * 1.0f;

            float imgW = iv_main_image.getWidth() * 1.0f;
            float imgH = iv_main_image.getHeight() * 1.0f;
            float bitW = copyBitmap.getWidth() * 1.0f;
            float bitH = copyBitmap.getHeight() * 1.0f;

            scaleXX = bitW / imgW;
            scaleYY = bitH / imgH;
            float scale = scaleXX > scaleYY ? scaleXX:scaleYY;
            System.out.println("scale------:" + scaleXX + ":" + scaleYY);
            int saveLeft, saveBottom;
            float leaveW = 0f, leaveH = 0f;
            if (scaleXX > scaleYY) {
                leaveH = (imgH - bitH / scale) / 2;
            } else {
                leaveW = (imgW - bitW / scale) / 2;
            }

            float textSize = movableTextView2.getTextSize() * scale;
            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(0);
            mPaint.setTextSize(textSize);
            mPaint.setColor(Color.BLUE);

            float textSpacing = (textViewH - textSize / scale);     // 获取画笔要绘画的文本的高度
            System.out.println("textSpacing:" + textSpacing);
            System.out.println("leave:" + leaveW + ":" + leaveH + "//spacing:" + textSpacing);
            saveLeft = (int) ((textViewL - leaveW) * scale);
            saveBottom = (int) (((textViewB - leaveH) - textSpacing) * scale);//textSpacing
            System.out.println("save:" + saveLeft + ":" + saveBottom);
//            设置滤镜
//            copyBitmap = PhotoProcessing.filterPhoto(copyBitmap, lastClickPosition);
            Canvas canvas = new Canvas(copyBitmap);
            canvas.drawText(
                    movableTextView2.getText().toString(),
                    saveLeft, saveBottom,
                    mPaint
            );
//            canvas.scale(scale, scale);
            iv_main_image.setImageBitmap(copyBitmap);
            // 保存到本地目录中
            String fileName = "save" + System.currentTimeMillis();
            String doneWithFileAbs = BitmapUtils.saveBitmap(copyBitmap, fileName);
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
}
