package com.dyzs.conciseimageeditor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;
import com.dyzs.conciseimageeditor.utils.BitmapUtils;
import com.dyzs.conciseimageeditor.view.MovableTextView2;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MainUIActivity extends Activity {
    private static final String TAG = MainUIActivity.class.getSimpleName();
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

    private FrameLayout fl_main_content;
//    private RelativeLayout rl_work_panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_ui);
        mContext = this;
//        AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
//        View view = View.inflate(getApplicationContext(), R.layout.fragment_edit_image_text_bak, null);
//        dialog.setView(view);
//        dialog.show();
//        EditText et = (EditText) view.findViewById(R.id.et_input_text);
//        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                return false;
//            }
//        });

        // initImageLoader
        checkInitImageLoader();
        // 初始化 view
        initView();

        // 加载图片
        loadBitmap();
        handleEvent();


        loadRecycleViewData();
    }


    private float scaleXX;
    private float scaleYY;
    private void loadBitmap() {
//        String filePath = "file:/" + Environment.getExternalStorageDirectory().getPath()
//                            + "/PictureTest/saveTemp.jpg";
//        System.out.println("=======" + filePath);
//        loadImage(filePath);
//        mainBitmap = loadImage();
        mainBitmap = loadImage();

        copyBitmap = mainBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        iv_main_image.setImageBitmap(copyBitmap);
        System.out.println("mainBitmap------:" + mainBitmap.getWidth() + ":" + mainBitmap.getHeight());
        iv_main_image.setImageBitmap(copyBitmap);




        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                copyBitmap.getWidth(), copyBitmap.getHeight());
//        rl_work_panel.setLayoutParams(layoutParams);
//        System.out.println("masdfasdf-----:" + rl_work_panel.getWidth() + ":" + rl_work_panel.getHeight());
        fl_main_content.addView(movableTextView2);
    }
    public Bitmap loadImage(){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.pic_bg_design, opts);
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        System.out.println(opts.outWidth + "::::" + opts.outHeight);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight();
        System.out.println(screenWidth + "::::" + screenHeight);
        int scaleX = imgWidth / screenWidth;
        int scaleY = imgHeight / screenHeight;
        int scale = 1; //? 1 还是 0
        if(scaleX > scale && scaleY > scale){
            scale = (scaleX>scaleY)?scaleX:scaleY;
        }
        System.out.println("得到的缩放比例为："+scale);
        opts.inSampleSize = scale;
        opts.inJustDecodeBounds = false;
        Bitmap copyImg = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_bg_design, opts);
        return copyImg;
    }


    private void loadRecycleViewData() {

    }

    private void initView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = (int) ((float) metrics.widthPixels / 1.5);
        imageHeight = (int) ((float) metrics.heightPixels / 1.5);

        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
//        rl_work_panel = (RelativeLayout) findViewById(R.id.rl_work_panel);

//        System.out.println("--===-->" + rl_work_panel.getWidth());
//        rl_work_panel.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        );
//        System.out.println("--===-13434324->" + rl_work_panel.getWidth());

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
            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Holder h = (Holder) holder;
            String name = PhotoProcessing.FILTERS[position];
            h.text.setText(name);
            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap = null;
            }
            srcBitmap = BitmapUtils.getScaleBitmap(copyBitmap, 0.1f);
            if (position == 0) {
                String filePath = "file:/" + Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/PictureTest/saveTemp.jpg";
                System.out.println("position:" + position + filePath);
                h.icon.setImageBitmap(srcBitmap);
            }
            else {
                h.icon.setImageBitmap(PhotoProcessing.filterPhoto(srcBitmap, position));
            }
        }

        public class Holder extends RecyclerView.ViewHolder {
            public ImageView icon;
            public TextView text;
            public Holder(View itemView) {
                super(itemView);
                this.icon = (ImageView) itemView.findViewById(R.id.icon);
                this.text = (TextView) itemView.findViewById(R.id.text);
            }
        }


    }
    // inner class end
private Bitmap srcBitmap;

    private Bitmap getImageFromFileSystem(String fileName) {
        Bitmap image = null;
        File file = new File(fileName);
        try {
            InputStream is = new FileInputStream(file);
            image = BitmapFactory.decodeStream(is);
        } catch (Exception e) {

        }
        return image;
    }


    private void handleEvent() {
        bt_save.setOnClickListener(new SaveClickListener());

        main_radio.setOnCheckedChangeListener(new CurrentRadioGroupOnCheckChangeListener());


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
//            iv_main_image.measure(
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            iv_main_image.layout(0, 0, iv_main_image.getMeasuredWidth(),
//                    iv_main_image.getMeasuredHeight());
//            iv_main_image.buildDrawingCache();
//            Bitmap ivCache = iv_main_image.getDrawingCache();
//
            movableTextView2.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            movableTextView2.buildDrawingCache();
            Bitmap mtvCache = movableTextView2.getDrawingCache();

            int imgW = iv_main_image.getWidth();
            int imgH = iv_main_image.getHeight();
            System.out.println("imgW:" + imgW + ":" + imgH);
            scaleXX = copyBitmap.getWidth()* 1.0f / imgW*1.0f;
            scaleYY = copyBitmap.getHeight()*1.0f / imgH*1.0f;
            float scale = scaleXX > scaleYY ? scaleXX:scaleYY;
            System.out.println("scale------:" + scaleXX + ":" + scaleYY);
            int left, bottom;
            // float scaleMax = Math.max(scaleWidth, scaleHeight);
            if (scaleXX > scaleYY) {
                left = (int) (movableTextView2.getLeft() * scale);
                bottom = (int) (movableTextView2.getBottom() * scaleYY - ((imgH * 1.0f - copyBitmap.getHeight() * scaleYY) / 2));
            } else {
                left = (int) (movableTextView2.getLeft() * scaleXX - ((imgW * 1.0f - copyBitmap.getWidth() * scaleXX) / 2));
                bottom = (int) (movableTextView2.getBottom() * scale);
            }
//            left = (movableTextView2.getLeft());
//            bottom = (movableTextView2.getBottom());

            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(0);
            mPaint.setTextSize(movableTextView2.getTextSize());
            mPaint.setColor(Color.RED);

            Canvas canvas = new Canvas(copyBitmap);
            canvas.scale(scale, scale);
            canvas.drawBitmap(mtvCache, 100, 200, null);

            iv_main_image.setImageBitmap(copyBitmap);

            // 保存到本地目录中
            String fileName = "save" + System.currentTimeMillis();
            String doneWithFileAbs = BitmapUtils.saveBitmap(copyBitmap, fileName);
            movableTextView2.setTextColor(Color.WHITE);
        }
    }


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




























    // 加载 ImageLoader
    protected void checkInitImageLoader() {
        if (!ImageLoader.getInstance().isInited()) {
            initImageLoader();
        }//end if
    }

    /**
     * 初始化图片载入框架
     */
    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        System.out.println("cacheDir:" + cacheDir.toString());
        int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory());
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).memoryCacheExtraOptions(480, 800).defaultDisplayImageOptions(defaultOptions)
                .diskCacheExtraOptions(480, 800, null).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MAXMEMONRY / 5))
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .imageDecoder(new BaseImageDecoder(false)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).build();

        ImageLoader.getInstance().init(config);
    }


//    srcBitmap = Bitmap.createBitmap(activity.mainBitmap.copy(
//    Bitmap.Config.RGB_565, true));
//    return PhotoProcessing.filterPhoto(srcBitmap, type);
}
