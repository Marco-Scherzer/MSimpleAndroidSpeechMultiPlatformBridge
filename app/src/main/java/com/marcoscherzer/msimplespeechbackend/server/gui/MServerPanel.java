package com.marcoscherzer.msimplespeechbackend.server.gui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.marcoscherzer.msimplespeechbackend.util.gui.MAnimatedButton;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MServerPanel {

    EditText logArea;
    MAnimatedButton resetButton;
    LinearLayout layout;
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final MAnimatedButton getResetButton(){
        return resetButton;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final EditText getLogArea(){
        return logArea;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MServerPanel(Context context) {
        GradientDrawable border = new GradientDrawable();
        border.setStroke(4, Color.rgb(177,150,0));
        border.setCornerRadius(12);
        border.setColor(Color.rgb(27,27,27));

        layout = new LinearLayout(context);
        layout.setBackground(border);
        layout.setPadding(16, 16, 16, 16);

        LinearLayout serverGroup = new LinearLayout(context);
        serverGroup.setOrientation(LinearLayout.VERTICAL);

        TextView header = new TextView(context);
        header.setText("Server");
        header.setTextColor(Color.rgb(177,150,0));
        header.setTextSize(18);
        header.setPadding(0, 0, 0, 16);
        header.setGravity(Gravity.CENTER);
        serverGroup.addView(header,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        logArea = new EditText(context);
        logArea.setHint("Server log...");
        logArea.setTextColor(Color.rgb(0,160,200));
        logArea.setHintTextColor(Color.GRAY);
        logArea.setBackgroundColor(Color.rgb(27,27,27));
        logArea.setTextSize(14);
        logArea.setPadding(24, 24, 24, 24);
        logArea.setGravity(Gravity.TOP | Gravity.START);
        logArea.setSingleLine(false);
        logArea.setEnabled(false);
        logArea.setMovementMethod(new ScrollingMovementMethod());

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.addView(logArea);
        serverGroup.addView(scroll,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1f));

        resetButton = new MAnimatedButton(context);
        resetButton.setButtonText("1. Start Client Pairing");
        serverGroup.addView(resetButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(serverGroup,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }


    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final LinearLayout getLayout(){
        return layout;
    }
}

