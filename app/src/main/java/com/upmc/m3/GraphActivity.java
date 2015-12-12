package com.upmc.m3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GraphActivity extends AppCompatActivity {

    @Bind(R.id.webview)
    WebView webview;

    public class WebAppInterface {
        private Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public String loadData() {
            return "";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_child);
        ButterKnife.bind(this);
        //Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        //ActionBar ab = getSupportActionBar();

        // Enable the Up button
        //ab.setDisplayHomeAsUpEnabled(true);
    }

    public void setNewNodeName() {

        String name = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New node name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String node_n = input.getText().toString();
                webview.loadUrl("javascript:alert('" + node_n + "')");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_action:
                new AlertDialog.Builder(this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return true;
            case R.id.reset_action:
                webview.loadUrl("javascript:createNew()");
                return true;
            case R.id.new_node_action:
                setNewNodeName();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        final WebSettings ws = webview.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowContentAccess(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.addJavascriptInterface( new WebAppInterface( this ), "Android");
        webview.loadUrl("file:///android_asset/main.html");
    }
}
