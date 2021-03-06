package com.upmc.m3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class M3Activity extends AppCompatActivity {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String fileName;

    @Bind(R.id.webview)
    WebView webview;

    public class WebAppInterface {
        private Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public String loadData() {
            File file = new File(context.getFilesDir(), fileName);
            String text = "";

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text += line;
                }
                br.close();
            }
            catch (IOException e) {

            }

            return text;
        }

        @JavascriptInterface
        public void saveData(String s) {
            FileOutputStream out_stream;

            try {
                out_stream = openFileOutput(fileName, Context.MODE_PRIVATE);
                out_stream.write(s.getBytes());
                out_stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            fileName = b.getString("filename");
        } else {
            fileName = "derp";
        }

        setContentView(R.layout.activity_m3);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(fileName);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setNewMapFileName();
                        webview.loadUrl("javascript:createNew()");
                        break;
                    case 1:
                        Intent intentGetMessage = new Intent(getBaseContext(), SelectFile.class);
                        startActivityForResult(intentGetMessage, 2);
                        break;
                    case 2: {
                        webview.loadUrl("javascript:save()");
                        Toast.makeText(getBaseContext(), "File saved", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    default:
                        break;
                }
                mDrawerList.setItemChecked(position, false);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            fileName = data.getStringExtra("MESSAGE");
            getSupportActionBar().setTitle(fileName);
            Toast.makeText(this, "Map " + fileName + " loaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void setNewMapFileName() {

        String name = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter the new map name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileName = input.getText().toString();
                getSupportActionBar().setTitle(fileName);
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

    public void setNewNodeName() {
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

    public void renameNode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter desired node name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String node_n = input.getText().toString();
                String jsCmd = String.format("javascript:renameNode('%s')", node_n);
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

        switch (item.getItemId()) {
            case R.id.new_node_action:
                setNewNodeName();
                break;
            case R.id.rename_node:
                renameNode();
                break;
            case R.id.delete_action:
                new AlertDialog.Builder(this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this node?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                webview.loadUrl("javascript:deleteNode()");
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
            case R.id.move_LR: webview.loadUrl("javascript:moveNodes('left', 'right')"); break;
            case R.id.move_RL: webview.loadUrl("javascript:moveNodes('right', 'left')"); break;
            case R.id.diagonal: webview.loadUrl("javascript:setConnector('diagonal')"); break;
            case R.id.elbow: webview.loadUrl("javascript:setConnector('elbow')"); break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
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
        webview.addJavascriptInterface(new WebAppInterface(this), "Android");
        webview.loadUrl("file:///android_asset/main.html");
    }
}
