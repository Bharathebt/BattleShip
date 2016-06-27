package com.example.battleship;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

class CustomDrawable extends Drawable{
	
	private final Paint mPaint;
    private final RectF mRect;
    private final int color;

    public CustomDrawable(int color)
    {
        mPaint = new Paint();
        mRect = new RectF();
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas)
    {
        // Set the correct values in the Paint
        //mPaint.setARGB(255, 255, 0, 0);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Style.FILL);

        // Adjust the rect
        mRect.left = 9.0f;
        mRect.top = 8.0f;
        mRect.right = 72.0f;
        mRect.bottom = 74.0f;

        // Draw it
        canvas.drawRoundRect(mRect, 0.5f, 0.5f, mPaint);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setAlpha(int arg0)
    {
    }

    @Override
    public void setColorFilter(ColorFilter arg0)
    {
    }
}