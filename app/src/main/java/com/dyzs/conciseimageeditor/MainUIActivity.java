package com.dyzs.conciseimageeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dyzs.conciseimageeditor.filter.PhotoProcessing;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainUIActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        mContext = this;
        // initImageLoader
        checkInitImageLoader();
        // 初始化 view
        initView();

        // 加载图片
        loadBitmap();
        handleEvent();


        loadRecycleViewData();

        // codeStyleAddMTV();
    }

    private void codeStyleAddMTV() {
        MovableTextView2 mtv = new MovableTextView2(getApplicationContext());
        mtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fl_main_content.addView(mtv);

    }


    private void loadBitmap() {
//        String filePath = "file:/" + Environment.getExternalStorageDirectory().getPath()
//                            + "/PictureTest/saveTemp.jpg";
//        System.out.println("=======" + filePath);
//        loadImage(filePath);


        mainBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_bg_ch_style);
        copyBitmap = mainBitmap.copy(Bitmap.Config.RGB_565, true);
        iv_main_image.setImageBitmap(copyBitmap);
//        loadImage();
    }

    public void loadImage(){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.pic_bg_ch_style, opts);
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight();
        int scaleX = imgWidth / screenWidth;
        int scaleY = imgHeight / screenHeight;
        int zoom = 1; //? 1 还是 0
        if(scaleX > zoom && scaleY > zoom){
            zoom = (scaleX>scaleY)?scaleX:scaleY;
        }
        System.out.println("得到的缩放比例为："+zoom);
        opts.inSampleSize = zoom;
        opts.inJustDecodeBounds = false;
        Bitmap copyImg = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_bg_ch_style, opts);
        iv_main_image.setImageBitmap(copyImg);
    }


    private void loadRecycleViewData() {

    }

    private void initView() {
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
        imageWidth = iv_main_image.getWidth();
        imageHeight = iv_main_image.getHeight();

        // bottomToolbar
        main_radio = (RadioGroup) findViewById(R.id.main_radio);

        movableTextView2 = (MovableTextView2) findViewById(R.id.mtmtmtmt);

        movableTextView2.setOnActionUpListener(new MovableTextView2.OnActionUpListener() {
            @Override
            public void getStartPosition(int startX, int startY) {
                System.out.println(startX + ":" + startY);
            }
        });

//        movableTextView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "我被点击了", Toast.LENGTH_SHORT).show();
//            }
//        });

        movableTextView2.setOnCustomClickListener(new MovableTextView2.OnCustomClickListener() {
            @Override
            public void onCustomClick() {
                Toast.makeText(getApplicationContext(), "=.=.=.=", Toast.LENGTH_SHORT).show();
            }
        });


        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
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
            if (position == 0) {
                String filePath = "file:/" + Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/PictureTest/saveTemp.jpg";
                System.out.println("position:" + position + filePath);
                h.icon.setImageBitmap(BitmapFactory.decodeFile(filePath));
            }
            else {
//                System.out.println("position:" + position);
//                h.icon.setImageBitmap(PhotoProcessing.filterPhoto(copyBitmap, position));
                 h.icon.setImageBitmap(mainBitmap);
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
            // 初始化画笔
            Paint textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.MAGENTA);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextAlign(Paint.Align.CENTER);


            movableTextView2.setDrawingCacheEnabled(true);
            movableTextView2.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            movableTextView2.layout(0, 0, movableTextView2.getMeasuredWidth(), movableTextView2.getMeasuredHeight());

            movableTextView2.setBackgroundRes(false);
            Bitmap bitmap = movableTextView2.getDrawingCache();
            movableTextView2.setBackgroundRes(true);

            Canvas canvas = new Canvas(copyBitmap);

            canvas.drawBitmap(bitmap, 100, 200, textPaint);
            iv_main_image.setImageBitmap(copyBitmap);

            // 保存到本地目录中
            String fileName = "save" + System.currentTimeMillis();
            String doneWithFileAbs = BitmapUtils.saveBitmap(copyBitmap, fileName);
            System.out.println("===>" + doneWithFileAbs);
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
}
