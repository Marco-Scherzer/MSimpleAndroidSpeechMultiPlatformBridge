package com.marcoscherzer.msimplespeechbackend.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * @version 0.0.1 , unready intermediate state
 * Author: Marco Scherzer
 * Ideas, APIs, Nomenclatures & Architectures: Marco Scherzer
 * Copyright Marco Scherzer
 * All rights reserved
 */
public class MMediaButtonReceiver extends BroadcastReceiver {

    @Override
    public final void onReceive(Context context, Intent intent) {

        System.out.println("MMediaButtonReceiver: onReceive() called");

        if (!Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            System.out.println("MMediaButtonReceiver: Unexpected intent action = " + intent.getAction());
            return;
        }

        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        if (event == null) {
            System.out.println("MMediaButtonReceiver: ERROR → KeyEvent is null");
            return;
        }

        System.out.println("MMediaButtonReceiver: Media button event → keyCode="
                + event.getKeyCode() + ", action=" + event.getAction());

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {

                System.out.println("MMediaButtonReceiver: Headset hook pressed → triggering speech job");

                //MMain.get().getClient().submitRecordJob();
                MMain.get().getServer().getClientInformation();

            } else {
                System.out.println("MMediaButtonReceiver: Unhandled media keyCode = " + event.getKeyCode());
            }

        } else {
            System.out.println("MMediaButtonReceiver: Ignoring non-ACTION_DOWN event");
        }
    }
}
