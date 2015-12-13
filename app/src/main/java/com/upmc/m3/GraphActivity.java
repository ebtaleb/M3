package com.upmc.m3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GraphActivity extends AppCompatActivity {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

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

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                mDrawerList.setItemChecked(position, false);
                //setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                R.drawable.ic_drawer, //nav menu toggle icon
//                R.string.app_name, // nav drawer open - description for accessibility
//                R.string.app_name // nav drawer close - description for accessibility
//        ){
//            public void onDrawerClosed(View view) {
//                //getActionBar().setTitle(mTitle);
//                // calling onPrepareOptionsMenu() to show action bar icons
//                invalidateOptionsMenu();
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                //getActionBar().setTitle(mDrawerTitle);
//                // calling onPrepareOptionsMenu() to hide action bar icons
//                invalidateOptionsMenu();
//            }
//        };
//
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
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
                String jsCmd = String.format("javascript:insertNode('%s')", node_n);
                webview.loadUrl(jsCmd);
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
//            case R.id.export_action:
//                new AlertDialog.Builder(this)
//                        .setTitle("Delete entry")
//                        .setMessage("Are you sure you want to delete this entry?")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .show();
//                return true;
            case R.id.reset_action:
                webview.loadUrl("javascript:createNew()");
                return true;
            case R.id.new_node_action:
                setNewNodeName();
                return true;
            case R.id.delete_action:
                webview.loadUrl("javascript:deleteNode()");
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
