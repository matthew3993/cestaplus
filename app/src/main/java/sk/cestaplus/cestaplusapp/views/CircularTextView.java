package sk.cestaplus.cestaplusapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import sk.cestaplus.cestaplusapp.R;

/* SOURCES:
    original class CircularTextView: http://stackoverflow.com/a/34685568
    changes in constructors: http://stackoverflow.com/questions/7608464/android-custom-ui-with-custom-attributes
 */
public class CircularTextView extends TextView
{
    private float strokeWidth;
    int strokeColor,solidColor;

    public CircularTextView(Context context) {
        super(context);
    }

    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // SOURCE: http://stackoverflow.com/questions/7608464/android-custom-ui-with-custom-attributes
        // point 3. Make use of the values you get passed
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircularTextView, 0, 0);
        try {

            // SOURCE of get color int from resource:
            // http://stackoverflow.com/questions/5271387/get-color-int-from-color-resource
            // - in accepted answer in COMMENTS!!
            int defColorVal = ContextCompat.getColor(context, R.color.black);
            solidColor = ta.getColor(R.styleable.CircularTextView_solidColor, defColorVal);
        } finally {
            ta.recycle();
        }
    }

    public CircularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void draw(Canvas canvas) {

        Paint circlePaint = new Paint();
        circlePaint.setColor(solidColor);
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        Paint strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        int h = this.getHeight();
        int w = this.getWidth();

        int diameter = ((h > w) ? h : w);
        int radius = diameter / 2;

        this.setHeight(diameter);
        this.setWidth(diameter);

        canvas.drawCircle(diameter / 2, diameter / 2, radius, strokePaint);

        canvas.drawCircle(diameter / 2, diameter / 2, radius - strokeWidth, circlePaint);

        super.draw(canvas);
    }

    public void setStrokeWidth(int dp)
    {
        float scale = getContext().getResources().getDisplayMetrics().density;
        strokeWidth = dp*scale;

    }

    public void setStrokeColor(String color)
    {
        strokeColor = Color.parseColor(color);
    }

    public void setSolidColor(String color)
    {
        solidColor = Color.parseColor(color);

    }
}