package com.teamsun.bi.ui;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.TextView;

import com.teamsun.bi.demo.R;
import com.teamsun.bi.model.DataSet;

@SuppressWarnings("deprecation")
public class SimplePanelView extends PanelView {

	public SimplePanelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		coordinateYHeight = 10;
	}

	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		column2DView = getColumn2DViewInstance();

		if (column2DView == null) {
			return;
		}
		if (column2DView.isShowXLable()) {
			coordinateYHeight = 25;
		} else {
			coordinateYHeight = 10;
		}
		chartData = column2DView.getChartData(this);

	}

	public void processYPadding() {
		if (chartData != null) {
			int count = 0;
			Vector v = chartData.getValueArray();
			float min = 0, max = 0, sum = 0;

			int m = v.size();
			for (int i = 0; i < m; i++) {

				DataSet ds = (DataSet) v.elementAt(i);
				if (ds != null) {
					Float[] dsv = ds.getValue();
					int n = dsv.length;
					count += n;
					for (int j = 0; j < n; j++) {
						if (dsv[j] != null) {
							sum += dsv[j];
							if (min > dsv[j]) {
								min = dsv[j];
							}
							if (max < dsv[j]) {
								max = dsv[j];
							}
						}
					}
				}
			}
			avgValue = (int) sum / count;
			getAxisLimits(max, min);

			if (column2DView.isShowYLable() && scaleLableY != null) {
				// column2DView view=Column2DView.getInstance();
				AbsoluteLayout layout = (AbsoluteLayout) column2DView
						.findViewById(R.id.AbsoluteLayoutLeft);
				Rect rect = new Rect();
				layout.getDrawingRect(rect);

				rect.top += KTitleHeight;
				Rect r = new Rect();
				getDrawingRect(r);

				int p0 = (r.height() - coordinateYHeight) / KSegment;
				avgY = (maxY - avgValue) * (r.height() - coordinateYHeight)
						/ (maxY - minY);

				labelYView = new TextView[scaleLableY.length
						+ (chartData.isShowAvg() ? 1 : 0)];
				for (int i = 0; i < scaleLableY.length; i++) {

					String label = formatValue(scaleLableY[i], 2);

					TextView t = new TextView(mContext);
					t.setText(label);
					t.setTextSize(10);
					t.setTextColor(Color.WHITE);
					t.getPaint().getTextBounds(label, 0, label.length(), r);
					AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, 0, 0);

					param.x = rect.right - r.width() - 2;
					param.y = rect.top + p0 * i - (r.height() >> 1);

					if (scaleLableY[i] == 0) {
						zeroY = p0 * i;
						// zeroIndex=i;
					}

					t.setVisibility(View.INVISIBLE);
					labelYView[i] = t;
					layout.addView(t, param);
				}
				View vLine = new View(mContext);
				vLine.setBackgroundColor(Color.WHITE);
				AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams(
						1, p0 * (scaleLableY.length - 1) + 5, 0, 0);

				p.x = rect.right - 1;
				p.y = rect.top - (r.height() >> 1);
				layout.addView(vLine, p);
				/*
				 * if(chartData.isShowAvg()) { String label="average"; TextView
				 * t=new TextView(mContext); t.setText(label); t.setTextSize(9);
				 * t.setTextColor(Color.GREEN);
				 * t.getPaint().getTextBounds(label,0,label.length(), r);
				 * AbsoluteLayout.LayoutParams param = new
				 * AbsoluteLayout.LayoutParams
				 * (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0,0);
				 * param.x = rect.right-r.width()-1; param.y =
				 * rect.top+avgY-(r.height()>>1);
				 * 
				 * t.setVisibility(View.INVISIBLE);
				 * labelYView[labelYView.length-1]=t; layout.addView(t,param); }
				 */
			}
		}
	}

	protected void getAxisLimits(float maxValue, float minValue) {

		double maxPowerOfTen = Math.floor(Math.log10(Math.abs(maxValue))
				/ Math.log(10));
		// Get the minimum power of 10 that is applicable to maxvalue
		double minPowerOfTen = Math.floor(Math.log10(Math.abs(minValue))
				/ Math.log(10));
		// Find which powerOfTen (the max power or the min power) is bigger
		// It is this which will be multiplied to get the y-interval
		double powerOfTen = Math.max(minPowerOfTen, maxPowerOfTen);
		double y_interval = Math.pow(10, powerOfTen);
		// For accomodating smaller range values (so that scale doesn't
		// represent too large an interval
		if (Math.abs(maxValue) / y_interval < 2
				&& Math.abs(minValue) / y_interval < 2) {
			powerOfTen--;
			y_interval = Math.pow(10, powerOfTen);
		}
		// If the y_interval of min and max is way more than that of range.
		// We need to reset the y-interval as per range
		double rangePowerOfTen = Math.floor(Math.log10(maxValue - minValue)
				/ Math.log(10));
		double rangeInterval = Math.pow(10, rangePowerOfTen);
		// Now, if rangeInterval is 10 times less than y_interval, we need to
		// re-set
		// the limits, as the range is too less to adjust the axis for max,min.
		// We do this only if range is greater than 0 (in case of 1 data on
		// chart).
		if (((maxValue - minValue) > 0) && ((y_interval / rangeInterval) >= 10)) {
			y_interval = rangeInterval;
			powerOfTen = rangePowerOfTen;
		}
		// Calculate the y-axis upper limit
		double y_topBound = (Math.floor(maxValue / y_interval) + 1)
				* y_interval;
		// Calculate the y-axis lower limit
		double y_lowerBound = 0;
		// If the min value is less than 0
		if (minValue < 0) {
			// Then calculate by multiplying negative numbers with y-axis
			// interval
			y_lowerBound = -1
					* ((Math.floor(Math.abs(minValue / y_interval)) + 1) * y_interval);
		} else {
			// Else, simply set it to 0.
			// if (setMinAsZero)
			// {
			y_lowerBound = 0;
			// } else
			// {
			// y_lowerBound = Math.floor (Math.abs (minValue / y_interval) - 1)
			// * y_interval;
			// //Now, if minValue>=0, we keep x_lowerBound to 0 - as for values
			// like minValue 2
			// //lower bound goes negative, which is not required.
			// y_lowerBound = (y_lowerBound < 0) ?0 : y_lowerBound;
			// }
		}
		// MaxValue cannot be less than 0 if stopMaxAtZero is set to true
		if (maxValue <= 0) {
			y_topBound = 0;
		}

		maxY = (int) y_topBound;
		minY = (int) y_lowerBound;
		if(maxY==minY)
		{
			if(y_interval==0)
			{
				y_interval =10;
			}
			minY-=y_interval;
			maxY+=y_interval;
		}

		// intervalY = (int)y_interval;

		float range = Math.abs(maxY - minY);
		if (!column2DView.isShowYLable()) {
			Rect r = new Rect();
			this.getDrawingRect(r);
			if (maxY == 0) {
				zeroY = r.top;
			} else if (minY == 0) {
				zeroY = r.bottom;
			} else {
				zeroY = maxY * r.height() / range;
			}

			return;
		}
		float p1 = range / KSegment;
		while (range % KSegment != 0 || range / KSegment % y_interval != 0) {
			range += y_interval;
			p1 = range / KSegment;
		}

		scaleLableY = new int[KSegment + 1];
		if (maxY > 0 && minY >= 0) {
			for (int i = 0; i <= KSegment; i++) {
				scaleLableY[KSegment - i] = (int) (i * p1);
			}
		} else {
			if (maxY < 0 && minY <= 0) {
				for (int i = 0; i <= KSegment; i++) {
					scaleLableY[i] = (int) (-i * p1);
				}
			} else {
				if (maxY > 0 && minY < 0) {
					if (maxY % p1 != 0) {
						float p2 = maxY / p1 + 1;
						while (p2 >= 5) {
							p2 = maxY / (p1 += (int) y_interval) + 1;
						}

						maxY = p2 * p1;
						while (minY < maxY - 5 * p1) {
							p1 += (int) y_interval;
							maxY = p2 * p1;
						}
						minY = maxY - 5 * p1;
						for (int i = 0; i <= KSegment; i++) {
							scaleLableY[i] = (int) (maxY - i * p1);
						}
					} else {
						for (int i = 0; i <= KSegment; i++) {
							scaleLableY[i] = (int) (maxY - i * p1);
						}
					}
				}
			}
		}

	}

	void setupTitle() {
		if (chartData != null) {
			String title = chartData.getTitle();
			if (title != null) {
				TextView tv = (TextView) column2DView
						.findViewById(R.id.TitleView);
				tv.setText(title);
			}
		}
	}

	void processColumnMap() {
		if (chartData != null) {
			int x, y, h, w = KColumnWidth;

			int countCol = countColumnMap();
			int countColWidth = w * countCol;
			column2DChartArray = null;
			System.gc();
			if (countCol > 0) {
				column2DChartArray = new Vector();
			}

			float max = maxY;// scaleLableY[0];
			float min = minY;// scaleLableY[scaleLableY.length-1];
			float range = max - min;

			Vector v = chartData.getValueArray();

			Rect rect = new Rect();
			this.getDrawingRect(rect);
			rect.bottom -= coordinateYHeight;

			int size = v.size();

			int n = 0;
			int color = 0;
			for (int i = 0; i < size; i++) {
				DataSet ds = (DataSet) v.elementAt(i);

				if (ds.getType() == DataSet.DataType.DataTypeColumn) {
					Float[] dv = ds.getValue();
					for (int j = 0; j < dv.length; j++) {
						x = intervalX * (j + 1) - ((countColWidth) >> 1)
								- ((countColWidth) >> 1) + n * w;
						if (dv[j] != null) {
							float num = dv[j];
							if (num > 0) {
								h = (int) (num * (zeroY - rect.top) / max);
								y = (int) (zeroY - h);
							} else {
								h = (int) Math.abs((int) num
										* (rect.bottom - zeroY) / min);
								y = (int) zeroY;
							}

							AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT, 0, 0);
							param.x = x;
							// ����1px ����
							param.x += (n > 0 ? -1 * n : 0);
							param.y = y - 2;

							param.height = h;
							param.width = w;

							Column2D b = new Column2D(mContext);
							b.setValue(num);
							if (countCol == 1) {
								b.setSimple(true);
							}

							b.setLayoutParams(param);
							column2DChartArray.add(b);

						}
					}
					n++;
					if (countCol > 1 && patternName != null) {
						// String l=ds.getSeriesName();
						// if(l==null)
						// patternName.add("");
						// else
						patternName.add(ds.getSeriesName());
						patternName.add(new Integer(Column2DView.colors[color++
								% Column2DView.colors.length]));
					} else {

						color = dv.length;
					}
				} else if (ds.getType() == DataSet.DataType.DataTypeArea) {
					processAreaMap(ds, rect);
					patternName.add(ds.getSeriesName());
					patternName.add(new Integer(Column2DView.colors[color++
							% Column2DView.colors.length]));

				} else if (ds.getType() == DataSet.DataType.DataTypeLine) {
					processLineMap(ds, rect);
					patternName.add(ds.getSeriesName());
					patternName.add(new Integer(Column2DView.colors[color++
							% Column2DView.colors.length]));
				}

			}

			processPattern(patternName);
		}
	}

	public void processXPadding() {
		if (column2DView.isShowXLable()) {
			super.processXPadding();
			return;
		}
		// int colCount = 0;
		int labelXCount = 0;

		if (chartData != null) {

			Vector labels = chartData.getDispLabelX();
			labelXCount = labels.size();

			Rect r = new Rect();
			this.getDrawingRect(r);
			int maxW = 0;

			maxW = r.width() / labelXCount - 1;
			int count = countColumnMap();

			int padding = 0;
			if (count >= 2) {
				KColumnWidth = maxW / count;
				if (KColumnWidth < 5) {
					KColumnWidth = 5;
				}

				// KColumnWidth=maxW;
				padding = maxW;
			} else {
				KColumnWidth = maxW;
				padding = KColumnWidth * count;
			}
			if (padding > maxW) {
				maxW = padding;
			}
			// ----------

			// maxW+=maxW/4;
			intervalX = maxW; //
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (chartData == null)
			return;
		Paint mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(Color.WHITE);
		// canvas.drawLine(0, 0, 0, this.getHeight()-coordinateYHeight, mPaint);
		canvas.drawLine(0, zeroY, maxWidth, zeroY, mPaint);

		int countX = chartData.getDispLabelX().size();
		int w = intervalX * (countX - 1) + 8;
		int curID = chartData.getCurDataId();
		if (curID >= 0) {
			int perW = w / (countX - 1);
			int x = intervalX - ((KColumnWidth * countColumnMap()) >> 1) - 4;

			if (curID == countX - 1) {
				x += perW - 4;
			} else
				x += 4 + perW * curID;
			canvas.drawLine(x, 0, x, getHeight() - coordinateYHeight, mPaint);

		}
	}

	public void drawBg(Canvas canvas) {
		// canvas.getClipBounds(mRect);

		getDrawingRect(mRect);

		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL_AND_STROKE);
		mPaint.setColor(Color.WHITE);

		canvas.drawRoundRect(new RectF(mRect), 4, 4, mPaint);
		// canvas.drawRect(mRect, mPaint);

		Rect r = new Rect(mRect);
		r.top = r.bottom - coordinateYHeight;

		mRect.bottom -= +r.height();
		// drawBgGrid(mRect,canvas,mPaint);

		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(2);
		mPaint.setColor(Kedgecolor);
		mRect.left += 1;
		mRect.top += 1;
		mRect.right -= 1;
		mRect.bottom -= 1;
		// canvas.drawRect(mRect,mPaint);
		canvas.drawRoundRect(new RectF(mRect), 4, 4, mPaint);

	}
}
