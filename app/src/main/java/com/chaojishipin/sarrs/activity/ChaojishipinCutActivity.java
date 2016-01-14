package com.chaojishipin.sarrs.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.thirdparty.UserLoginState;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.NetWorkUtils;
import com.chaojishipin.sarrs.utils.ToastUtil;
import com.chaojishipin.sarrs.widget.CutView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChaojishipinCutActivity extends ChaoJiShiPinBaseActivity implements OnClickListener {
    private Button cutBtn, cancelBtn;
    private int requestCode = 100;
    private Bitmap bitmap = null;
    private CutView cutView;
    private int mode;
    public static String IMAGE_URL = "imageurl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifyactivity_cutactivity);
        setTitleBarVisibile(false);
        init();

        mode = getIntent().getIntExtra("mode", 0);
        if (mode == 0) {
            // 获取相机拍照原图
            getOriginalMap(getIntent());
        } else {
            String picPath = getIntent().getStringExtra("uri");
            if (picPath != null) {
                bitmap = ImageLoader.getInstance().loadImageSync(picPath);
                if (bitmap != null) {
                    cutView.setBitmap(bitmap);
                    cutView.setHandler(handler);
                }
            }

        }

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String newImg = "file://" + msg.obj.toString();
//                    UserLoginState.getInstance().getUserInfo().setAvatar(newImg); // 保存裁剪后的新图片，用于请求头像修改
                    LogUtil.e("CutActivity", newImg);
                    Intent intent = new Intent();
                    intent.putExtra(IMAGE_URL, newImg);
                    intent.putExtra("mode", 2);
                    intent.setAction(ChaojishipinModifyUserInfoDetailActivity.imagePickFinish);
                    sendBroadcast(intent); // 发送广播，请求头像修改
                    finish();
                    break;
            }
        }

    };

    @Override
    public void handleNetWork(String netName, int netType, boolean isHasNetWork) {
        
    }

    void getOriginalMap(Intent data) {

        Uri picuri = data.getData();

        if (picuri != null) {
            try {
                // 读取uri所在的图片
                float radis = cutView.getCircleRadis();
                String path = getAbsoluteImagePath(picuri);
                bitmap = loadResizedBitmap(path, (int) radis * 2, (int) radis * 2, false);
//                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                bitmapOptions.inSampleSize = 1;
//                bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(picuri), null , bitmapOptions);
            } catch (Exception e) {
                Log.e("[Android]", e.getMessage());
                Log.e("[Android]", "目录为：" + picuri);
                e.printStackTrace();
            }
            if (bitmap != null) {
                cutView.setBitmap(bitmap);
                cutView.setHandler(handler);
            }
        }
//        String[] pojo = {MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery(data.getData(), pojo, null, null, null);
//        if (cursor != null) {
//            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
//            cursor.moveToFirst();
//            picPath = cursor.getString(columnIndex);
//            cursor.close();
//        }


//        if (picPath != null) {
//            bitmap = ImageLoader.getInstance().loadImageSync("file://" + picPath);
//            System.out.println("picPath:" + picPath);
//            if (bitmap != null) {
//                cutView.setBitmap(bitmap);
//                cutView.setHandler(handler);
//            }
//        }

    }

    /*
     *
     *
     */
    public Bitmap rotate(Bitmap map) {

        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(90);
        //Bitmap tmpBitmap=BitmapFactory.decodeByteArray(Utils.picData1, 0, Utils.picData1.length);
        System.out.println("map:" + map);
        map = Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(), matrix, true);
        return map;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    void readFromIntent(Intent data) {
        String sdState = Environment.getExternalStorageState();
        if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
            Log.e("", " sdcard no mounted");
            return;
        }
        new DateFormat();
        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        Bitmap bitmap = null;
        Bundle bundle = data.getExtras();
        bitmap = (Bitmap) bundle.get("data");
        FileOutputStream fout = null;
        File file = new File("/sdcard/pintu/");
        file.mkdirs();
        String filename = file.getPath() + name;
        try {
            fout = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fout.flush();
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (bitmap != null) {
            cutView.setBitmap(bitmap);
            cutView.setHandler(handler);
        }
    }


    void dosave(Intent data) {
        Bitmap photo = null;

        Uri uri = data.getData();
        if (uri != null) {
            photo = BitmapFactory.decodeFile(uri.getPath());

            if (photo != null) {
                cutView.setBitmap(photo);
                cutView.setHandler(handler);
            }
        }
        if (photo == null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                photo = (Bitmap) bundle.get("data");
            } else {
                Toast.makeText(this, "拍照失败", Toast.LENGTH_LONG).show();
                return;
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            // 获取 SD 卡根目录
            String saveDir = "data/data" + "/sarrs_pic/";
            // 新建目录
            File dir = new File(saveDir);
            if (!dir.exists()) dir.mkdir();
            // 生成文件名
            SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
            String filename = "" + (t.format(new Date())) + ".jpg";
            // 新建文件
            File file = new File(saveDir, filename);
            // 打开文件输出流
            fileOutputStream = new FileOutputStream(file);
            // 生成图片文件
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            // 相片的完整路径
            String picPath = file.getPath();
           /* ImageView imageView = (ImageView) findViewById(R.id.showPhoto);
            imageView.setImageBitmap(photo);*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }


    /**
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }


    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewHeight == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            //为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    private Bitmap loadResizedBitmap(String filename, int width, int height, boolean exact) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        if (options.outHeight > 0 && options.outWidth > 0) {
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;
            while (options.outWidth / options.inSampleSize > width
                    && options.outHeight / options.inSampleSize > height) {
                options.inSampleSize++;
            }
            options.inSampleSize--;

            bitmap = BitmapFactory.decodeFile(filename, options);
            if (bitmap != null && exact) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            }
        }
        return bitmap;
    }

    void createBitmap(String fileName) {
        //将保存在本地的图片取出并缩小后显示在界面上
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        bitmap = decodeThumbBitmapForFile(fileName, 400, 900);
        //Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);  
        //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常  
        if (bitmap != null) {
            cutView.setBitmap(bitmap);
            cutView.setHandler(handler);
        }


    }


    void readFromAssert() {
        try {
            InputStream in = getResources().getAssets().open("wangfei.jpg");

            if (in != null)
                bitmap = BitmapFactory.decodeStream(in);
            if (bitmap != null) {
                cutView.setBitmap(bitmap);
                cutView.setHandler(handler);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void readBitmap(String picName) {
        FileInputStream is = null;
        try {
            is = openFileInput(picName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        if (bitmap != null) {
            cutView.setBitmap(bitmap);
            cutView.setHandler(handler);
        }

    }

    void readBitmap(Uri imgUri) {
        Bitmap bitmap = null;
        try {
            //图片解析成Bitmap对象
            bitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(imgUri));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            cutView.setBitmap(bitmap);
            cutView.setHandler(handler);
        }

    }


    private void init() {
        // TODO Auto-generated method stub
        cutView = (CutView) findViewById(R.id.cut_activity_cutview);
        cutBtn = (Button) findViewById(R.id.cut_activity_save);
        cutBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.cut_activity_cancel);
        cancelBtn.setOnClickListener(this);
        //readFromIntent(getIntent());

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
        System.gc();
    }

    @Override
    protected View setContentView() {
        return null;
    }

    @Override
    protected void handleInfo(Message msg) {

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.cut_activity_save:
                //无网络直接finish
                if (NetWorkUtils.isNetAvailable()) {
                    cutView.setSave(true);// 保存图片
//                  cutView.invalidate();
                    this.finish();
                } else
                    ToastUtil.showShortToast(ChaojishipinCutActivity.this, getResources().getString(R.string.neterror));
                break;
            case R.id.cut_activity_cancel:
                finish();
                break;
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    protected String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri,
                proj,                 // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null);                 // Order-by clause (ascending by name)

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
