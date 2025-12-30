package com.marcoscherzer.msimplespeechbackend.util.gui;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.drawable.GradientDrawable.OVAL;
import static android.widget.LinearLayout.VERTICAL;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public class MSomeSimpleButtonsUtil {
        /**
         * Kreis-Button mit kompensiertem Stroke:
         * - sichtbarer Durchmesser = visualDiameterPx
         * - strokePx ist Pixel (keine DP-Umrechnung!)
         */
        public static FrameLayout createCircleButton1(Context context, String symbol, int color, int strokePx, int visualDiameterPx) {
            FrameLayout container = new FrameLayout(context);

            // Tatsächlicher Durchmesser inkl. innenliegendem Stroke
            int diameterPx = visualDiameterPx + strokePx * 2;

            // Klickbare Fläche (volle Größe)
            Button btn = new Button(context);
            btn.setBackgroundColor(TRANSPARENT);
            btn.setText("");
            btn.setAllCaps(false);
            btn.setClickable(true);
            btn.setFocusable(true);
            FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(diameterPx, diameterPx, Gravity.CENTER);
            btn.setLayoutParams(btnParams);
            container.addView(btn);

            // Kreis als View-Hintergrund
            View circle = new View(context);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(OVAL);
            shape.setColor(TRANSPARENT);
            shape.setStroke(strokePx, color); // Stroke in Pixel
            circle.setBackground(shape);
            FrameLayout.LayoutParams circleParams = new FrameLayout.LayoutParams(diameterPx, diameterPx, Gravity.CENTER);
            circle.setLayoutParams(circleParams);
            container.addView(circle);

            // Symbol-Overlay (Textgröße in PX, proportional zum sichtbaren Durchmesser)
            TextView icon = new TextView(context);
            icon.setText(symbol);
            icon.setTextColor(color);
            icon.setGravity(Gravity.CENTER);
            icon.setTextSize(TypedValue.COMPLEX_UNIT_PX, visualDiameterPx * 0.7f);
            FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(diameterPx, diameterPx, Gravity.CENTER);
            icon.setLayoutParams(iconParams);
            container.addView(icon);

            return container;
        }

        /**
         * Menü-Button mit horizontalen Linien:
         * - Breite = lengthPx
         * - Höhe = menuLineCnt * strokePx + (menuLineCnt - 1) * gapPx
         * - Alle Maße sind Pixel
         */
        public static FrameLayout createMenuButton1(Context context, int menuLineCnt, int color, int strokePx, int lengthPx, int gapPx) {
            FrameLayout container = new FrameLayout(context);

            int lines = Math.max(0, menuLineCnt);
            int totalHeightPx = lines > 0 ? (lines * strokePx + (lines - 1) * gapPx) : strokePx;

            // Klickbare Fläche (exakt so groß wie das Icon)
            Button btn = new Button(context);
            btn.setBackgroundColor(TRANSPARENT);
            btn.setText("");
            btn.setAllCaps(false);
            btn.setClickable(true);
            btn.setFocusable(true);
            FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(lengthPx, totalHeightPx, Gravity.CENTER);
            btn.setLayoutParams(btnParams);
            container.addView(btn);

            // Icon-Linien
            LinearLayout iconLayout = new LinearLayout(context);
            iconLayout.setOrientation(VERTICAL);
            iconLayout.setGravity(Gravity.CENTER);

            for (int i = 0; i < lines; i++) {
                View line = new View(context);
                line.setBackgroundColor(color);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(lengthPx, strokePx);
                if (i > 0) params.topMargin = gapPx;
                line.setLayoutParams(params);
                iconLayout.addView(line);
            }

            FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(lengthPx, totalHeightPx, Gravity.CENTER);
            iconLayout.setLayoutParams(iconParams);
            container.addView(iconLayout);

            return container;
        }



    private static int dp(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
