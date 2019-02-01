package com.demo.playground.bubble;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by liyongan on 18/8/12.
 */

public class BubbleWorlds {
    private List<BubbleInfo> mBubbles;
    private RectF mBounds;

    public BubbleWorlds() {
        initBubbles();
    }

    private void initBubbles() {
        mBubbles = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            mBubbles.add(BubbleInfo.getInstance(random, i));
        }
    }

    public void setBounds(float left, float top, float right, float bottom) {
        if (mBounds == null) {
            mBounds = new RectF();
        }
        mBounds.set(left, top, right, bottom);
    }

    public void renderBubbles(Canvas canvas, Paint paint) {
        for (BubbleInfo bubbleInfo : mBubbles) {
            bubbleInfo.render(canvas, paint);
        }
    }

    public void updateBubbles() {
        int count = mBubbles.size();
        for (int i = 0; i < count - 1; i++) {
            BubbleInfo currentBubble = mBubbles.get(i);
            for (int j = i + 1; j < count; j++) {
                bubbleCollide(currentBubble, mBubbles.get(j));
            }
        }

        for (BubbleInfo bubbleInfo : mBubbles) {
            collideWithBound(bubbleInfo);

            bubbleInfo.setOldSpeedX(bubbleInfo.getSpeedX());
            bubbleInfo.setOldSpeedY(bubbleInfo.getSpeedY());

            bubbleInfo.update();
        }
    }

    private void collideWithBound(BubbleInfo currentBubble) {
        if (mBounds == null) {
            return;
        }
        if ((currentBubble.getCenterX() < mBounds.left && currentBubble.getOldSpeedX() < 0)
                || (currentBubble.getCenterX() > mBounds.right && currentBubble.getOldSpeedX() > 0)) {
            currentBubble.setSpeedX(-currentBubble.getOldSpeedX());
            currentBubble.setOldSpeedX(currentBubble.getSpeedX());
        }
        if (currentBubble.getCenterY() < mBounds.top && currentBubble.getOldSpeedY() < 0
                || currentBubble.getCenterY() > mBounds.bottom && currentBubble.getOldSpeedY() > 0) {
            currentBubble.setSpeedY(-currentBubble.getOldSpeedY());
            currentBubble.setOldSpeedY(currentBubble.getSpeedY());
        }
    }

    private void bubbleCollide(BubbleInfo currentBubble, BubbleInfo otherBubble) {
        if (currentBubble.getIndex() == otherBubble.getIndex()) {
            return;
        }
        if (isBubbleOverlap(currentBubble, otherBubble)) {
            if (((currentBubble.getCenterX() - otherBubble.getCenterX() < 0) && (currentBubble.getOldSpeedX() - otherBubble.getOldSpeedX() > 0))
                    || ((currentBubble.getCenterX() - otherBubble.getCenterX() > 0) && (currentBubble.getOldSpeedX() - otherBubble.getOldSpeedX() < 0))) {
                currentBubble.setSpeedX(otherBubble.getOldSpeedX());
                otherBubble.setSpeedX(currentBubble.getOldSpeedX());
                currentBubble.setOldSpeedX(currentBubble.getSpeedX());
                otherBubble.setOldSpeedX(otherBubble.getSpeedX());
            }
            if (((currentBubble.getCenterY() - otherBubble.getCenterY() < 0) && (currentBubble.getOldSpeedY() - otherBubble.getOldSpeedY() > 0)) ||
                    ((currentBubble.getCenterY() - otherBubble.getCenterY() > 0) && (currentBubble.getOldSpeedY() - otherBubble.getOldSpeedY() < 0))) {
                currentBubble.setSpeedY(otherBubble.getOldSpeedY());
                otherBubble.setSpeedY(currentBubble.getOldSpeedY());
                currentBubble.setOldSpeedY(currentBubble.getSpeedY());
                otherBubble.setOldSpeedY(otherBubble.getSpeedY());
            }
        }
    }

    private boolean isBubbleOverlap(BubbleInfo bubble1, BubbleInfo bubble2) {
        return Math.pow(bubble1.getRadius() + bubble2.getRadius(), 2) >= Math.pow(bubble1.getCenterX() - bubble2.getCenterX(), 2) + Math.pow(bubble1.getCenterY() - bubble2.getCenterY(), 2);
    }

    public static class BubbleInfo {
        private int type, index;
        private float centerX, centerY, radius, speedX, speedY, oldSpeedX, oldSpeedY;

        static BubbleInfo getInstance(Random random, int index) {
            BubbleInfo bubbleInfo = new BubbleInfo();
            bubbleInfo.setCenterX(random.nextInt(1000));
            bubbleInfo.setCenterY(300 + random.nextInt(1500));
            bubbleInfo.setRadius(100);
            bubbleInfo.setSpeedX(random.nextFloat() * 2 * (random.nextBoolean() ? 1 : -1));
            bubbleInfo.setSpeedY(random.nextFloat() * 2 * (random.nextBoolean() ? 1 : -1));
            bubbleInfo.setIndex(index);
            bubbleInfo.setOldSpeedX(bubbleInfo.getSpeedX());
            bubbleInfo.setOldSpeedY(bubbleInfo.getSpeedY());
            return bubbleInfo;
        }

        int getIndex() {
            return index;
        }

        void setIndex(int index) {
            this.index = index;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        float getCenterX() {
            return centerX;
        }

        void setCenterX(float centerX) {
            this.centerX = centerX;
        }

        float getCenterY() {
            return centerY;
        }

        void setCenterY(float centerY) {
            this.centerY = centerY;
        }

        float getRadius() {
            return radius;
        }

        void setRadius(float radius) {
            this.radius = radius;
        }

        float getSpeedX() {
            return speedX;
        }

        void setSpeedX(float speedX) {
            this.speedX = speedX;
        }

        float getSpeedY() {
            return speedY;
        }

        void setSpeedY(float speedY) {
            this.speedY = speedY;
        }

        float getOldSpeedX() {
            return oldSpeedX;
        }

        void setOldSpeedX(float oldSpeedX) {
            this.oldSpeedX = oldSpeedX;
        }

        float getOldSpeedY() {
            return oldSpeedY;
        }

        void setOldSpeedY(float oldSpeedY) {
            this.oldSpeedY = oldSpeedY;
        }

        void render(Canvas canvas, Paint paint) {
            canvas.drawCircle(centerX, centerY, radius, paint);
        }

        void update() {
            centerX += speedX;
            centerY += speedY;
        }
    }
}
