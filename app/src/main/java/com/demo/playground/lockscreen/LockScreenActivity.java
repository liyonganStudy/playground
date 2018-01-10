package com.demo.playground.lockscreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.playground.R;

import java.util.List;

public class LockScreenActivity extends AppCompatActivity {

    private static final float BLACK_MAX_LIGHTNESS = 0.08f;
    private static final float WHITE_MIN_LIGHTNESS = 0.90f;
    private static final float POPULATION_FRACTION_FOR_WHITE_OR_BLACK = 2.5f;

    private int[] bgImageIds = new int[]{R.mipmap.pic1, R.mipmap.pic2, R.mipmap.pic3, R.mipmap.pic4, R.mipmap.pic5};
    private int currentIndex = 0;
    private ImageView bgMainColorView;
    private TextView lyricView;
    private float[] mFilteredBackgroundHsl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        lyricView = (TextView) findViewById(R.id.lyric);
        bgMainColorView = (ImageView) findViewById(R.id.bgImage);

        setBackgroundAndLryric();

        bgMainColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex + 1) % 5;
                setBackgroundAndLryric();
            }
        });
    }

    private void setBackgroundAndLryric() {
        int bgImageId = bgImageIds[currentIndex];
        int bgColor = getBackgroundColor(bgImageId);
        bgMainColorView.setImageBitmap(colorize(getResources().getDrawable(bgImageId), bgColor, false));
        bgMainColorView.setBackgroundDrawable(new ColorDrawable(bgColor));
        lyricView.setTextColor(isStrictWhite(mFilteredBackgroundHsl) ? Color.BLACK : Color.WHITE);
    }


    public static void launch(Context context) {
        Intent intent = new Intent(context, LockScreenActivity.class);
        context.startActivity(intent);
    }

    public Bitmap colorize(Drawable drawable, int backgroundColor, boolean isRtl) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int size = Math.min(width, height);
        int widthInset = (width - size) / 2;
        int heightInset = (height - size) / 2;
        drawable = drawable.mutate();
        drawable.setBounds(- widthInset, - heightInset, width - widthInset, height - heightInset);
        Bitmap newBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);

        // Values to calculate the luminance of a color
        float lr = 0.2126f;
        float lg = 0.7152f;
        float lb = 0.0722f;

        // Extract the red, green, blue components of the color extraction color in
        // float and int form
        int tri = Color.red(backgroundColor);
        int tgi = Color.green(backgroundColor);
        int tbi = Color.blue(backgroundColor);

        float tr = tri / 255f;
        float tg = tgi / 255f;
        float tb = tbi / 255f;

        // Calculate the luminance of the color extraction color
        float cLum = (tr * lr + tg * lg + tb * lb) * 255;

        ColorMatrix m = new ColorMatrix(new float[] {
                lr, lg, lb, 0, tri - cLum,
                lr, lg, lb, 0, tgi - cLum,
                lr, lg, lb, 0, tbi - cLum,
                0, 0, 0, 1, 0,
        });

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient linearGradient =  new LinearGradient(0, size, 0, 0,
                new int[] {0, Color.argb(127, 255, 255, 255), Color.BLACK},
                new float[] {0.0f, 0.4f, 1.0f}, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        Bitmap fadeIn = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas fadeInCanvas = new Canvas(fadeIn);
        drawable.clearColorFilter();
        drawable.draw(fadeInCanvas);

//        if (isRtl) {
//            // Let's flip the gradient
//            fadeInCanvas.translate(size, 0);
//            fadeInCanvas.scale(-1, 1);
//        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        fadeInCanvas.drawPaint(paint);

        Paint coloredPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coloredPaint.setColorFilter(new ColorMatrixColorFilter(m));
        coloredPaint.setAlpha((int) (0.5f * 255));
        canvas.drawBitmap(fadeIn, 0, 0, coloredPaint);

        linearGradient =  new LinearGradient(0, size, 0, 0,
                new int[] {0, Color.argb(127, 255, 255, 255), Color.BLACK},
                new float[] {0.0f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        fadeInCanvas.drawPaint(paint);
        canvas.drawBitmap(fadeIn, 0, 0, null);

        return newBitmap;
    }

    private int getBackgroundColor(int resourceId) {
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(resourceId);
        Bitmap bitmap = drawable.getBitmap();
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Palette.Builder paletteBuilder = Palette.from(bitmap)
                .setRegion(0, bitmapHeight / 3 * 2, bitmapWidth, bitmap.getHeight())
                .clearFilters() // we want all colors, red / white / black ones too!
                .resizeBitmapArea(bitmapHeight / 3 * bitmapWidth);
        Palette palette = paletteBuilder.generate();
        return findBackgroundColorAndFilter(palette);
    }

    private int findBackgroundColorAndFilter(Palette palette) {
        // by default we use the dominant palette
        Palette.Swatch dominantSwatch = palette.getDominantSwatch();
        if (dominantSwatch == null) {
            // We're not filtering on white or black
            mFilteredBackgroundHsl = null;
            return Color.WHITE;
        }

        if (!isWhiteOrBlack(dominantSwatch.getHsl())) {
            mFilteredBackgroundHsl = dominantSwatch.getHsl();
            return dominantSwatch.getRgb();
        }
        // Oh well, we selected black or white. Lets look at the second color!
        List<Palette.Swatch> swatches = palette.getSwatches();
        float highestNonWhitePopulation = -1;
        Palette.Swatch second = null;
        for (Palette.Swatch swatch : swatches) {
            if (swatch != dominantSwatch
                    && swatch.getPopulation() > highestNonWhitePopulation
                    && !isWhiteOrBlack(swatch.getHsl())) {
                second = swatch;
                highestNonWhitePopulation = swatch.getPopulation();
            }
        }
        if (second == null) {
            // We're not filtering on white or black
            mFilteredBackgroundHsl = null;
            return dominantSwatch.getRgb();
        }
        if (dominantSwatch.getPopulation() / highestNonWhitePopulation > POPULATION_FRACTION_FOR_WHITE_OR_BLACK) {
            // The dominant swatch is very dominant, lets take it!
            // We're not filtering on white or black
            mFilteredBackgroundHsl = null;
            return dominantSwatch.getRgb();
        } else {
            mFilteredBackgroundHsl = second.getHsl();
            return second.getRgb();
        }
    }

    private boolean isWhiteOrBlack(float[] hsl) {
        return isBlack(hsl) || isWhite(hsl);
    }

    /**
     * @return true if the color represents a color which is close to black.
     */
    private boolean isBlack(float[] hslColor) {
        return hslColor[2] <= BLACK_MAX_LIGHTNESS;
    }

    /**
     * @return true if the color represents a color which is close to white.
     */
    private boolean isWhite(float[] hslColor) {
        return hslColor[2] >= WHITE_MIN_LIGHTNESS;
    }

    private boolean isStrictWhite(float[] hslColor) {
        return hslColor[2] >= 0.8f;
    }
}
