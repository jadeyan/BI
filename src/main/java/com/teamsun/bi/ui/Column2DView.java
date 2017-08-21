package com.teamsun.bi.ui;

import com.teamsun.bi.demo.*;
import com.teamsun.bi.model.ChartData;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Column2DView extends ViewGroup {

	final static int bgcolor = 0xffE8E8E8;
	final static int edgecolor = 0xff545454;
	public static int colors[] = { 0xAFD8F8, 0xF6BD0F, 0x8BBA00, 0xFF8E46,
			0x008E8E, 0xD64646, 0x8E468E, 0x588526, 0xB3AA00, 0x008ED6,
			0x9D080D, 0xA186BE, 0xCC6600, 0xFDC689, 0xABA000, 0xF26D7D,
			0xFFF200, 0x0054A6, 0xF7941C, 0xCC3300, 0x006600, 0x663300,
			0x6DCFF6 };

	final static int KCoordinateTextColor = 0xff555555;

	protected Paint mPaint;
	protected Context mContext;
	Rect mRect = new Rect();
	// int defaultH;

	View child;
	protected ChartData chartData;

	private boolean showXLable;
	private boolean showYLable;
	protected boolean showPatten;

	public Column2DView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mPaint = new Paint();

		mPaint.setAlpha(255);

		setWillNotDraw(false);

		showXLable = true;
		showYLable = true;
		showPatten = true;

	}

	public void clear() {
		if (chartData != null) {
			chartData.clear();
		}
		if (child != null) {
			if (child instanceof LinearLayout) {
				LinearLayout l = (LinearLayout) child;
				int c = l.getChildCount();
				if (c > 1) {
					View v = l.getChildAt(1);
					if (v instanceof LinearLayout) {
						l = (LinearLayout) v;
						c = l.getChildCount();
						if (c > 1) {
							v = l.getChildAt(1);
							if (v instanceof PanelView) {
								PanelView p = (PanelView) v;
								p.clear();
							}
						}
					}
				}
			}
			((LinearLayout) child).removeAllViews();
			child = null;
		}
		removeAllViews();
	}

	protected void setupLayout() {
		LayoutInflater factory = LayoutInflater.from(mContext);
		LinearLayout layout = (LinearLayout) factory.inflate(
				R.layout.xychartlayout2, null);
		layout.setBackgroundColor(bgcolor);
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		this.addView(layout, params1);

		final int count = getChildCount();
		if (count > 0)
			child = getChildAt(0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onAttachedToWindow()
	 */
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		setupLayout();
	}

	public ChartData getChartData(View v) {
		return chartData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.getClipBounds(mRect);
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(bgcolor);
		canvas.drawRect(mRect, mPaint);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(1);
		mPaint.setColor(edgecolor);
		mRect.left += 1;
		mRect.top += 1;
		mRect.right -= 2;
		mRect.bottom -= 2;
		canvas.drawRect(mRect, mPaint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (child != null)
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
		// setMeasuredDimension(measureWidth(widthMeasureSpec),
		// measureHeight(heightMeasureSpec));

		Log.e("nan.onMeasure", widthMeasureSpec + "   " + heightMeasureSpec
				+ " " + this.getMeasuredHeight());
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			// result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
			// + getPaddingRight();
			// if (specMode == MeasureSpec.AT_MOST) {
			// // Respect AT_MOST value if that was what is called for by
			// measureSpec
			// result = Math.min(result, specSize);
			// }
			result = specSize;
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = 200;// defaultH ;
			// + getPaddingTop()
			// + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);

			}
		}
		return result;
	}

	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		if (child == null)
			return;
		child.setVisibility(View.VISIBLE);

		Log.e("nan.onLayout", l + "   " + t + "   " + r + "  " + b + "  "
				+ child.getMeasuredWidth() + "  " + child.getMeasuredHeight());
		child.layout(0, 0, r, b);

	}

	/**
	 * @return the chartData
	 */
	public ChartData getChartData() {
		return chartData;
	}

	/**
	 * @param chartData
	 *            the chartData to set
	 */
	public void setChartData(ChartData chartData) {
		this.chartData = chartData;
	}

	/**
	 * @return the showXLable
	 */
	public boolean isShowXLable() {
		return showXLable;
	}

	/**
	 * @param showXLable
	 *            the showXLable to set
	 */
	public void setShowXLable(boolean showXLable) {
		this.showXLable = showXLable;
	}

	/**
	 * @return the showYLable
	 */
	public boolean isShowYLable() {
		return showYLable;
	}

	/**
	 * @param showYLable
	 *            the showYLable to set
	 */
	public void setShowYLable(boolean showYLable) {
		this.showYLable = showYLable;
	}

	/**
	 * @return the showPatten
	 */
	public boolean isShowPatten() {
		return showPatten;
	}

	/**
	 * @param showPatten
	 *            the showPatten to set
	 */
	public void setShowPatten(boolean showPatten) {
		this.showPatten = showPatten;
	}

}
