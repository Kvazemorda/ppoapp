package com.ppoapp.controller;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebView;

public class WebViewAsync extends WebView {

    public WebViewAsync(Context context) {
        super(context);
    }

    public WebViewAsync(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewAsync(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void loadWebViewAsync(String path){
        new LoadViewPage(path).execute(this);
        this.setPadding(0,0,0,0);
    }

    public class LoadViewPage extends AsyncTask<WebViewAsync, Void, WebViewAsync> {
        String path;
        public LoadViewPage(String path) {
            this.path = path;
        }

        @Override
        protected WebViewAsync doInBackground(WebViewAsync... params) {
            params[0].loadDataWithBaseURL(null, path, "text/html", "UTF-8", null);
            return params[0];
        }
    }
}
