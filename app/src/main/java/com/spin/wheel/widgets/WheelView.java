package com.spin.wheel.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;

import com.spin.wheel.R;
import com.spin.wheel.manager.MyDBAdapter;
import com.spin.wheel.manager.Prefs;

import java.util.List;
import java.util.Random;

public class WheelView extends View {
    public boolean lifted;
    private Paint circlePaint;
    private long currTime;
    private String item;
    private Paint item10Paint;
    private Paint item11Paint;
    private Paint item12Paint;
    private Paint item13Paint;
    private Paint item14Paint;
    private Paint item15Paint;
    private Paint item1Paint;
    private Paint item2Paint;
    private Paint item3Paint;
    private Paint item4Paint;
    private Paint item5Paint;
    private Paint item6Paint;
    private Paint item7Paint;
    private Paint item8Paint;
    private Paint item9Paint;
    private float itemLength;
    private List<String> itemNames;
    private double lastTheta;
    private String listName;
    private Paint markerPaint;
    private long oldTime;
    private Paint pointerPaint;
    private int px;
    private int py;
    private float radius;
    private MediaPlayer resourcePlayer;
    private Paint rimPaint;
    private double rotationOffset;
    private int selected;
    private Paint textPaint;
    private boolean textSet;
    private long timeDiff;
    private Paint titlePaint;
    private String winner;

    public WheelView(Context context) {
        super(context);
        initWheelView();
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWheelView();
    }

    public WheelView(Context context, AttributeSet ats, int defaultStyle) {
        super(context, ats, defaultStyle);
        initWheelView();
    }

    protected void initWheelView() {
        setFocusable(true);
        Resources r = getResources();
        this.circlePaint = new Paint(1);
        this.circlePaint.setColor(r.getColor(R.color.wheel_color));
        this.circlePaint.setStrokeWidth(5.0f);
        this.circlePaint.setStyle(Style.STROKE);
        this.rimPaint = new Paint(1);
        this.rimPaint.setColor(r.getColor(R.color.text_color));
        this.rimPaint.setStrokeWidth(1.0f);
        this.rimPaint.setStyle(Style.STROKE);
        this.textPaint = new Paint(1);
        this.textPaint.setColor(r.getColor(R.color.text_color));
        this.titlePaint = new Paint(1);
        this.titlePaint.setColor(r.getColor(R.color.text_color));
        this.pointerPaint = new Paint(1);
        this.pointerPaint.setColor(r.getColor(R.color.pointer_color));
        this.markerPaint = new Paint(1);
        this.markerPaint.setStrokeWidth(2.0f);
        this.markerPaint.setColor(r.getColor(R.color.marker_color));
        this.item1Paint = new Paint(1);
        this.item1Paint.setColor(r.getColor(R.color.item1_color));
        this.item2Paint = new Paint(1);
        this.item2Paint.setColor(r.getColor(R.color.item2_color));
        this.item3Paint = new Paint(1);
        this.item3Paint.setColor(r.getColor(R.color.item3_color));
        this.item4Paint = new Paint(1);
        this.item4Paint.setColor(r.getColor(R.color.item4_color));
        this.item5Paint = new Paint(1);
        this.item5Paint.setColor(r.getColor(R.color.item5_color));
        this.item6Paint = new Paint(1);
        this.item6Paint.setColor(r.getColor(R.color.item6_color));
        this.item7Paint = new Paint(1);
        this.item7Paint.setColor(r.getColor(R.color.item7_color));
        this.item8Paint = new Paint(1);
        this.item8Paint.setColor(r.getColor(R.color.item8_color));
        this.item9Paint = new Paint(1);
        this.item9Paint.setColor(r.getColor(R.color.item9_color));
        this.item10Paint = new Paint(1);
        this.item10Paint.setColor(r.getColor(R.color.item10_color));
        this.item11Paint = new Paint(1);
        this.item11Paint.setColor(r.getColor(R.color.item11_color));
        this.item12Paint = new Paint(1);
        this.item12Paint.setColor(r.getColor(R.color.item12_color));
        this.item13Paint = new Paint(1);
        this.item13Paint.setColor(r.getColor(R.color.item13_color));
        this.item14Paint = new Paint(1);
        this.item14Paint.setColor(r.getColor(R.color.item14_color));
        this.item15Paint = new Paint(1);
        this.item15Paint.setColor(r.getColor(R.color.item15_color));
        this.lifted = false;
        this.selected = -1;
        this.lastTheta = 0.0d;
        this.timeDiff = 1;
        this.oldTime = System.currentTimeMillis();
        this.currTime = System.currentTimeMillis();
        this.winner = "";
        this.resourcePlayer = MediaPlayer.create(getContext(), R.raw.clack);
        this.item = "";
        this.itemLength = 0.0f;
        this.listName = "";
        this.textSet = false;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == 0) {
            return 200;
        }
        return specSize;
    }

    public boolean withinWheel(float _x, float _y) {
        return getRadius() * getRadius() > ((_x - ((float) getPX())) * (_x - ((float) getPX()))) + ((((float) getPY()) - _y) * (((float) getPY()) - _y));
    }

    public void setList(String _listName) {
        this.listName = _listName;
    }

    public void setRotation(double _rotation) {
        this.rotationOffset = _rotation % 360.0d;
    }

    public double getRotationNumber() {
        return this.rotationOffset;
    }

    public void setLastTheta(double _lastTheta, float width) {
        float offset = 12800.0f / width;
        if (_lastTheta < -40.0d) {
            _lastTheta = (double) (-offset);
        }
        if (_lastTheta > 40.0d) {
            _lastTheta = (double) offset;
        }
        this.lastTheta = _lastTheta;
    }

    public void setItems(List<String> items) {
        this.itemNames = items;
    }

    public int getPX() {
        return getMeasuredWidth() / 2;
    }

    public int getPY() {
        return getMeasuredHeight() / 2;
    }

    public float getRadius() {
        return ((float) Math.min(getPX(), getPY())) * 0.8f;
    }

    protected void onDraw(Canvas canvas) {
        if (this.itemNames != null) {
            int i;
            float offSet;
            this.px = getMeasuredWidth() / 2;
            this.py = getMeasuredHeight() / 2;
            if (!this.textSet) {
                this.textPaint.setTextSize((float) (getMeasuredWidth() / 25));
                this.titlePaint.setTextSize((float) (getMeasuredWidth() / 15));
                this.textSet = true;
            }
            this.radius = ((float) Math.min(this.px, this.py)) * 0.8f;
            float incr = 360.0f / ((float) this.itemNames.size());
            RectF myCircleBox = new RectF(((float) this.px) - this.radius, ((float) this.py) - this.radius, ((float) this.px) + this.radius, ((float) this.py) + this.radius);
            RectF rectF = new RectF(((float) this.px) - (this.radius / 5.0f), ((float) this.py) - (this.radius * 1.1f), ((float) this.px) + (this.radius / 5.0f), ((float) this.py) - (this.radius * 0.6f));
            rectF = new RectF((((float) this.px) - (this.radius / 5.0f)) + 5.0f, (((float) this.py) - (this.radius * 1.1f)) + 2.0f, (((float) this.px) + (this.radius / 5.0f)) - 5.0f, (((float) this.py) - (this.radius * 0.6f)) - 12.0f);
            double actualRotation = this.rotationOffset + ((double) ((incr / 2.0f) + 90.0f));
            canvas.save();
            canvas.rotate((float) this.rotationOffset, (float) this.px, (float) this.py);
            for (i = 0; i < this.itemNames.size(); i++) {
                Paint itemPaint;
                switch (i) {
                    case MyDBAdapter.KEY_ID_COLUMN /*0*/:
                        itemPaint = this.item1Paint;
                        break;
                    case MyDBAdapter.NAME_COLUMN /*1*/:
                        itemPaint = this.item2Paint;
                        break;
                    case MyDBAdapter.NAME_REF /*2*/:
                        itemPaint = this.item3Paint;
                        break;
                    case 3:
                        itemPaint = this.item4Paint;
                        break;
                    case 4:
                        itemPaint = this.item5Paint;
                        break;
                    case 5:
                        itemPaint = this.item6Paint;
                        break;
                    case 6:
                        itemPaint = this.item7Paint;
                        break;
                    case 7:
                        itemPaint = this.item8Paint;
                        break;
                    case 8:
                        itemPaint = this.item9Paint;
                        break;
                    case 9:
                        itemPaint = this.item10Paint;
                        break;
                    case 10:
                        itemPaint = this.item11Paint;
                        break;
                    case 11:
                        itemPaint = this.item12Paint;
                        break;
                    case 12:
                        itemPaint = this.item13Paint;
                        break;
                    case 13:
                        itemPaint = this.item14Paint;
                        break;
                    case 14:
                        itemPaint = this.item15Paint;
                        break;
                    default:
                        itemPaint = this.item1Paint;
                        break;
                }
                offSet = ((float) i) * (-incr);
                canvas.save();
                canvas.rotate(offSet, (float) this.px, (float) this.py);
                canvas.drawArc(myCircleBox, (-incr) / 2.0f, incr, true, itemPaint);
                this.item = ((String) this.itemNames.get(i)).toString();
                this.itemLength = (float) ((int) this.textPaint.measureText(this.item));
                if (((double) this.itemLength) > ((double) this.radius) * 0.8d) {
                    this.item = this.item.substring(0, (int) (((double) this.item.length()) * ((((double) this.radius) * 0.8d) / ((double) ((int) this.textPaint.measureText(this.item))))));
                    this.itemLength = (float) ((int) this.textPaint.measureText(this.item));
                }
                canvas.drawText(this.item, (((float) this.px) + (this.radius / 2.0f)) - (this.itemLength / 2.0f), (float) (this.py + ((int) (((double) this.textPaint.measureText("yY")) / 2.5d))), this.textPaint);
                canvas.restore();
            }
            for (i = 0; i < this.itemNames.size(); i++) {
                offSet = ((float) i) * incr;
                canvas.save();
                canvas.rotate(offSet, (float) this.px, (float) this.py);
                canvas.save();
                canvas.rotate(incr / 2.0f, (float) this.px, (float) this.py);
                canvas.drawLine(((float) this.px) + this.radius, (float) this.py, (float) this.px, (float) this.py, this.markerPaint);
                canvas.restore();
                canvas.restore();
            }
            canvas.restore();
            canvas.drawCircle((float) this.px, (float) this.py, this.radius, this.circlePaint);
            canvas.drawCircle((float) this.px, (float) this.py, this.radius + (this.circlePaint.getStrokeWidth() * 0.5f), this.rimPaint);
            canvas.drawCircle((float) this.px, (float) this.py, 2.0f, this.rimPaint);
            canvas.drawCircle((float) this.px, (float) this.py, 1.0f, this.rimPaint);
            canvas.drawArc(rectF, 250.0f, 40.0f, true, this.textPaint);
            canvas.drawArc(rectF, 250.0f, 40.0f, true, this.pointerPaint);
            if (actualRotation < 0.0d) {
                actualRotation += 360.0d;
            } else if (actualRotation > 360.0d) {
                actualRotation -= 360.0d;
            }
            if (!(this.selected == -1 || this.selected == ((int) (actualRotation / ((double) incr))) || !Prefs.getMusic(getContext()) || this.resourcePlayer == null)) {
                if (this.resourcePlayer.isPlaying()) {
                    this.resourcePlayer.seekTo(0);
                } else {
                    this.resourcePlayer.start();
                }
            }
            this.selected = (int) (actualRotation / ((double) incr));
            if (this.lifted) {
                this.winner = ((String) this.itemNames.get(this.selected)).toString();
            }
            canvas.drawText(this.winner, (float) (this.px - (((int) this.titlePaint.measureText(this.winner)) / 2)), (((float) this.py) + this.radius) + ((float) (getMeasuredWidth() / 10)), this.titlePaint);
            canvas.drawText(this.listName, (float) (this.px - (((int) this.titlePaint.measureText(this.listName)) / 2)), (((float) this.py) - this.radius) - ((float) (getMeasuredWidth() / 18)), this.titlePaint);
            if (this.lifted) {
                long currentTimeMillis;
                if (this.lastTheta > 0.0d) {
                    currentTimeMillis = System.currentTimeMillis();
                    this.currTime = currentTimeMillis;
                    this.timeDiff = currentTimeMillis - this.oldTime;
                    this.oldTime = this.currTime;
                    setRotation(getRotationNumber() + this.lastTheta);
                    this.lastTheta -= (double) (0.002f * ((float) this.timeDiff));
                    if (this.lastTheta <= 0.0d) {
                        this.lifted = false;
                    }
                } else {
                    currentTimeMillis = System.currentTimeMillis();
                    this.currTime = currentTimeMillis;
                    this.timeDiff = currentTimeMillis - this.oldTime;
                    this.oldTime = this.currTime;
                    setRotation(getRotationNumber() + this.lastTheta);
                    this.lastTheta += (double) (0.002f * ((float) this.timeDiff));
                    if (this.lastTheta >= 0.0d) {
                        this.lifted = false;
                    }
                }
                invalidate();
            }
        }
    }

    void OnDestroy() {
        if (this.resourcePlayer != null) {
            this.resourcePlayer.release();
        }
    }

    public void autoSpin(float seed) {
        if (seed >= 20.0f) {
            seed = 15.0f;
        }
        this.lastTheta = (double) (((new Random().nextFloat() * seed) + seed) + 5.0f);
        this.lifted = true;
        invalidate();
    }

}
