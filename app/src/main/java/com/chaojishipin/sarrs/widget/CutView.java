package com.chaojishipin.sarrs.widget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.Utils;

public class CutView extends View {
    private Bitmap originBitmap;
    private Bitmap bitmap;
    private boolean isSave = false;
    private float scale = 1;
    private float translateX = 0, translateY = 0;
    private float left = 0, top = 0;
    private PointF prev = new PointF();
    private PointF mid = new PointF();
    private CutMode mode;
    private float circleOffet = 0;
    private float yCircleOffet = 0;
    private float circleCenterX, circleCenterY, circleRadius;
    private Rect cutRect = new Rect();
    private Handler handler;

    enum CutMode {
        NONE, DRAG, ZOOM
    }

    public CutView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CutView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.originBitmap = bitmap;

        if (bitmap.getWidth() < getScreenWidth(getContext())) {
            circleOffet = (getScreenWidth(getContext()) - bitmap.getWidth()) / 2;
        }
        if (bitmap.getHeight() < getScreenHeight(getContext())) {
            yCircleOffet = (getScreenHeight(getContext()) - bitmap.getHeight()) / 2;
        }
        circleCenterX = getScreenWidth(getContext()) / 2;
        circleCenterY = getScreenHeight(getContext()) / 2 - 20;
        circleRadius = getScreenWidth(getContext()) / 2 - 10;

        if (bitmap.getWidth() < getScreenWidth(getContext())) {
            cutRect.left = (int) Math.floor(circleOffet
                    + (bitmap.getWidth() / 2 - circleRadius));
        } else
            cutRect.left = (int) Math.floor(circleOffet
                    + (getScreenWidth(getContext()) / 2 - circleRadius));
        if (bitmap.getHeight() < getScreenHeight(getContext())) {
            cutRect.top = (int) Math.floor(bitmap.getHeight() / 2
                    - circleRadius);
        } else
            cutRect.top = (int) Math.floor(getScreenHeight(getContext()) / 2
                    - circleRadius);
        cutRect.right = (int) Math.floor(cutRect.left + circleRadius * 2);
        cutRect.bottom = (int) Math.floor(cutRect.top + circleRadius * 2);

        left = circleOffet;
        top = yCircleOffet;
//        if (bitmap.getWidth() < getScreenWidth(getContext())) {
//            left = (getScreenWidth(getContext()) - bitmap.getWidth()) / 2;
//            circleOffet = left;
//            circleCenterX = bitmap.getWidth() / 2 + circleOffet;
//            circleCenterY = bitmap.getHeight() / 2;
//            circleRadius = getScreenWidth(getContext()) / 2 - 8;
//
//            cutRect.left = (int) Math.floor(circleOffet
//                    + (bitmap.getWidth() / 2 - circleRadius));
//            cutRect.top = (int) Math.floor(bitmap.getHeight() / 2
//                    - circleRadius);
//            cutRect.right = (int) Math.floor(cutRect.left + circleRadius * 2);
//            cutRect.bottom = (int) Math.floor(cutRect.top + circleRadius * 2);
//        } else {
//            circleCenterX = getScreenWidth(getContext()) / 2 + circleOffet;
//            circleCenterY = getScreenHeight(getContext()) / 2;
//            circleRadius = getScreenWidth(getContext()) / 2 - 8;
//
//            cutRect.left = (int) Math.floor(circleOffet
//                    + (getScreenWidth(getContext()) / 2 - circleRadius));
//            cutRect.top = (int) Math.floor(getScreenHeight(getContext()) / 2
//                    - circleRadius);
//            cutRect.right = (int) Math.floor(cutRect.left + circleRadius * 2);
//            cutRect.bottom = (int) Math.floor(cutRect.top + circleRadius * 2);
//        }
        Log.d("cutView", "circleCenterX is " + circleCenterX + " circleCenterY is " + circleCenterY + " cutRect.left is " + cutRect.left + " cutRect.top is " + cutRect.top + " cutRect.right is " + cutRect.right);
    }

    private void setScale(float scale) {
        this.scale = scale;
    }

    private void setTranslateX(float translateX) {
        this.translateX = translateX;
    }

    private void setTranslateY(float translateY) {
        this.translateY = translateY;
    }

    float dist;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 主点按下
            case MotionEvent.ACTION_DOWN:
                mode = CutMode.DRAG;
                prev.set(event.getX(), event.getY());
                break;
            // 副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                dist = spacing(event);
                if (dist > 10f) {
                    midPoint(mid, event);
                    mode = CutMode.ZOOM;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = CutMode.NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == CutMode.DRAG) {
                    setTranslateX(event.getX() - prev.x);
                    setTranslateY(event.getY() - prev.y);
                } else if (mode == CutMode.ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        float tScale = (float) Math.sqrt(newDist / dist);
                        if (scale > 10)
                            scale = 10f;
                        if (scale < 0.1)
                            scale = 0.1f;
                        setScale(tScale);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                if (mode == CutMode.DRAG) {
                    left += (event.getX() - prev.x) / scale;
                    top += (event.getY() - prev.y) / scale;

                    translateX = 0;
                    translateY = 0;
                }
                // 边界控制
                // if (bitmap.getWidth() < Utils.getScreenWidth(getContext())) {
                // if (left > (circleOffet + (bitmap.getWidth() - bitmap.getWidth()
                // / 3 * 2) / 2))
                // left = circleOffet
                // + (bitmap.getWidth() - bitmap.getWidth() / 3 * 2) / 2;
                // if (left < (circleOffet - (bitmap.getWidth() - bitmap.getWidth()
                // / 3 * 2) / 2))
                // left = circleOffet
                // - (bitmap.getWidth() - bitmap.getWidth() / 3 * 2) / 2;
                // }
                // if (bitmap.getHeight() < Utils.getScreenHeight(getContext())) {
                // if (top > (bitmap.getHeight() - bitmap.getWidth() / 3 * 2) / 2)
                // top = (bitmap.getHeight() - bitmap.getWidth() / 3 * 2) / 2;
                //
                // if (top < -(bitmap.getHeight() - bitmap.getWidth() / 3 * 2) / 2)
                // top = -(bitmap.getHeight() - bitmap.getWidth() / 3 * 2) / 2;
                // }

                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (originBitmap != null) {
            Matrix matrix = new Matrix();
            canvas.save();
            matrix.setScale(scale, scale, mid.x, mid.y);
            try {
//				Bitmap recycleBitmap = bitmap;
                if (bitmap != null && bitmap != originBitmap) {
                    bitmap.recycle();
                    bitmap = null;
                }
                bitmap = Bitmap.createBitmap(originBitmap, 0, 0,
                        originBitmap.getWidth(), originBitmap.getHeight(),
                        matrix, true);

//				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//				bitmapOptions.inSampleSize = 2;
//				bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
//				bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(picuri), null, bitmapOptions);

                matrix.postTranslate(translateX, translateY);
                canvas.setMatrix(matrix);
                Log.d("cutView", "left is " + left + " top is " + top);

                canvas.drawBitmap(bitmap, left, top, new Paint());
                invalidate();

                canvas.restore();

                canvas.save();
                Path path = new Path();
                path.addCircle(circleCenterX, circleCenterY, circleRadius,
                        Direction.CCW);
                canvas.clipPath(path, Region.Op.DIFFERENCE);
                canvas.drawColor(getResources().getColor(R.color.color_33000000));

				/*DashPathEffect dashStyle = new DashPathEffect(new float[] { 10,
                        5, 5, 5 }, 2);*/
                Paint mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setStyle(Style.STROKE);
                mPaint.setColor(getResources().getColor(R.color.color_ffffff));
                mPaint.setStrokeWidth(Utils.dip2px(1));
                /*
                mPaint.setPathEffect(dashStyle);*/
                canvas.drawPath(path, mPaint);

                if (isSave) {
                    try {
                        int x = (int) Math.floor(cutRect.left * scale
                                - Math.ceil(left)) <= 0 ? 0 : (int) Math
                                .floor(cutRect.left * scale
                                        - Math.ceil(left));
                        int y = (int) Math.floor(cutRect.top * scale
                                - Math.ceil(top)) <= 0 ? 0 : (int) Math
                                .floor(cutRect.top * scale
                                        - Math.ceil(top));
                        int width = (int) (cutRect.width() / scale) + 1;
                        int height = (int) (cutRect.height() / scale + 1);
                        if (width + x > bitmap.getWidth())
                            width = bitmap.getWidth() - x;
                        if (height + y > bitmap.getHeight())
                            height = bitmap.getHeight() - y;
                        if (width < 0)
                            width = bitmap.getWidth();
                        if (height < 0)
                            height = bitmap.getHeight();
                        Bitmap newbm = Bitmap.createBitmap(
                                bitmap,
                                x,
                                y,
                                width,
                                height);// top*scale

                        String uri = saveTu(toRoundBitmap(newbm));
//						if (newbm != null && !newbm.isRecycled())
//						{
//							newbm.recycle();
//							newbm
//						}
                        if (newbm != null && !newbm.isRecycled()) {
                            if (newbm == bitmap) {
                                bitmap = null;
                            }
                            if (newbm == originBitmap) {
                                originBitmap = null;
                            }
                            newbm.recycle();
                            newbm = null;
                        }
                        if (originBitmap != null && !originBitmap.isRecycled()) {
                            if (originBitmap == bitmap)
                                bitmap = null;
                            originBitmap.recycle();
                            originBitmap = null;
                        }

                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                        if (uri != null && !uri.equals("")) {
//							newbm.recycle();
                            setSave(false);
//                            Toast.makeText(getContext(), "picture_saved!",
//                                    Toast.LENGTH_SHORT).show();
                            handler.obtainMessage(0, uri).sendToTarget();
                        }

                    } catch (Exception e) {
                        setSave(false);
                        System.out.println(e);
                        Toast.makeText(getContext(), "picture_save_failed!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                canvas.restore();
            } catch (Exception e) {

            }

        }
    }

    public void setSave(boolean isSave) {
        this.isSave = isSave;
    }

    /**
     * 两点的距�?
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private String saveTu(Bitmap bm) {
        boolean hasSD = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (!hasSD) {
            Toast.makeText(getContext(), "sdcard_not_exist!",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        String filePath = Environment.getExternalStorageDirectory().getPath()
                + File.separator + "sarrs_pic";
        File dirFile = new File(filePath);
        if (!dirFile.exists())
            dirFile.mkdirs();
        String path = filePath + File.separator + System.currentTimeMillis() + ".jpg";
        File file = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {

            }
        }
        return path;
    }

    /**
     * 转换图片成圆�?
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

//		BitmapFactory.Options opts = new BitmapFactory.Options();
//		opts.inTempStorage = new byte[100 * 1024];
//		opts.inPreferredConfig = Bitmap.Config.RGB_565;
//		opts.inPurgeable = true;
//		opts.inSampleSize = 2;
//		opts.inInputShareable = true;

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 两点的中�?
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    private int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public float getCircleRadis() {
        return getScreenWidth(getContext()) / 2 - 10;
    }
}
