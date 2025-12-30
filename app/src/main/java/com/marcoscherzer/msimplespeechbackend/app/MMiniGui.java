package com.marcoscherzer.msimplespeechbackend.app;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.marcoscherzer.msimplespeechbackend.client.gui.MClientPanel;
import com.marcoscherzer.msimplespeechbackend.server.gui.MServerPanel;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
final class MMiniGui {

    private MServerPanel serverPanel;
    private MClientPanel clientPanel;
    private LinearLayout layout;
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MMiniGui(Context context) {
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.BLACK);
        layout.setPadding(5, 5, 5, 5);
        layout.setWeightSum(100);

        // === SERVER-Bereich ===
        serverPanel = new MServerPanel(context);
        layout.addView(serverPanel.getLayout(),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        48.75f));

        // Spacer
        View spacer = new View(context);
        layout.addView(spacer,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        2.5f));

        // === CLIENT-Bereich ===
        clientPanel = new MClientPanel(context);
        layout.addView(clientPanel.getLayout(),
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        48.75f));
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MServerPanel getServerPanel() {
        return serverPanel;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MClientPanel getClientPanel() {
        return clientPanel;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public LinearLayout getLayout() {
        return layout;
    }
}


















