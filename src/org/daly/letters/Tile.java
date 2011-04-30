package org.daly.letters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;
import android.view.View.MeasureSpec;
import java.lang.Math;

/*
  A tile has named points of interest:

      A  Q-G---------------------------------U--P--K
      |   /                                      / |
      B--E-----------------------------------V--H  R
      | /|                                      |  |
      C  |                                      T  S
      |  |                                      |  |
      |  |                                      |  |
      |  |                                      |  |
      |  |                                      |  |
      |  |                                      |  |
      |  |                                      |  |
      |  |                                      |  |
      |  |                                      |  L
      |  F--------------------------------------I/ M  
      |/                                       /   |
      D---------------------------------------J----N

*/

public class Tile extends View {
  private int Ax,Bx,Cx,Dx,Ex,Fx,Gx,Hx,Ix,Jx,Kx,Lx,Mx,Nx,Px,Qx,Rx,Sx,Tx,Ux,Vx;
  private int Ay,By,Cy,Dy,Ey,Fy,Gy,Hy,Iy,Jy,Ky,Ly,My,Ny,Py,Qy,Ry,Sy,Ty,Uy,Vy;

  private final static int widthPad = 8;
  private final static int heightPad = 10;
  private final int tilewidth = 80;
  private final int tileheight = 80;
  private final int totalheight = 100;
  private final int totalwidth = 100;
  private int imageId;
  private Bitmap image;
  private Bitmap transtile = null;  
  private int imagewidth;
  private int imageheight;
  private Rect srcbox; 
  private Rect destbox;
  private BitmapDrawable drawtile = null;
  private int id = -1;
  private int index = -1;
  private double tabletheight;
  private double tabletwidth;
  private boolean chosen = false;
  private float scale = 1.0f;
  private Paint paint;
  private Matrix matrix;
  private Path mark = null;   // blue mark on tile when chosen
  private Path border = null; // shading for tile sides
  private  int doshadow;
  public static final int NEITHER = 0;

  public static final int TOP = 1;
  Path topshadow = null;      // shadow for upper tiles

  public static final int SIDE = 2;
  Path sideshadow = null;     // shadow for upper tiles

  public static final int BOTH = 3;

  public static final int TOPTILT = 4;
  Path toptiltshadow = null;  // shadow for upper tiles

  public static final int SIDETILT = 5;
  Path sidetiltshadow = null; // shadow for upper tiles

  public static final int CORNERTILT = 7;
  Path cornertopshadow = null;
  Path cornersideshadow = null;

  public static final int SHORTSIDE = 8;
  Path shortside = null;

  public static final int SHORTTOP = 9;
  Path shorttop = null;

  public static final int SHORTCORNER = 10;

  Path overcolor;
  int[] colorById = { // separate by 0xFFFFFF/0x24=0x71C70
    0x3F000000, 0x3F071C70, 0x3F0E38E0, 0x3F155550,
    0x3F1C71C0, 0x3F238E30, 0x3F2AAAA0, 0x3F31C710,
    0x3F38E380, 0x3F3FFFF0, 0x3F471C60, 0x3F4E38D0,
    0x3F555540, 0x3F5C71B0, 0x3F638E20, 0x3F6AAA90,
    0x3F71C700, 0x3F78E370, 0x3F7FFFE0, 0x3F871C50,
    0x3F8E38C0, 0x3F955530, 0x3F9C71A0, 0x3FA38E10,
    0x3FAAAA80, 0x3FB1C6F0, 0x3FB8E360, 0x3FBFFFD0,
    0x3FC71C40, 0x3FCE38B0, 0x3FD55520, 0x3FDC7190,
    0x3FE38E00, 0x3FEAAA70, 0x3FF1C6E0, 0x3FF8E350
  };
  private boolean coloron = true;

  public Tile (Context context, int img, int myid, float thescale,
               boolean color) {
    super(context);
    id = myid;
    imageId = img;
    scale = thescale;
    coloron = color;
    paint = new Paint();
    matrix = new Matrix();
    Ax = 0;                     Ay = 0;
    Bx = 0;                     By = Math.round(10*scale);
    Cx = 0;                     Cy = Math.round(20*scale);
    Dx = 0;                     Dy = Math.round(100*scale);
    Ex = Math.round(10*scale);  Ey = Math.round(10*scale);
    Fx = Math.round(10*scale);  Fy = Math.round(90*scale);
    Gx = Math.round(20*scale);  Gy = 0;
    Hx = Math.round(90*scale);  Hy = Math.round(10*scale);
    Ix = Math.round(90*scale);  Iy = Math.round(90*scale);
    Jx = Math.round(80*scale);  Jy = Math.round(100*scale);
    Kx = Math.round(100*scale); Ky = 0;
    Lx = Math.round(100*scale); Ly = Math.round(80*scale);
    Mx = Math.round(100*scale); My = Math.round(90*scale);
    Nx = Math.round(100*scale); Ny = Math.round(100*scale);
    Px = Math.round(90*scale);  Py = 0;
    Qx = Math.round(10*scale);  Qy = 0;
    Rx = Math.round(100*scale); Ry = Math.round(10*scale);
    Sx = Math.round(100*scale); Sy = Math.round(20*scale);
    Tx = Math.round(90*scale);  Ty = Math.round(20*scale);
    Ux = Math.round(80*scale);  Uy = 0;
    Vx = Math.round(80*scale);  Vy = Math.round(10*scale);
    image = BitmapFactory.decodeResource(context.getResources(),imageId);
    makeTransTile();
    setMarkPath();      
    setBorder();
    drawOvercolor();
    setShadow();
    setFocusable(true);
    setBackgroundColor(0x0000FF00);
    setClickable(true);
  }

  public int getId() {
    return id;
  }

  public void setIndex(int thisindex) {
    index = thisindex;
  }

  public int getIndex() {
    return index;
  }

  public void setShadow(int shadow) {
    doshadow = shadow;
  }

  private void setMarkPath() { // draw a small square to mark the choice
    if (mark == null) {
      mark = new Path();
      mark.moveTo(20,20);
      mark.lineTo(20,30);
      mark.lineTo(30,30);
      mark.lineTo(30,20);
      mark.lineTo(20,20);
      mark.close();
    }
  }

  private void setBorder() {
    if (border == null) {
      border = new Path();
      border.moveTo(Cx,Cy);
      border.lineTo(Dx,Dy);
      border.lineTo(Jx,Jy);
      border.lineTo(Ix,Iy);
      border.lineTo(Fx,Fy);
      border.lineTo(Ex,Ey);
      border.lineTo(Cx,Cy);
      border.close();
    }
  }

  public void drawOvercolor() {
    if (overcolor == null) {
      overcolor = new Path();
      overcolor.moveTo(Ex,Ey);
      overcolor.lineTo(Hx,Hy);
      overcolor.lineTo(Ix,Iy);
      overcolor.lineTo(Fx,Fy);
      overcolor.lineTo(Ex,Ey);
      overcolor.close();
    }
  }

  public void setShadow() {
    if (topshadow == null) {
      topshadow = new Path();
      topshadow.moveTo(Gx,Gy);
      topshadow.lineTo(Ex,Ey);
      topshadow.lineTo(Hx,Hy);
      topshadow.lineTo(Kx,Ky);
      topshadow.lineTo(Gx,Gy);
      topshadow.close();
      toptiltshadow = new Path();
      toptiltshadow.moveTo(Gx,Gy);
      toptiltshadow.lineTo(Ex,Ey);
      toptiltshadow.lineTo(Hx,Hy);
      toptiltshadow.lineTo(Px,Py);
      toptiltshadow.lineTo(Gx,Gy);
      toptiltshadow.close();
      sideshadow = new Path();
      sideshadow.moveTo(Hx,Hy);
      sideshadow.lineTo(Ix,Iy);
      sideshadow.lineTo(Lx,Ly);
      sideshadow.lineTo(Kx,Ky);
      sideshadow.lineTo(Hx,Hy);
      sideshadow.close();
      sidetiltshadow = new Path();
      sidetiltshadow.moveTo(Px,Py);
      sidetiltshadow.lineTo(Ix,Iy);
      sidetiltshadow.lineTo(Lx,Ly);
      sidetiltshadow.lineTo(Kx,Ky);
      sidetiltshadow.lineTo(Px,Py);
      sidetiltshadow.close();
      cornertopshadow = new Path();
      cornertopshadow.moveTo(Gx,Gy);
      cornertopshadow.lineTo(Ex,Ey);
      cornertopshadow.lineTo(Hx,Hy);
      cornertopshadow.lineTo(Px,Py);
      cornertopshadow.lineTo(Gx,Gy);
      cornertopshadow.close();
      cornersideshadow = new Path();
      cornersideshadow.moveTo(Hx,Hy);
      cornersideshadow.lineTo(Ix,Iy);
      cornersideshadow.lineTo(Lx,Ly);
      cornersideshadow.lineTo(Rx,Ry);
      cornersideshadow.lineTo(Hx,Hy);
      cornersideshadow.close();
      shortside = new Path();
      shortside.moveTo(Tx,Ty);
      shortside.lineTo(Ix,Iy);
      shortside.lineTo(Lx,Ly);
      shortside.lineTo(Sx,Sy);
      shortside.lineTo(Tx,Ty);
      shortside.close();
      shorttop = new Path();
      shorttop.moveTo(Ux,Uy);
      shorttop.lineTo(Gx,Gy);
      shorttop.lineTo(Ex,Ey);
      shorttop.lineTo(Vx,Vy);
      shorttop.lineTo(Ux,Uy);
      shorttop.close();
    }
  }

  private void makeTransTile() {
    if (transtile == null) {
      imagewidth = image.getWidth();
      imageheight = image.getHeight();
      float scalewidth = (float)(((double) tilewidth) / (double) imagewidth);
      float scaleheight= (float)(((double) tileheight) / (double) imageheight);
      matrix.postScale(scalewidth,scaleheight);
      transtile=
        Bitmap.createBitmap(image,0,0,imagewidth,imageheight,matrix,true);
      srcbox = new Rect(0,0,80,80);
      destbox = new Rect(Ex,Ey,Ix,Iy);
    }
  }


  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawARGB(0,0,255,0); // make it transparent green
    paint.setColor(0xFF00FF00); // draw the border of the image
    canvas.drawPath(border,paint);
    if (chosen == true) {
      canvas.drawBitmap(transtile, srcbox, destbox, paint);
      drawShadow(canvas);
      paint.setColor(0xFF0000FF); // mark the tile
      canvas.drawPath(mark,paint);
      if (coloron == true) {
        paint.setColor(colorById[id]);
        canvas.drawPath(overcolor,paint);
      }
    } else {
      canvas.drawBitmap(transtile, srcbox, destbox, paint);
      drawShadow(canvas);
      if (coloron == true) {
        paint.setColor(colorById[id]);
        canvas.drawPath(overcolor,paint);
      }
    }
  }

  public void drawShadow(Canvas canvas) {
    paint.setColor(0x70000000); // gray shadow the tile
    switch (doshadow) {
      case TOP:
        canvas.drawPath(topshadow,paint);
        break;
      case SIDE:
        canvas.drawPath(sideshadow,paint);
        break;
      case BOTH:
        canvas.drawPath(topshadow,paint);
        canvas.drawPath(sideshadow,paint);
        break;
      case TOPTILT:
        canvas.drawPath(toptiltshadow,paint);
        break;
      case SIDETILT:
        canvas.drawPath(sidetiltshadow,paint);
        break;
//      case BOTHTILT:
//        canvas.drawPath(toptiltshadow,paint);
//        canvas.drawPath(sidetiltshadow,paint);
//        break;
      case CORNERTILT:
        canvas.drawPath(cornertopshadow,paint);
        canvas.drawPath(cornersideshadow,paint);
        break;
      case SHORTSIDE:
        canvas.drawPath(shortside,paint);
        break;
      case SHORTTOP:
        canvas.drawPath(shorttop,paint);
        break;
      case SHORTCORNER:
        canvas.drawPath(shorttop,paint);
        canvas.drawPath(shortside,paint);
        break;
    }
    paint.setColor(0xFF000000);
    canvas.drawLine(Cx,Cy,Dx,Dy,paint); 
    canvas.drawLine(Dx,Dy,Fx,Fy,paint); 
    canvas.drawLine(Cx,Cy,Ex,Ey,paint); 
    canvas.drawLine(Ex,Ey,Fx,Fy,paint); 
    canvas.drawLine(Ex,Ey,Hx,Hy,paint); 
    canvas.drawLine(Fx,Fy,Ix,Iy,paint); 
    canvas.drawLine(Hx,Hy,Ix,Iy,paint); 
    canvas.drawLine(Ix,Iy,Jx,Jy,paint); 
    canvas.drawLine(Dx,Dy,Jx,Jy,paint); 
  }

  public void setColor(boolean color) {
    coloron = color;
  }

  public void markChosen() {
    chosen = true;
  }

  public void markUnchosen() {
    chosen = false;
  }

  @Override
  protected void onMeasure (int width, int height) {
    int imgwidth = boundBox(width,image.getWidth()*2);
    int imgheight = boundBox(height,image.getHeight()*2);
    setMeasuredDimension((int)(totalwidth*scale),(int)(totalheight*scale));
  }

  protected int boundBox(int required, int want) {
    int result = 0;
    switch(MeasureSpec.getMode(required)) {
      case MeasureSpec.EXACTLY:
        result = MeasureSpec.getSize(required);
        break;
      case MeasureSpec.AT_MOST:
        result = Math.min(MeasureSpec.getSize(required),want);
        break;
      default:
        result = want;
        break;
    }
    //    return(result);
    return(want);
  }

}
