package com.upmc.m3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity implements OnItemClickListener {

    static final LauncherIcon[] ICONS = {
            new LauncherIcon(R.drawable.new_100, "New map"),
            new LauncherIcon(R.drawable.edit_96, "Load map"),
            new LauncherIcon(R.drawable.visible_96, "View collapsible map"),
            new LauncherIcon(R.drawable.delete_96, "Delete map"),
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        GridView gridview = (GridView) findViewById(R.id.dashboard_grid);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(this);

        // Hack to disable GridView scrolling
        gridview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    public void createNewMap() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter the new map name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getBaseContext(), M3Activity.class);
                intent.putExtra("filename", input.getText().toString());
                startActivity(intent);
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
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        Intent intent;

        switch (position) {
            case 0:
                createNewMap();
                break;

            case 1:
                intent = new Intent(getBaseContext(), SelectFile.class);
                startActivityForResult(intent, position);
                break;

            case 2:
                intent = new Intent(getBaseContext(), SelectFile.class);
                startActivityForResult(intent, position);
                break;

            case 3:
                intent = new Intent(getBaseContext(), SelectFile.class);
                startActivityForResult(intent, position);
                break;

            default:
                return;

        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        Intent intent;
        String fileName = data.getStringExtra("MESSAGE");

        switch (requestCode) {
            case 1: {
                // pass filename to activity
                intent = new Intent(this, M3Activity.class);
                intent.putExtra("filename", fileName);
                startActivity(intent);
                break;
            }
            case 2: {
                // pass filename to activity
                intent = new Intent(this, Collapsible.class);
                intent.putExtra("filename", fileName);
                startActivity(intent);
                break;
            }
            case 3: {
                boolean deleted = getBaseContext().deleteFile(fileName);
                if (deleted) {
                    Toast.makeText(getBaseContext(), "Map "+ fileName + " deleted", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
        }


    }

    static class LauncherIcon {
        final String text;
        final int imgId;
        public LauncherIcon(int imgId, String text) {
            super();
            this.imgId = imgId;
            this.text = text;
        }
    }

    static class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageAdapter(Context c) {
            mContext = c;
        }
        @Override
        public int getCount() {
            return ICONS.length;
        }
        @Override
        public LauncherIcon getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        static class ViewHolder {
            public ImageView icon;
            public TextView text;
        }
        // Create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.dashboard_icon, null);
                holder = new ViewHolder();
                holder.text = (TextView) v.findViewById(R.id.dashboard_icon_text);
                holder.icon = (ImageView) v.findViewById(R.id.dashboard_icon_img);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.icon.setImageResource(ICONS[position].imgId);
            holder.text.setText(ICONS[position].text);
            return v;
        }
    }
}
