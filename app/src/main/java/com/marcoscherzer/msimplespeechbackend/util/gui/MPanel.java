package com.marcoscherzer.msimplespeechbackend.util.gui;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
class MPanel {

    LinearLayout layout;       // Root-Layout
    LinearLayout contentArea;
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MPanel(Context context, String title) {
        // Farben
        final int gold = Color.rgb(177, 150, 0);
        final int bg = Color.rgb(27, 27, 27);
        final int divider = Color.rgb(80, 80, 80);

        // Maße
        final int circle = dp(context, 37);
        final int lineH = dp(context, 1);
        final int strokePx = 4;

        // Rahmen
        GradientDrawable border = new GradientDrawable();
        border.setStroke(strokePx, gold);
        border.setCornerRadius(12);
        border.setColor(bg);

        // Root-Layout
        layout = new LinearLayout(context);
        layout.setBackground(border);
        layout.setOrientation(LinearLayout.VERTICAL);

        // ===== Titelleiste =====
        LinearLayout titleBarContainer = new LinearLayout(context);
        titleBarContainer.setOrientation(LinearLayout.VERTICAL);

        LinearLayout titleBar = new LinearLayout(context);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setPadding(dp(context, 7), dp(context, 7), dp(context, 7), dp(context, 2));

        // Links: Fensterbuttons
        LinearLayout leftContainer = new LinearLayout(context);
        leftContainer.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

        TextView minimizeBtn = createCircleSymbolButton(context, gold, "–");
        leftContainer.addView(minimizeBtn, new LinearLayout.LayoutParams(circle, circle));

        Space btnSpace = new Space(context);
        btnSpace.setLayoutParams(new LinearLayout.LayoutParams(dp(context, 3), ViewGroup.LayoutParams.MATCH_PARENT));
        leftContainer.addView(btnSpace);

        TextView maximizeBtn = createCircleSymbolButton(context, gold, "□");
        leftContainer.addView(maximizeBtn, new LinearLayout.LayoutParams(circle, circle));

        titleBar.addView(leftContainer, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // Mitte: Titel
        LinearLayout centerContainer = new LinearLayout(context);
        centerContainer.setGravity(Gravity.CENTER);

        TextView header = new TextView(context);
        header.setText(title);
        header.setTextColor(gold);
        header.setTextSize(18);
        header.setGravity(Gravity.CENTER);

        centerContainer.addView(header);
        titleBar.addView(centerContainer, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // Rechts: Menü (4 Linien)
        LinearLayout rightContainer = new LinearLayout(context);
        rightContainer.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        View menuIcon = createMenuIcon(context, gold, 24, 2, 5);
        LinearLayout.LayoutParams menuParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        menuParams.width = dp(context, 48);
        menuParams.height = circle;
        menuIcon.setLayoutParams(menuParams);

        rightContainer.addView(menuIcon);
        titleBar.addView(rightContainer, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // Untere Trennlinie
        View fullWidthLine = new View(context);
        fullWidthLine.setBackgroundColor(divider);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, lineH);
        lineParams.topMargin = dp(context, 2);
        lineParams.leftMargin = strokePx;
        lineParams.rightMargin = strokePx;
        fullWidthLine.setLayoutParams(lineParams);

        titleBarContainer.addView(titleBar);
        titleBarContainer.addView(fullWidthLine);

        layout.addView(titleBarContainer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ===== Content-Bereich (leer, von außen befüllbar) =====
        contentArea = new LinearLayout(context);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        contentArea.setPadding(dp(context, 7), dp(context, 7), dp(context, 7), dp(context, 7));

        layout.addView(contentArea, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setContent(View view) {
        contentArea.removeAllViews();
        contentArea.addView(view);
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private TextView createCircleSymbolButton(Context context, int color, String symbol) {
        TextView btn = new TextView(context);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.TRANSPARENT);
        shape.setStroke(2, color);
        btn.setBackground(shape);
        btn.setText(symbol);
        btn.setTextColor(color);
        btn.setGravity(Gravity.CENTER);
        btn.setTextSize(16);
        return btn;
    }
    /*author Marco Scherzer, Copyright Marco Scherzer , All rights reserved
          Menü-Icon mit 4 Linien
     */
    private View createMenuIcon(Context context, int color, int lengthDp, int strokeDp, int gapDp) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);

        int lengthPx = dp(context, lengthDp);
        int strokePx = dp(context, strokeDp);
        int gapPx = dp(context, gapDp);

        for (int i = 0; i < 4; i++) {
            View line = new View(context);
            line.setBackgroundColor(color);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(lengthPx, strokePx);
            if (i > 0) params.topMargin = gapPx;
            line.setLayoutParams(params);
            container.addView(line);
        }
        return container;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private int dp(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}

