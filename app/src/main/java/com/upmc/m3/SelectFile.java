package com.upmc.m3;

import java.io.File;
import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectFile extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> filesinfolder = GetFiles(getApplicationContext().getFilesDir().toString());
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        setListAdapter(new ArrayAdapter<String>(SelectFile.this, R.layout.list_file, filesinfolder));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String selectedValue = (String) getListAdapter().getItem(position);

        Intent intentMessage=new Intent();
        intentMessage.putExtra("MESSAGE", selectedValue);
        setResult(2, intentMessage);
        finish();
    }

    public ArrayList<String> GetFiles(String directorypath){
        ArrayList<String> Myfiles = new ArrayList<String>();
        File f = new File(directorypath);
        f.mkdirs();
        File[] files = f.listFiles();
        if(files.length==0){
            return null;
        }
        else{
            for(int i=0;i<files.length;i++)
                Myfiles.add(files[i].getName());
        }
        return Myfiles;
    }
}
