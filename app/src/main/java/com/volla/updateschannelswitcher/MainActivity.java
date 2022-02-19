package com.volla.updateschannelswitcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String getprop(String key, String def) {
        String ret = def;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);

            ret = (String) get.invoke(c, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private Boolean setprop(String key, String value) {
        Boolean ret = false;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);

            set.invoke(c, key, value);
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currentType = getprop("ro.lineage.releasetype", "STABLE");
        String message = getResources().getString(R.string.top_text, currentType);
        TextView topText = findViewById(R.id.top_textView);
        topText.setText(message);

        Spinner chSpinner = (Spinner) findViewById(R.id.channel_spinner);

        List<String> list = new ArrayList<String>();
        list.add("Current (" + currentType + ")");
        list.add("STABLE");
        list.add("BETA");
        list.add("WEEKLY");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chSpinner.setAdapter(dataAdapter);

        String overrideType = getprop("persist.volla.update.type", "NULL");
        if (!TextUtils.equals(overrideType, "NULL")) {
            int pos = 0;
            for(String type : list) {
                if (TextUtils.equals(type, overrideType)) {
                    chSpinner.setSelection(pos, false);
                }
                pos++;
            }
        } else {
            chSpinner.setSelection(0, false);
        }

        chSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String overrideType = "";
                if (i > 0) {
                    overrideType = list.get(i);
                }

                String msg = getResources().getString(R.string.failed_text);
                if (setprop("persist.volla.update.type", overrideType)) {
                    msg = getResources().getString(R.string.switched_text,
                            getprop("persist.volla.update.type", currentType));
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}