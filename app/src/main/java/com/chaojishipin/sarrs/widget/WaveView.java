package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @ClassName: WaveView
 * @author: daipei
 * @date: 2015年6月19日 14:58:06
 */
public class WaveView extends View {

	public int mLevel = 0;
	
	private float primaryWaveLineWidth = 6;//6
	private float secondaryWaveLineWidth = 3;//3
	private int numberOfWaves = 5;
	private float frequency = 1.5f;
	private float density = 1.0f;//1.7//5.0
	private float phase = 0.0f;
	private Float phaseShift = -0.15f;
	private Float idleAmplitude = 0.15f;
	private float amplitude = 0;
	
	private Paint mPaint = new Paint(); // 绘制样式物件

	private final Path mPath = new Path();

	public WaveView(Context context) {
		super(context);
	}

	public WaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		Log.d("dyf", "onDraw");
		int height = getHeight();
		int width = getWidth();
//		mPaint.setColor(Color.rgb(205, 243, 246));
		mPaint.setColor(Color.WHITE);
		/** 画笔的类型 **/
		mPaint.setStyle(Paint.Style.STROKE);
		/** 设置画笔变为圆滑状 **/
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setAntiAlias(true); // 反锯齿
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));

		grawView(canvas);

	}



	void grawView(Canvas canvas) {
		// We draw multiple sinus waves, with equal phases but altered
		// amplitudes, multiplied by a parable function.
		mPath.reset();
		for (int i = 0; i < numberOfWaves; i++) {
			float lineWidth = i == 0 ? primaryWaveLineWidth: secondaryWaveLineWidth;
			mPaint.setStrokeWidth(lineWidth);
			int halfHeight = getHeight()/2;
			int width = getWidth();
			int mid = width / 2;
			int maxAmplitude = halfHeight - 4; // 4 corresponds to twice the stroke width
			// Progress is a value between 1.0 and -0.5, determined by the
			// current wave idx, which is used to alter the wave's amplitude.
			float progress = 1.0f - (float) i / (float) numberOfWaves;
			float normedAmplitude = (1.5f * progress - 0.5f) * amplitude;

			float multiplier = Math.min(1.0f, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));
			// self.waveColor.colorWithAlphaComponent(multiplier * CGColorGetAlpha(self.waveColor.CGColor)).set()
			mPaint.setAlpha((int) (multiplier * mPaint.getAlpha()));

			for (float x = 0.0f; x < width + density; x += density) {
				// We use a parable to scale the sinus wave, that has its peak
				// in the middle of the view.
				float scaling = (float) (-Math
						.pow((1.0 / mid * (x - mid)), 2.0) + 1.0);

				float y = scaling * maxAmplitude * normedAmplitude * (float) Math.sin(2 * Math.PI * (x / width) * frequency + phase) + halfHeight;

//				canvas.drawPoint(x, y, mPaint);
				if(x == 0){
					mPath.moveTo(x,y);
				}else{
					mPath.lineTo(x,y);
				}

			}
//			updateWithLevel(0);
			updateWithLevel(mLevel, canvas);
			long time = 8;
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		updateWithLevel(mLevel,canvas);
	}

	private void updateWithLevel(float level,Canvas canvas) {
		if (level <0) {
//			amplitude = 0.25f;
			amplitude = 0;
			Log.d("dyf", "--this.i-----" + level);
		} else {
			phase += phaseShift;
			amplitude = level / 100 + idleAmplitude;
		}
		canvas.drawPath(mPath,mPaint);
//		invalidate();
//		postInvalidateDelayed(0);
	}

	public void stopWave(){
		mLevel = -1;
		invalidate();
	}

	public void startWave(int level){
		this.mLevel = level;
		invalidate();
	}
}
