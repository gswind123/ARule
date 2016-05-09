package org.windning.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.windning.arule.bridge.model.NativeMethod;
import org.windning.arule.script.ScriptContext;
import org.windning.arule.script.ScriptEngine;
import org.windning.arule.script.exception.ScriptException;
import org.windning.arule.script.model.OperationModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private Button mTestBtn = null;

    private NativeMethod mNativeAlert = new NativeMethod() {
        @Override
        public void execute(Object[] objects) {
            if(objects.length < 1 || !(objects[0] instanceof String)) {
                Log.e("NativeAlert Method", "should have an argument");
            }
            Toast.makeText(MainActivity.this, objects[0].toString(), Toast.LENGTH_SHORT).show();
        }
    };
    private int mPassengerNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTestBtn = (Button)findViewById(R.id.test_btn);
        mTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream rin = getResources().openRawResource(R.raw.test_script);
                BufferedReader reader = new BufferedReader(new InputStreamReader(rin));
                String line;
                StringBuilder scriptBuilder = new StringBuilder();
                try{
                    while((line=reader.readLine()) != null) {
                        scriptBuilder.append(line);
                    }
                }catch(IOException e) {
                    SunLogUtil.saveCrashTrace(e);
                }

                ScriptContext context = new ScriptContext();
                context.registerInterface("nativeAlert", mNativeAlert);
                MainActivity.this.mPassengerNum = 4;
                try {
                    ArrayList<OperationModel> optList = ScriptEngine.parseScript(scriptBuilder.toString());
                    for(OperationModel optModel : optList) {
                        ScriptEngine.runOperation(optModel, context, MainActivity.this);
                    }
                } catch (ScriptException e) {
                    SunLogUtil.saveCrashTrace(e);
                }
            }
        });
    }
}
