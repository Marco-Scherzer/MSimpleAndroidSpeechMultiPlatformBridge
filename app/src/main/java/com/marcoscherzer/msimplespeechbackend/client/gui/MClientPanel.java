package com.marcoscherzer.msimplespeechbackend.client.gui;

import static com.marcoscherzer.mgridbuilder_androidversion.MBorderDrawableBuilder.BorderEdge.BOTTOM;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.marcoscherzer.mgridbuilder_androidversion.MBorderDrawableBuilder;
import com.marcoscherzer.mgridbuilder_androidversion.MGridBuilder;
import com.marcoscherzer.msimplespeechbackend.util.gui.MAnimatedButton;
import com.marcoscherzer.msimplespeechbackend.util.gui.MSomeSimpleButtonsUtil;


/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MClientPanel {

    View layout;
    TextView logArea;
    MAnimatedButton recordButton;
    MAnimatedButton connectButton;
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final TextView getLogArea(){
        return logArea;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final MAnimatedButton getRecordButton(){
        return recordButton;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final MAnimatedButton getConnectButton(){
        return connectButton;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MClientPanel(Context context) {
        final int gold    = Color.rgb(177, 150, 0);
        final int bg      = Color.rgb(27, 27, 27);
        final int divider = Color.rgb(80, 80, 80);
        final int strokePx= 4;

        // === Hauptgrid: eine Spalte, zwei Zeilen (Titel + Content) ===
        MGridBuilder root = new MGridBuilder(context)
                .setColumnWidths(1f);

        // Titelleiste: 12% der Höhe
        // nur unterer Rand
        MBorderDrawableBuilder titleStyle = new MBorderDrawableBuilder()
                .setStroke(gold, 1f, BOTTOM);
                //.setCornerRadius(12f);

        root.addLine(0.12f)
                .addWithPadding(createTitleBar(context, gold, divider),1,3,0,7,titleStyle);

        // Contentbereich: 88% der Höhe
        root.addLine(0.88f).add(createContentArea(context, bg));

        // Panel-Rahmen
        MBorderDrawableBuilder panelStyle = new MBorderDrawableBuilder()
                .setFillColor(bg)
                .setStroke(gold, 2)
                .setCornerRadius(7f);


        layout = root.create();
        layout.setBackground(panelStyle.create(context));
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private View createTitleBar(Context context, int gold, int divider) {
        MGridBuilder titleGrid = new MGridBuilder(context);
        titleGrid.setColumnWidths(0.13f, 0.74f,0.03f, 0.10f);
        MGridBuilder.GridLine line = titleGrid.addLine(1f);

        View b= MSomeSimpleButtonsUtil.createCircleButton1(context, "□",gold,1,62);
        //b.setBackgroundColor(Color.GREEN);
        line
                .add( MSomeSimpleButtonsUtil.createMenuButton1(context,5, gold, 1, 55, 10))
                .add(createTitleLabel(context, gold))
                .add(new View(context))
                .add(b);

        return titleGrid.create();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private View createContentArea(Context context, int bg) {
        connectButton = new MAnimatedButton(context);
        connectButton.setButtonText("2. Connect");
        // Zeile 5: Record (20%)
        recordButton = new MAnimatedButton(context);
        recordButton.setButtonText("Microphone");

        MGridBuilder content = new MGridBuilder(context);
        content.setColumnWidths(1.0f);
        content.addLine(0.58f).add(createTranscriptScroll(context, bg));
        content.addLine(0.01f).add(new View(context));
        content.addLine(0.2f).add(connectButton);
        content.addLine(0.01f).add(new View(context));
        content.addLine(0.2f).add(recordButton);

        View v = content.create();
        v.setPadding(7, 7, 7, 7);
        return v;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private TextView createTitleLabel(Context context, int color) {
        TextView t = new TextView(context);
        t.setText("Test Client");
        t.setTextColor(color);
        t.setTextSize(18);
        //t.setGravity(CENTER);
        return t;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private View createTranscriptScroll(Context context, int bg) {
        ScrollView scroll = new ScrollView(context);
        //scroll.setPadding(24, 24, 24, 24);
        logArea = new TextView(context);
        logArea.setHint("Recognition results...");
        logArea.setTextColor(Color.rgb(0, 160, 200));
        //logArea.setHintTextColor(Color.GRAY);
        logArea.setBackgroundColor(bg);
        logArea.setTextSize(14);
        //logArea.setSingleLine(false);
        //logArea.setMovementMethod(new ScrollingMovementMethod());
        //logArea.setBackgroundColor(Color.WHITE);
        scroll.addView(logArea);
        //scroll.setBackgroundColor(Color.GREEN);
        scroll.setHapticFeedbackEnabled(true);
        scroll.setSmoothScrollingEnabled(true);
        //scroll.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        //scroll.setHorizontalScrollBarEnabled(false);
        //scroll.setVerticalScrollBarEnabled(false);
        MGridBuilder G = new MGridBuilder(context);
        G.setColumnWidths(0.1f,0.8f,0.1f);
        G.addLine(0.1f).add(new View(context));
        G.addLine(0.8f).add(new View(context)).add(scroll).add(new View(context));
        G.addLine(0.1f).add(new View(context));

        return G.create();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final View getLayout(){
        return layout;
    }

}


















