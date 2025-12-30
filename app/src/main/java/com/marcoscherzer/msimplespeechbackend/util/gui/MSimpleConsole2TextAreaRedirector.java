package com.marcoscherzer.msimplespeechbackend.util.gui;

import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MSimpleConsole2TextAreaRedirector extends PrintStream {
    public MSimpleConsole2TextAreaRedirector(TextView textArea) {
        super(new OutputStream() {

            final TextView target = textArea;

            @Override
            public final void write(int b) {
                postToUi(String.valueOf((char) b));
            }

            @Override
            public final void write(byte[] b, int off, int len) {
                String text = new String(b, off, len);
                postToUi(text);
            }
            private int lastLineCnt = -1;

            /**
             * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
             */
            private void postToUi(final String text) {
                target.post(new Runnable() {
                    String text_ = text;
                    @Override
                    public void run() {
                        target.append(text_);
                        if(target.getLineCount() !=lastLineCnt)
                        if(target.getParent() instanceof ScrollView){
                            ((ScrollView)target.getParent()).computeScroll();
                            ((ScrollView)target.getParent()).fullScroll(View.FOCUS_DOWN);
                            ((ScrollView)target.getParent()).fling(100);
                            lastLineCnt=target.getLineCount();
                        }
                    }
                });
            }
        }, true); // autoFlush
    }
}


