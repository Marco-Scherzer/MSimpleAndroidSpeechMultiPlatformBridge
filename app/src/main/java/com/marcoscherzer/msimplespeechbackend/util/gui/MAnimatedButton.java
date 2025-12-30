package com.marcoscherzer.msimplespeechbackend.util.gui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatButton;
/**author Marco Scherzer, Copyright Marco Scherzer , All rights reserved
* fast scratched Animated-Button Class, unready (currently to much in one, works but unsafe to use)
* */
public class MAnimatedButton extends AppCompatButton {

    // Animationsmodus-Konstanten
    public static final int ANIMATION_NONE             = 0;
    public static final int ANIMATION_SPINNER          = 1;
    public static final int ANIMATION_FADE             = 2;
    public static final int ANIMATION_TEXT             = 3;
    public static final int ANIMATION_FADE_AND_SPINNER = 4;
    public static final int ANIMATION_FADE_AND_TEXT    = 5;
    public static final int ANIMATION_TEXT_FADE        = 6; // Hintergrund-Fade + Text-Fade/Wechsel

    //  Laufrichtungen für Textlauf
    public static final int TEXT_DIRECTION_LEFT  = 0; // nach links
    public static final int TEXT_DIRECTION_RIGHT = 1; // nach rechts

    private int animationMode = ANIMATION_FADE_AND_SPINNER;

    // Textlauf (Marquee)
    private int textDirection = TEXT_DIRECTION_LEFT;
    private float textStartOffsetPx = 0f;
    private ValueAnimator textAnimator;
    private float textOffset = 0f;
    private String animatedText = "text";

    // Text-Fade/Wechsel
    private ValueAnimator textFadeAnimator;
    private String[] textFadeTexts = null;
    private long[] textFadeDurationsMs = null;
    private int textFadeIndex = 0;
    private int textFadeAlpha = 0;
    private static final float TEXT_FADE_IN_FRACTION = 0.2f;  // 20%
    private static final float TEXT_FADE_OUT_FRACTION = 0.2f; // 20%

    // Spinner
    private GradientDrawable backgroundDrawable;
    private ValueAnimator colorFade;
    private ValueAnimator spinnerAnimator;
    private float spinnerAngle = 0f;
    private boolean showSpinner = false;

    private Paint spinnerPaint;
    private long animationStartTime = 0L;
    private static final long MIN_ANIM_TIME_MS = 3000;

    // Spinner-Parameter
    private int spinnerColor = Color.GREEN;
    private float spinnerRadiusDp = 8f;
    private float spinnerDotSizeDp = 2f;
    private int spinnerDotCount = 12;
    private long spinnerSpeedMs = 2000;

    // Fade-Parameter
    private long fadeSpeedMs = 1500;
    private int fadeStartColor = Color.rgb(27,27,27);
    private int fadeEndColor = Color.parseColor("#3377FF");
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MAnimatedButton(Context context) { super(context); init(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MAnimatedButton(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MAnimatedButton(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private void init() {
        setAllCaps(false);
        setTextColor(Color.rgb(177,150,0));
        setTextSize(16);
        int pad = dp(12);
        setPadding(pad, pad, pad, pad);
        setClickable(true);
        setFocusable(true);

        backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(fadeStartColor);
        backgroundDrawable.setCornerRadius(dp(7));
        backgroundDrawable.setStroke(dp(1), Color.rgb(177,150,0));
        setBackground(backgroundDrawable);

        spinnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        spinnerPaint.setColor(spinnerColor);
        spinnerPaint.setStyle(Paint.Style.FILL);

        buildFadeAnimator();
        buildSpinnerAnimator();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private void buildFadeAnimator() {
        colorFade = ValueAnimator.ofArgb(fadeStartColor, fadeEndColor);
        colorFade.setDuration(fadeSpeedMs);
        colorFade.setRepeatMode(ValueAnimator.REVERSE);
        colorFade.setRepeatCount(ValueAnimator.INFINITE);
        colorFade.addUpdateListener(anim -> {
            backgroundDrawable.setColor((int) anim.getAnimatedValue());
            invalidate();
        });
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private void buildSpinnerAnimator() {
        spinnerAnimator = ValueAnimator.ofFloat(0f, 360f);
        spinnerAnimator.setDuration(spinnerSpeedMs);
        spinnerAnimator.setRepeatCount(ValueAnimator.INFINITE);
        spinnerAnimator.setInterpolator(new LinearInterpolator());
        spinnerAnimator.addUpdateListener(anim -> {
            spinnerAngle = (float) anim.getAnimatedValue();
            invalidate();
        });
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setButtonText(String text) {
        setText(text == null ? "" : text);
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void startClickAnimation(String newText) {
        if(newText !=null) setButtonText(newText);
        animationStartTime = System.currentTimeMillis();
        showSpinner = (animationMode == ANIMATION_SPINNER || animationMode == ANIMATION_FADE_AND_SPINNER);

        if ((animationMode == ANIMATION_SPINNER || animationMode == ANIMATION_FADE_AND_SPINNER) && !spinnerAnimator.isStarted()) {
            spinnerAnimator.start();
        }

        if ((animationMode == ANIMATION_FADE || animationMode == ANIMATION_FADE_AND_SPINNER
                || animationMode == ANIMATION_FADE_AND_TEXT || animationMode == ANIMATION_TEXT_FADE)
                && !colorFade.isStarted()) {
            colorFade.start();
        }

        if ((animationMode == ANIMATION_TEXT || animationMode == ANIMATION_FADE_AND_TEXT) && animatedText != null) {
            startTextAnimator();
        }

        if (animationMode == ANIMATION_TEXT_FADE && textFadeTexts != null && textFadeTexts.length > 0) {
            startTextFadeAnimator();
        }
    }
    /*author Marco Scherzer, Copyright Marco Scherzer , All rights reserved
    Textlauf (Marquee) */
    private void startTextAnimator() {
        if (textAnimator != null && textAnimator.isRunning()) {
            textAnimator.cancel();
        }
        float distance = getWidth() + dp(100) + Math.abs(textStartOffsetPx);
        float start = 0f;
        float end = (textDirection == TEXT_DIRECTION_LEFT) ? distance : -distance;

        textAnimator = ValueAnimator.ofFloat(start, end);
        textAnimator.setDuration(5000);
        textAnimator.setRepeatCount(ValueAnimator.INFINITE);
        textAnimator.setInterpolator(new LinearInterpolator());
        textAnimator.addUpdateListener(anim -> {
            textOffset = (float) anim.getAnimatedValue();
            invalidate();
        });
        textAnimator.start();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private void startTextFadeAnimator() {
        stopTextFadeAnimator();

        if (textFadeTexts == null || textFadeDurationsMs == null
                || textFadeTexts.length == 0 || textFadeTexts.length != textFadeDurationsMs.length) {
            return;
        }
        textFadeIndex = 0;
        textFadeAlpha = 0;
        startOneTextFadeCycle();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private void startOneTextFadeCycle() {
        long duration = Math.max(1L, textFadeDurationsMs[textFadeIndex]);
        textFadeAnimator = ValueAnimator.ofFloat(0f, 1f);
        textFadeAnimator.setDuration(duration);
        textFadeAnimator.setInterpolator(new LinearInterpolator());
        textFadeAnimator.addUpdateListener(a -> {
            float p = (float) a.getAnimatedValue(); // 0..1
            if (p < TEXT_FADE_IN_FRACTION) {
                float local = p / TEXT_FADE_IN_FRACTION;
                textFadeAlpha = (int) (255 * local);
            } else if (p > 1f - TEXT_FADE_OUT_FRACTION) {
                float local = (1f - p) / TEXT_FADE_OUT_FRACTION;
                textFadeAlpha = (int) (255 * Math.max(0f, local));
            } else {
                textFadeAlpha = 255;
            }
            invalidate();
        });
        textFadeAnimator.addListener(new SimpleAnimatorListener() {
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                textFadeIndex = (textFadeIndex + 1) % textFadeTexts.length;
                // Nächste Runde
                post(MAnimatedButton.this::startOneTextFadeCycle);
            }
        });
        textFadeAnimator.start();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private void stopTextFadeAnimator() {
        if (textFadeAnimator != null && textFadeAnimator.isRunning()) {
            textFadeAnimator.cancel();
        }
        textFadeAnimator = null;
        textFadeAlpha = 0;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void stopClickAnimation(String newText) {
        long elapsed = System.currentTimeMillis() - animationStartTime;
        if (elapsed < MIN_ANIM_TIME_MS) {
            postDelayed(() -> reallyStop(newText), MIN_ANIM_TIME_MS - elapsed);
        } else {
            reallyStop(newText);
        }
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private void reallyStop(String newText) {
        if (spinnerAnimator.isRunning()) spinnerAnimator.cancel();
        if (colorFade.isRunning()) colorFade.cancel();
        if (textAnimator != null && textAnimator.isRunning()) textAnimator.cancel();
        stopTextFadeAnimator();

        backgroundDrawable.setColor(fadeStartColor);
        showSpinner = false;
        textOffset = 0f;
        if(newText!=null)setButtonText(newText);
        invalidate();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Spinner
        if (showSpinner) {
            float cx = getWidth() - dp(18);
            float cy = getHeight() / 2f;
            float radius = dp(spinnerRadiusDp);
            int dotCount = spinnerDotCount;

            canvas.save();
            canvas.rotate(spinnerAngle, cx, cy);

            for (int i = 0; i < dotCount; i++) {
                double theta = 2 * Math.PI * i / dotCount;
                float dx = (float) (Math.cos(theta) * radius);
                float dy = (float) (Math.sin(theta) * radius);
                int alpha = (int) (255 * ((float) i / dotCount));
                spinnerPaint.setAlpha(alpha);
                canvas.drawCircle(cx + dx, cy + dy, dp(spinnerDotSizeDp), spinnerPaint);
            }
            canvas.restore();
            spinnerPaint.setAlpha(255);
        }
        /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
        // Lauftext (Marquee)
        if ((animationMode == ANIMATION_TEXT || animationMode == ANIMATION_FADE_AND_TEXT) && animatedText != null) {
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(getTextSize());
            float y = getHeight() / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f);

            float drawX = (textDirection == TEXT_DIRECTION_LEFT)
                    ? getWidth() - textOffset + textStartOffsetPx
                    : -getWidth() - textOffset + textStartOffsetPx;

            canvas.drawText(animatedText, drawX, y, textPaint);
        }
        /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
        // Text-Fade/Wechsel
        if (animationMode == ANIMATION_TEXT_FADE && textFadeTexts != null && textFadeTexts.length > 0) {
            String txt = textFadeTexts[textFadeIndex];
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(getTextSize());
            textPaint.setAlpha(textFadeAlpha);

            float textWidth = textPaint.measureText(txt);
            float x = (getWidth() - textWidth) / 2f;
            float y = getHeight() / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f);

            canvas.drawText(txt, x, y, textPaint);
        }
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private int dp(float value) {
        return Math.round(value * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationSpinnerColor(int color) { this.spinnerColor = color; spinnerPaint.setColor(color); invalidate(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationSpinnerRadiusDp(float radiusDp) { this.spinnerRadiusDp = radiusDp; invalidate(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationSpinnerDotSizeDp(float dotSizeDp) { this.spinnerDotSizeDp = dotSizeDp; invalidate(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationSpinnerDotCount(int count) { this.spinnerDotCount = Math.max(1, count); invalidate(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationSpinnerSpeedMs(long durationMs) { this.spinnerSpeedMs = durationMs; if (spinnerAnimator != null) spinnerAnimator.setDuration(durationMs); }

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationFadeSpeedMs(long durationMs) { this.fadeSpeedMs = durationMs; if (colorFade != null) colorFade.setDuration(durationMs); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationFadeStartColor(int color) { this.fadeStartColor = color; rebuildFadeAnimator(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationFadeEndColor(int color) { this.fadeEndColor = color; rebuildFadeAnimator(); }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private void rebuildFadeAnimator() {
        boolean wasRunning = colorFade != null && colorFade.isRunning();
        if (colorFade != null) {
            colorFade.cancel();
        }
        buildFadeAnimator();
        if (wasRunning) {
            colorFade.start();
        }
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationText(String text) {
        this.animatedText = text;
        textOffset = 0f;
        invalidate();
    }
    /*author Marco Scherzer, Copyright Marco Scherzer , All rights reserved
     Startoffset für den Lauftext in Pixel setzen */
    public void setAnimationTextStartOffsetPx(float offsetPx) {
        this.textStartOffsetPx = offsetPx;
        this.textOffset = 0f;
        invalidate();
    }
    /*author Marco Scherzer, Copyright Marco Scherzer , All rights reserved
     Startoffset für den Lauftext in dp setzen */
    public void setAnimationTextStartOffsetDp(float offsetDp) {
        setAnimationTextStartOffsetPx(dp(offsetDp));
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public float getAnimationTextStartOffsetPx() {
        return textStartOffsetPx;
    }
    /*author Marco Scherzer, Copyright Marco Scherzer , All rights reserved
     Laufrichtung setzen: TEXT_DIRECTION_LEFT oder TEXT_DIRECTION_RIGHT */
    public void setAnimationTextDirection(int direction) {
        this.textDirection = (direction == TEXT_DIRECTION_RIGHT) ? TEXT_DIRECTION_RIGHT : TEXT_DIRECTION_LEFT;
        if (textAnimator != null && textAnimator.isRunning()) {
            textAnimator.cancel();
            startTextAnimator();
        }
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public int getAnimationTextDirection() {
        return textDirection;
    }

    /*author Marco Scherzer, Copyright Marco Scherzer , All rights reserved
     * Setzt die Texte und ihre Anzeigedauern (in Millisekunden, als Strings) für die Text-Fade-Animation.
     * Beide Arrays müssen gleich lang und nicht leer sein.
     */
    public void setAnimationTextFade(String[] texts, String[] timesMs) {
        if (texts == null || timesMs == null) {
            throw new IllegalArgumentException("texts und timesMs dürfen nicht null sein.");
        }
        if (texts.length == 0) {
            throw new IllegalArgumentException("texts darf nicht leer sein.");
        }
        if (texts.length != timesMs.length) {
            throw new IllegalArgumentException("texts und timesMs müssen die gleiche Länge haben.");
        }
        long[] durations = new long[timesMs.length];
        for (int i = 0; i < timesMs.length; i++) {
            String s = timesMs[i] == null ? "" : timesMs[i].trim();
            try {
                durations[i] = Long.parseLong(s);
                if (durations[i] <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Ungültige Dauer an Index " + i + ": '" + timesMs[i] + "'. Erwartet Millisekunden als positive Ganzzahl.");
            }
        }
        this.textFadeTexts = texts;
        this.textFadeDurationsMs = durations;
        this.textFadeIndex = 0;
        this.textFadeAlpha = 0;

        if (animationMode == ANIMATION_TEXT_FADE) {
            startTextFadeAnimator();
        }
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public void setAnimationMode(int mode) {
        this.animationMode = mode;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public int getAnimationMode() {
        return animationMode;
    }

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private static abstract class SimpleAnimatorListener implements android.animation.Animator.AnimatorListener {
        /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
        @Override public void onAnimationStart(android.animation.Animator animation) {}
        /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
        @Override public void onAnimationCancel(android.animation.Animator animation) {}
        /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
        @Override public void onAnimationRepeat(android.animation.Animator animation) {}
    }
}













