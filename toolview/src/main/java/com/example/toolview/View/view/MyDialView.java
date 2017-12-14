package com.example.toolview.View.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.example.toolview.R;


/**
 * Created by Administrator on 2017/12/13.
 */

public class MyDialView extends View{
    private String TAG="DialView";
    private final Context mContext;
    //开始重绘
    private boolean start = true;
    private int mSection = 10; // 值域（mMax-mMin）等分份数
    private int mPortion = 10; // 一个mSection等分份数
    private float TICK_SPLIT_DEFAULT_ANGLE = 2.6f;
    private int screenWidth;//屏幕宽
    private int screenHeight;//屏幕高
    private int mDensityDpi;//屏幕dpi
//    private Paint mPaint;//画笔
    private int mStartAngle = 140; // 起始角度
    private float mSweepAngle =260; // 绘制角度

    private int raduis=100;//半径
    private int pointX, pointY;//圆心
    private Paint textPaint;//文字画笔
    private int speed;//速度
    // 速度文字 绘制的XY坐标
    private int baseX;
    private int baseY;
    //速度控制模式  1 加速  2 减速  3 手刹
    private int type;
    private Shader mShader;
    private Paint scale;
//    private Paint innerPaint;
    private Paint mPaint_2;
    private RectF speedRectFInner_2;
    private int brightColor=0xFFFCBB1D;
    private int darkColor=0xFF343232;
    private int fontsColor=0x55ffffff;
    private int fontsSize=80;
    private int number=4;//页面线束数量
    private int sequence=1;
    public MyDialView(Context context) {
        this(context, null);
    }

    public MyDialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
// 得到xml中的属性内容
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dialView);
        int sequence= typedArray.getInteger(R.styleable.dialView_sequence, 0);
        if (sequence != 0) {
           this.sequence=sequence;
        }
        //获取屏幕宽高 和 屏幕密度dpi
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        Log.e(TAG,"screenWidth="+screenWidth+"  screenHeight="+screenHeight);
        mDensityDpi = displayMetrics.densityDpi / 320;
        //关闭硬件加速
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //初始化 半径
        raduis = (screenWidth /number);
//        raduis = screenWidth /2;
        Log.e(TAG,"raduis="+raduis);
        //圆心
        pointX = (screenWidth / number)*sequence;
//        pointX = (screenWidth / 2);
        pointY = (screenHeight / 100)*75;

        //设置抗锯齿
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        //设置画笔颜色
        Shader textShader = new LinearGradient(pointX, pointY - raduis, pointX, pointY + raduis, new int[]{0xffffffff, 0x00ffffff, 0xffffffff}, null, Shader.TileMode.CLAMP);
        textPaint.setShader(textShader);

        // 获取字体并设置画笔字体
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "DINCondensedBold.ttf");
        textPaint.setTypeface(typeface);

        // 设置速度范围扇形的亮的颜色
        mShader = new LinearGradient(pointX - raduis, pointY, pointX + raduis,
                pointY, new int[]{brightColor, brightColor, brightColor}, null, Shader.TileMode.CLAMP);
//        speedAreaPaint.setShader(mShader);
        //短刻度画笔
        scale = new Paint(Paint.ANTI_ALIAS_FLAG);
        scale.setAntiAlias(true);
        scale.setColor(darkColor);
        //设置画笔样式
        scale.setStyle(Paint.Style.FILL);
        //画笔宽
        scale.setStrokeWidth(5 * mDensityDpi);

        //虚线画笔
        mPaint_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_2.setAntiAlias(true);
        //设置画笔样式
        mPaint_2.setStyle(Paint.Style.STROKE);
        //画笔宽
        mPaint_2.setColor(darkColor);
        mPaint_2.setStrokeWidth(10 * mDensityDpi);

//        //虚线弧外切矩形
        int i=175;
        speedRectFInner_2 = new RectF(pointX - raduis + i * mDensityDpi, pointY - raduis + i * mDensityDpi,
                pointX + raduis - i * mDensityDpi, pointY + raduis - i * mDensityDpi);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        widthSize = heightSize > widthSize ? widthSize : heightSize;
//        heightSize = heightSize > widthSize ? widthSize : heightSize;
        setMeasuredDimension(widthSize, heightSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0x00000000);
        //绘制扇形
        drawScale(canvas);
        //绘制中间文字内容
        drawCenter(canvas);

    }

    private void drawCenter(Canvas canvas) {
        //速度
        textPaint.setTextSize(fontsSize);
        float tw = textPaint.measureText(String.valueOf(speed));
        baseX = (int) (pointX - tw / 2);
        baseY = (int) (pointY + Math.abs(textPaint.descent() + textPaint.ascent()) / 4);
        Shader textShader = new LinearGradient(baseX, baseY - raduis / 4, baseX, baseY, new int[]{fontsColor, fontsColor}, null, Shader.TileMode.MIRROR);
        textPaint.setShader(textShader);
        canvas.drawText(String.valueOf(speed), baseX, baseY, textPaint);
    }
    private void drawScale(Canvas canvas) {
        mPaint_2.setShader(null);
        PathEffect effects = new DashPathEffect(new float[]{19, 3, 19, 3}, 0);
        mPaint_2.setPathEffect(effects);
        canvas.drawArc(speedRectFInner_2, mStartAngle - 1, mSweepAngle, false, mPaint_2);

        //渐变的短刻度
        for (int i = 0; i < mSection * mPortion; i++) {
//            float[] point = getCoordinatePoint(raduis - 80 * mDensityDpi, mStartAngle + TICK_SPLIT_DEFAULT_ANGLE * i);
//            float[] point1 = getCoordinatePoint(raduis - 50 * mDensityDpi, mStartAngle + TICK_SPLIT_DEFAULT_ANGLE * i);
            if (speed > i) {
                scale.setShader(mShader);
            } else {
                scale.setShader(null);
            }
//            canvas.drawLine(point[0], point[1], point1[0], point1[1], scale);
            mPaint_2.setPathEffect(effects);
            mPaint_2.setShader(mShader);
            canvas.drawArc(speedRectFInner_2, mStartAngle - 1, speed * TICK_SPLIT_DEFAULT_ANGLE, false, mPaint_2);
        }
    }


    public void setStart(boolean start) {
        this.start = start;
    }

    // 设置速度 并重绘视图
    public void setSpeed(int speed) {
        this.speed = speed;
        postInvalidate();
    }

    //设置速度控制模式
    public void setType(int type) {
        this.type = type;
    }


}
