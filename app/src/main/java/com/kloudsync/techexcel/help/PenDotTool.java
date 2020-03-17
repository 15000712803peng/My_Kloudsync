package com.kloudsync.techexcel.help;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

import com.google.common.collect.ArrayListMultimap;
import com.kloudsync.techexcel.bean.Dots;
import com.kloudsync.techexcel.ui.DrawView;
import com.tqltech.tqlpencomm.Dot;

import java.util.List;
import java.util.Set;

/**
 * Created by tonyan on 2020/3/15.
 */

public class PenDotTool {


	private static final String TAG = "PenDotTool";
	private static float pointX;
	private static float pointY;
	private static int pointZ;
	private static float gpointX;
	private static float gpointY;

	private static double XDIST_PERUNIT = DotConstants.XDIST_PERUNIT;  //码点宽
	private static double YDIST_PERUNIT = DotConstants.YDIST_PERUNIT;  //码点高
	private static double A5_WIDTH = DotConstants.A5_WIDTH;            //本子宽
	private static double A5_HEIGHT = DotConstants.A5_HEIGHT;          //本子高
	private static int BG_REAL_WIDTH = DotConstants.BG_REAL_WIDTH;     //资源背景图宽
	private static int BG_REAL_HEIGHT = DotConstants.BG_REAL_HEIGHT;   //资源背景图高

	private static int BG_WIDTH;                                    //显示背景图宽
	private static int BG_HEIGHT;                                   //显示背景图高
	private static int A5_X_OFFSET;                                 //笔迹X轴偏移量
	private static int A5_Y_OFFSET;                                 //笔迹Y轴偏移量
	private static int gcontentLeft;                                //内容显示区域left坐标
	private static int gcontentTop;
	private static int gPIndex;


	private static float mov_x;                                     //声明起点坐标
	private static float mov_y;                                     //声明起点坐标
	private static int gCurPageID = -1;                             //当前PageID
	private static int gCurBookID = -1;                             //当前BookID
	private static float gScale = 1;                                //笔迹缩放比例
	private static int gColor = 6;                                  //笔迹颜色
	private static int gWidth = 1;                                  //笔迹粗细
	private static int gSpeed = 30;                                 //笔迹回放速度
	private static float gOffsetX = 0;                              //笔迹x偏移
	private static float gOffsetY = 0;

	private static boolean gbSetNormal = false;
	private static boolean gbCover = false;
	private static boolean bIsOfficeLine = false;
	public static DrawView drawView;  //add 2016-06-15 for draw

	private static ArrayListMultimap<Integer, Dots> dot_number = ArrayListMultimap.create();  //Book=100笔迹数据
	private static ArrayListMultimap<Integer, Dots> dot_number1 = ArrayListMultimap.create(); //Book=0笔迹数据
	private static ArrayListMultimap<Integer, Dots> dot_number2 = ArrayListMultimap.create(); //Book=1笔迹数据
	private static ArrayListMultimap<Integer, Dots> dot_number4 = ArrayListMultimap.create(); //笔迹回放数据

	public static float g_x0, g_x1, g_x2, g_x3;
	public static float g_y0, g_y1, g_y2, g_y3;
	public static float g_p0, g_p1, g_p2, g_p3;
	public static float g_vx01, g_vy01, g_n_x0, g_n_y0;
	public static float g_vx21, g_vy21;
	public static float g_norm;
	public static float g_n_x2, g_n_y2;


	public static void processEachDot(Dot dot, DrawView _drawView) {
		drawView = _drawView;
		Log.e(TAG, "111 ProcessEachDot=" + dot.toString());
		int counter = 0;
		pointZ = dot.force;
		counter = dot.Counter;
		if (pointZ < 0) {
			//Log.i(TAG, "Counter=" + counter + ", Pressure=" + pointZ + "  Cut!!!!!");
			return;
		}

		int tmpx = dot.x;
		pointX = dot.fx;
		pointX /= 100.0;
		pointX += tmpx;

		int tmpy = dot.y;
		pointY = dot.fy;
		pointY /= 100.0;
		pointY += tmpy;

		gpointX = pointX;
		gpointY = pointY;

		pointX *= (BG_WIDTH);
		float ax = (float) (A5_WIDTH / XDIST_PERUNIT);
		pointX /= ax;

		pointY *= (BG_HEIGHT);
		float ay = (float) (A5_HEIGHT / YDIST_PERUNIT);
		pointY /= ay;

		pointX += A5_X_OFFSET;
		pointY += A5_Y_OFFSET;


		if (pointZ > 0) {
			if (dot.type == Dot.DotType.PEN_DOWN) {
				//Log.i(TAG, "PEN_DOWN");
				gPIndex = 0;
				int pageID, bookID;
				pageID = dot.PageID;
				bookID = dot.BookID;
				if (pageID < 0 || bookID < 0) {
					// 谨防笔连接不切页的情况
					return;
				}

				//Log.i(TAG, "PageID=" + PageID + ",gCurPageID=" + gCurPageID + ",BookID=" + BookID + ",gCurBookID=" + gCurBookID);
				if (pageID != gCurPageID || bookID != gCurBookID) {
					gbSetNormal = false;
					bIsOfficeLine = true;
					gCurPageID = pageID;
					gCurBookID = bookID;
					drawInit();
					drawExistingStroke(gCurBookID, gCurPageID);
				}

				setPenColor(gColor);
				drawSubFountainPen3(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 0);
				drawView.invalidate((int) (Math.min(mov_x, pointX)) - 200, (int) (Math.min(mov_y, pointY)) - 200, (int) (Math.max(mov_x, pointX)) + 200, (int) (Math.max(mov_y, pointY)) + 200);
				//TODO new draw
				//newDrawPenView.onDrawDot(pointX, pointY, pointZ, 0);

				// 保存屏幕坐标，原始坐标会使比例缩小
				saveData(gCurBookID, gCurPageID, pointX, pointY, pointZ, 0, gWidth, gColor, dot.Counter, dot.angle);
				mov_x = pointX;
				mov_y = pointY;
				return;
			}

			if (dot.type == Dot.DotType.PEN_MOVE) {
				//Log.i(TAG, "PEN_MOVE");
				//gPIndex = 0;
				// Pen Move
				gPIndex += 1;
				mov_x = pointX;
				mov_y = pointY;
				setPenColor(gColor);
				drawSubFountainPen3(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, pointZ, 1);
				drawView.invalidate((int) (Math.min(mov_x, pointX)) - 200, (int) (Math.min(mov_y, pointY)) - 200, (int) (Math.max(mov_x, pointX)) + 200, (int) (Math.max(mov_y, pointY)) + 200);
				//TODO new draw
				//newDrawPenView.onDrawDot(pointX, pointY, pointZ, 1);

				// 保存屏幕坐标，原始坐标会使比例缩小
				saveData(gCurBookID, gCurPageID, pointX, pointY, pointZ, 1, gWidth, gColor, dot.Counter, dot.angle);
			}
		} else if (dot.type == Dot.DotType.PEN_UP) {
			//Log.i(TAG, "PEN_UP");
			// Pen Up
			if (dot.x == 0 || dot.y == 0) {
				pointX = mov_x;
				pointY = mov_y;
			}

			gPIndex += 1;
			drawSubFountainPen3(drawView, gScale, gOffsetX, gOffsetY, gWidth, pointX, pointY, 0, 2);
			drawView.invalidate((int) (Math.min(mov_x, pointX)) - 200, (int) (Math.min(mov_y, pointY)) - 200, (int) (Math.max(mov_x, pointX)) + 200, (int) (Math.max(mov_y, pointY)) + 200);
			//TODO new draw
			//newDrawPenView.onDrawDot(pointX, pointY, pointZ, 2);

			// 保存屏幕坐标，原始坐标会使比例缩小
			saveData(gCurBookID, gCurPageID, pointX, pointY, pointZ, 2, gWidth, gColor, dot.Counter, dot.angle);
			//以防笔迹未刷新，再调用一次全局刷新
			((View) drawView.getParent()).invalidate();

			pointX = 0;
			pointY = 0;
			gPIndex = -1;
		}
		//TODO new draw
		//newDrawPenView.invalidate();
	}


	private static void drawInit() {
		drawView.initDraw();
		drawView.setVcolor(Color.WHITE);
		drawView.setVwidth(1);
		setPenColor(gColor);
		drawView.paint.setStrokeCap(Paint.Cap.ROUND);
		drawView.paint.setStyle(Paint.Style.FILL);
		drawView.paint.setAntiAlias(true);
		drawView.invalidate();
	}

	public static void drawExistingStroke(int bookID, int pageID) {
		if (bookID == 100) {
			dot_number4 = dot_number;
		} else if (bookID == 0) {
			dot_number4 = dot_number1;
		} else if (bookID == 1) {
			dot_number4 = dot_number2;
		}

		if (dot_number4.isEmpty()) {
			return;
		}

		Set<Integer> keys = dot_number4.keySet();
		for (int key : keys) {
			//Log.i(TAG, "=========pageID=======" + PageID + "=====Key=====" + key);
			if (key == pageID) {
				List<Dots> dots = dot_number4.get(key);
				for (Dots dot : dots) {
					//Log.i(TAG, "=========pageID=======" + dot.pointX + "====" + dot.pointY + "===" + dot.ntype);
					//笔锋绘制方法
					setPenColor(dot.ncolor);
					if (dot.ntype == 0) {
						gPIndex = 0;
						drawSubFountainPen3(drawView, gScale, gOffsetX, gOffsetY, dot.penWidth, dot.pointX, dot.pointY, dot.force, 0);
					} else if (dot.ntype == 1) {
						gPIndex += 1;
						drawSubFountainPen3(drawView, gScale, gOffsetX, gOffsetY, dot.penWidth, dot.pointX, dot.pointY, dot.force, 1);
					} else if (dot.ntype == 2) {
						gPIndex += 1;
						drawSubFountainPen3(drawView, gScale, gOffsetX, gOffsetY, dot.penWidth, dot.pointX, dot.pointY, dot.force, 2);
						gPIndex = 0;
					}
				}
			}
		}

		drawView.postInvalidate();
		gPIndex = -1;
	}

	private static void setPenColor(int ColorIndex) {
		switch (ColorIndex) {
			case 0:
				drawView.paint.setColor(Color.GRAY);
				return;
			case 1:
				drawView.paint.setColor(Color.RED);
				return;
			case 2:
				drawView.paint.setColor(Color.rgb(192, 192, 0));
				return;
			case 3:
				drawView.paint.setColor(Color.rgb(0, 128, 0));
				return;
			case 4:
				drawView.paint.setColor(Color.rgb(0, 0, 192));
				return;
			case 5:
				drawView.paint.setColor(Color.BLUE);
				return;
			case 6:
				drawView.paint.setColor(Color.BLACK);
				return;
			case 7:
				drawView.paint.setColor(Color.MAGENTA);
				return;
			case 8:
				drawView.paint.setColor(Color.CYAN);
				return;
		}
		return;
	}

	private static void drawSubFountainPen1(DrawView DV, float scale, float offsetX, float offsetY, int penWidth, float x, float y, int force, int ntype, int color) {
		if (ntype == 0) {
			g_x0 = x;
			g_y0 = y;
			g_x1 = x;
			g_y1 = y;
			//Log.i(TAG, "--------draw pen down-------");
		}

		if (ntype == 2) {
			g_x1 = x;
			g_y1 = y;
			Log.i("TEST", "--------draw pen up--------");
			//return;
		} else {
			g_x1 = x;
			g_y1 = y;
			//Log.i(TAG, "--------draw pen move-------");
		}

		DV.paint.setStrokeWidth(penWidth);
		setPenColor(color);
		DV.canvas.drawLine(g_x0, g_y0, g_x1, g_y1, DV.paint);
		g_x0 = g_x1;
		g_y0 = g_y1;

		return;
	}

	private static void drawSubFountainPen2(DrawView DV, float scale, float offsetX, float offsetY, int penWidth, float x, float y, int force, int ntype) {
		if (ntype == 0) {
			g_x0 = x;
			g_y0 = y;
			g_x1 = x;
			g_y1 = y;
			//Log.i(TAG, "--------draw pen down-------");
		}
		if (ntype == 2) {
			g_x1 = x;
			g_y1 = y;
			Log.i("TEST", "--------draw pen up--------");
		} else {
			g_x1 = x;
			g_y1 = y;
			//Log.i(TAG, "--------draw pen move-------");
		}

		DV.paint.setStrokeWidth(penWidth);
		DV.canvas.drawLine(g_x0, g_y0, g_x1, g_y1, DV.paint);

		g_x0 = g_x1;
		g_y0 = g_y1;

		return;
	}

	private static Path mDrawPath = new Path();

	private static void drawSubFountainPen3(DrawView DV, float scale, float offsetX, float offsetY, int penWidth, float x, float y, int force, int ntype) {
		DV.paint.setStrokeCap(Paint.Cap.ROUND);
		DV.paint.setStyle(Paint.Style.FILL);
		try {
			if (gPIndex == 0) {
				g_x0 = x * scale + offsetX + 0.1f;
				g_y0 = y * scale + offsetY;
				//g_p0 = Math.max(1, penWidth * 3 * force / 1023) * scale;
				g_p0 = getPenWidth(penWidth, force) * scale;
				DV.canvas.drawCircle((float) (g_x0), (float) (g_y0), (float) 0.5, DV.paint);
				return;
			}

			if (gPIndex == 1) {
				g_x1 = x * scale + offsetX + 0.1f;
				g_y1 = y * scale + offsetY;
				//g_p1 = Math.max(1, penWidth * 3 * force / 1023) * scale;
				g_p1 = getPenWidth(penWidth, force) * scale;

				g_vx01 = g_x1 - g_x0;
				g_vy01 = g_y1 - g_y0;
				// instead of dividing tangent/norm by two, we multiply norm by 2
				g_norm = (float) Math.sqrt(g_vx01 * g_vx01 + g_vy01 * g_vy01 + 0.0001f) * 2f;
				g_vx01 = g_vx01 / g_norm * g_p0;
				g_vy01 = g_vy01 / g_norm * g_p0;
				g_n_x0 = g_vy01;
				g_n_y0 = -g_vx01;

				return;
			}

			if (gPIndex > 1 && gPIndex < 10000) {
				// (x0,y0) and (x2,y2) are midpoints, (x1,y1) and (x3,y3) are actual
				g_x3 = x * scale + offsetX + 0.1f;
				g_y3 = y * scale + offsetY;
				//g_p3 = Math.max(1, penWidth * 3 * force / 1023) * scale;
				g_p3 = getPenWidth(penWidth, force) * scale;

				g_x2 = (g_x1 + g_x3) / 2f;
				g_y2 = (g_y1 + g_y3) / 2f;
				g_p2 = (g_p1 + g_p3) / 2f;
				g_vx21 = g_x1 - g_x2;
				g_vy21 = g_y1 - g_y2;
				g_norm = (float) Math.sqrt(g_vx21 * g_vx21 + g_vy21 * g_vy21 + 0.0001f) * 2f;

				g_vx21 = g_vx21 / g_norm * g_p2;
				g_vy21 = g_vy21 / g_norm * g_p2;
				g_n_x2 = -g_vy21;
				g_n_y2 = g_vx21;


				mDrawPath.rewind();
				mDrawPath.moveTo(g_x0 + g_n_x0, g_y0 + g_n_y0);
				// The + boundary of the stroke
				mDrawPath.cubicTo(g_x1 + g_n_x0, g_y1 + g_n_y0, g_x1 + g_n_x2, g_y1 + g_n_y2, g_x2 + g_n_x2, g_y2 + g_n_y2);
				// round out the cap
				mDrawPath.cubicTo(g_x2 + g_n_x2 - g_vx21, g_y2 + g_n_y2 - g_vy21, g_x2 - g_n_x2 - g_vx21, g_y2 - g_n_y2 - g_vy21, g_x2 - g_n_x2, g_y2 - g_n_y2);
				// THe - boundary of the stroke
				mDrawPath.cubicTo(g_x1 - g_n_x2, g_y1 - g_n_y2, g_x1 - g_n_x0, g_y1 - g_n_y0, g_x0 - g_n_x0, g_y0 - g_n_y0);
				// round out the other cap
				mDrawPath.cubicTo(g_x0 - g_n_x0 - g_vx01, g_y0 - g_n_y0 - g_vy01, g_x0 + g_n_x0 - g_vx01, g_y0 + g_n_y0 - g_vy01, g_x0 + g_n_x0, g_y0 + g_n_y0);
				DV.canvas.drawPath(mDrawPath, DV.paint);

				if (ntype == 2) {
					DV.paint.setStrokeWidth(g_p3);
					DV.canvas.drawLine(g_x1, g_y1, g_x3, g_y3, DV.paint);
				}


				g_x0 = g_x2;
				g_y0 = g_y2;
				g_p0 = g_p2;
				g_x1 = g_x3;
				g_y1 = g_y3;
				g_p1 = g_p3;
				g_vx01 = -g_vx21;
				g_vy01 = -g_vy21;
				g_n_x0 = g_n_x2;
				g_n_y0 = g_n_y2;
				return;
			}
			if (gPIndex >= 10000) {//Last Point
				g_x2 = x * scale + offsetX + 0.1f;
				g_y2 = y * scale + offsetY;
				//g_p2 = Math.max(1, penWidth * 3 * force / 1023) * scale;
				g_p2 = getPenWidth(penWidth, force) * scale;

				g_vx21 = g_x1 - g_x2;
				g_vy21 = g_y1 - g_y2;
				g_norm = (float) Math.sqrt(g_vx21 * g_vx21 + g_vy21 * g_vy21 + 0.0001f) * 2f;
				g_vx21 = g_vx21 / g_norm * g_p2;
				g_vy21 = g_vy21 / g_norm * g_p2;
				g_n_x2 = -g_vy21;
				g_n_y2 = g_vx21;

				mDrawPath.rewind();
				mDrawPath.moveTo(g_x0 + g_n_x0, g_y0 + g_n_y0);
				mDrawPath.cubicTo(g_x1 + g_n_x0, g_y1 + g_n_y0, g_x1 + g_n_x2, g_y1 + g_n_y2, g_x2 + g_n_x2, g_y2 + g_n_y2);
				mDrawPath.cubicTo(g_x2 + g_n_x2 - g_vx21, g_y2 + g_n_y2 - g_vy21, g_x2 - g_n_x2 - g_vx21, g_y2 - g_n_y2 - g_vy21, g_x2 - g_n_x2, g_y2 - g_n_y2);
				mDrawPath.cubicTo(g_x1 - g_n_x2, g_y1 - g_n_y2, g_x1 - g_n_x0, g_y1 - g_n_y0, g_x0 - g_n_x0, g_y0 - g_n_y0);
				mDrawPath.cubicTo(g_x0 - g_n_x0 - g_vx01, g_y0 - g_n_y0 - g_vy01, g_x0 + g_n_x0 - g_vx01, g_y0 + g_n_y0 - g_vy01, g_x0 + g_n_x0, g_y0 + g_n_y0);
				DV.canvas.drawPath(mDrawPath, DV.paint);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveData(Integer bookID, Integer pageID, float pointX, float pointY, int force, int ntype, int penWidth, int color, int counter, int angle) {

		Dots dot = new Dots(bookID, pageID, pointX, pointY, force, ntype, penWidth, color, counter, angle);

		try {
			if (bookID == 100) {
				dot_number.put(pageID, dot);
			} else if (bookID == 0) {
				dot_number1.put(pageID, dot);
			} else if (bookID == 1) {
				dot_number2.put(pageID, dot);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static float getPenWidth(int penWidth, int pointZ) {
		float mPenWidth = 1;
		if (penWidth == 1) {
			if (pointZ >= 0 && pointZ <= 50) {
				mPenWidth = (float) 0.8;
			}
			if (pointZ > 50 && pointZ <= 90) {
				mPenWidth = (float) 1.0;
			} else if (pointZ > 90 && pointZ <= 120) {
				mPenWidth = (float) 1.2;
			} else if (pointZ > 120 && pointZ <= 150) {
				mPenWidth = (float) 1.4;
			} else if (pointZ > 150 && pointZ <= 190) {
				mPenWidth = (float) 1.6;
			} else if (pointZ > 190 && pointZ <= 210) {
				mPenWidth = (float) 1.8;
			} else if (pointZ > 210 && pointZ <= 330) {
				mPenWidth = (float) 1.9;
			} else if (pointZ > 330 && pointZ <= 500) {
				mPenWidth = (float) 2.0;
			} else if (pointZ > 500 && pointZ <= 650) {
				mPenWidth = (float) 2.1;
			} else if (pointZ > 650 && pointZ <= 800) {
				mPenWidth = (float) 2.2;
			} else if (pointZ > 800) {
				mPenWidth = (float) 2.4;
			}
		} else if (penWidth == 2) {
			if (pointZ >= 0 && pointZ <= 50) {
				mPenWidth = (float) 1.6;
			}
			if (pointZ > 50 && pointZ <= 90) {
				mPenWidth = (float) 2.0;
			} else if (pointZ > 90 && pointZ <= 120) {
				mPenWidth = (float) 2.4;
			} else if (pointZ > 120 && pointZ <= 150) {
				mPenWidth = (float) 2.8;
			} else if (pointZ > 150 && pointZ <= 190) {
				mPenWidth = (float) 3.2;
			} else if (pointZ > 190 && pointZ <= 210) {
				mPenWidth = (float) 3.6;
			} else if (pointZ > 210 && pointZ <= 330) {
				mPenWidth = (float) 3.8;
			} else if (pointZ > 330 && pointZ <= 500) {
				mPenWidth = (float) 4.0;
			} else if (pointZ > 500 && pointZ <= 650) {
				mPenWidth = (float) 4.2;
			} else if (pointZ > 650 && pointZ <= 800) {
				mPenWidth = (float) 4.4;
			} else if (pointZ > 800) {
				mPenWidth = (float) 4.8;
			}
		} else if (penWidth == 3) {
			if (pointZ >= 0 && pointZ <= 50) {
				mPenWidth = (float) 2.4;
			}
			if (pointZ > 50 && pointZ <= 90) {
				mPenWidth = (float) 3.0;
			} else if (pointZ > 90 && pointZ <= 120) {
				mPenWidth = (float) 3.6;
			} else if (pointZ > 120 && pointZ <= 150) {
				mPenWidth = (float) 4.2;
			} else if (pointZ > 150 && pointZ <= 190) {
				mPenWidth = (float) 4.8;
			} else if (pointZ > 190 && pointZ <= 210) {
				mPenWidth = (float) 5.4;
			} else if (pointZ > 210 && pointZ <= 330) {
				mPenWidth = (float) 5.7;
			} else if (pointZ > 330 && pointZ <= 500) {
				mPenWidth = (float) 6.0;
			} else if (pointZ > 500 && pointZ <= 650) {
				mPenWidth = (float) 6.3;
			} else if (pointZ > 650 && pointZ <= 800) {
				mPenWidth = (float) 6.6;
			} else if (pointZ > 800) {
				mPenWidth = (float) 7.2;
			}
		} else if (penWidth == 4) {
			if (pointZ >= 0 && pointZ <= 50) {
				mPenWidth = (float) 3.2;
			}
			if (pointZ > 50 && pointZ <= 90) {
				mPenWidth = (float) 4.0;
			} else if (pointZ > 90 && pointZ <= 120) {
				mPenWidth = (float) 4.8;
			} else if (pointZ > 120 && pointZ <= 150) {
				mPenWidth = (float) 5.6;
			} else if (pointZ > 150 && pointZ <= 190) {
				mPenWidth = (float) 6.4;
			} else if (pointZ > 190 && pointZ <= 210) {
				mPenWidth = (float) 7.2;
			} else if (pointZ > 210 && pointZ <= 330) {
				mPenWidth = (float) 7.6;
			} else if (pointZ > 330 && pointZ <= 500) {
				mPenWidth = (float) 8.0;
			} else if (pointZ > 500 && pointZ <= 650) {
				mPenWidth = (float) 8.4;
			} else if (pointZ > 650 && pointZ <= 800) {
				mPenWidth = (float) 8.8;
			} else if (pointZ > 800) {
				mPenWidth = (float) 9.6;
			}
		} else if (penWidth == 5) {
			if (pointZ >= 0 && pointZ <= 50) {
				mPenWidth = (float) 4.0;
			}
			if (pointZ > 50 && pointZ <= 90) {
				mPenWidth = (float) 5.0;
			} else if (pointZ > 90 && pointZ <= 120) {
				mPenWidth = (float) 6.0;
			} else if (pointZ > 120 && pointZ <= 150) {
				mPenWidth = (float) 7.0;
			} else if (pointZ > 150 && pointZ <= 190) {
				mPenWidth = (float) 8.0;
			} else if (pointZ > 190 && pointZ <= 210) {
				mPenWidth = (float) 9.0;
			} else if (pointZ > 210 && pointZ <= 330) {
				mPenWidth = (float) 9.5;
			} else if (pointZ > 330 && pointZ <= 500) {
				mPenWidth = (float) 10.0;
			} else if (pointZ > 500 && pointZ <= 650) {
				mPenWidth = (float) 10.5;
			} else if (pointZ > 650 && pointZ <= 800) {
				mPenWidth = (float) 11.0;
			} else if (pointZ > 800) {
				mPenWidth = (float) 12.0;
			}
		} else if (penWidth == 6) {
			if (pointZ >= 0 && pointZ <= 50) {
				mPenWidth = (float) 4.8;
			}
			if (pointZ > 50 && pointZ <= 90) {
				mPenWidth = (float) 6.0;
			} else if (pointZ > 90 && pointZ <= 120) {
				mPenWidth = (float) 7.2;
			} else if (pointZ > 120 && pointZ <= 150) {
				mPenWidth = (float) 8.4;
			} else if (pointZ > 150 && pointZ <= 190) {
				mPenWidth = (float) 9.6;
			} else if (pointZ > 190 && pointZ <= 210) {
				mPenWidth = (float) 10.8;
			} else if (pointZ > 210 && pointZ <= 330) {
				mPenWidth = (float) 11.4;
			} else if (pointZ > 330 && pointZ <= 500) {
				mPenWidth = (float) 12.0;
			} else if (pointZ > 500 && pointZ <= 650) {
				mPenWidth = (float) 12.6;
			} else if (pointZ > 650 && pointZ <= 800) {
				mPenWidth = (float) 13.2;
			} else if (pointZ > 800) {
				mPenWidth = (float) 14.4;
			}
		} else {
			if (pointZ >= 0 && pointZ <= 50) {
				mPenWidth = (float) 3.2;
			}
			if (pointZ > 50 && pointZ <= 90) {
				mPenWidth = (float) 4.0;
			} else if (pointZ > 90 && pointZ <= 120) {
				mPenWidth = (float) 4.8;
			} else if (pointZ > 120 && pointZ <= 150) {
				mPenWidth = (float) 5.6;
			} else if (pointZ > 150 && pointZ <= 190) {
				mPenWidth = (float) 6.4;
			} else if (pointZ > 190 && pointZ <= 210) {
				mPenWidth = (float) 7.2;
			} else if (pointZ > 210 && pointZ <= 330) {
				mPenWidth = (float) 7.6;
			} else if (pointZ > 330 && pointZ <= 500) {
				mPenWidth = (float) 8.0;
			} else if (pointZ > 500 && pointZ <= 650) {
				mPenWidth = (float) 8.4;
			} else if (pointZ > 650 && pointZ <= 800) {
				mPenWidth = (float) 8.8;
			} else if (pointZ > 800) {
				mPenWidth = (float) 9.6;
			}
		}
		return mPenWidth;
	}


}
