package com.example.homework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    TextView tv_minresult;
    TextView tv_maxresult;
    TextView tv_avgresult;
    SeekBar sb_runseek;
    Button bt_generate;
    int number;
    TextView tv_number;
    ProgressBar pb_loading;
    LinearLayout ll_progress;
    ArrayList<Double> arrayList = new ArrayList<>();
    ExecutorService threadPool;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("InClass4");
        tv_avgresult = findViewById(R.id.tv_avgresult);
        tv_maxresult = findViewById(R.id.tv_maxresult);
        tv_minresult = findViewById(R.id.tv_minresult);
        sb_runseek = findViewById(R.id.sb_runseek);
        pb_loading = findViewById(R.id.pb_loading);
        ll_progress = findViewById(R.id.ll_progress);
        bt_generate = findViewById(R.id.bt_generate);
        tv_number = findViewById(R.id.tv_number);

        sb_runseek.setMax(10);


        threadPool = Executors.newFixedThreadPool(2);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                pb_loading.setVisibility(View.GONE);
                ll_progress.setVisibility(View.GONE);
                tv_minresult.setText(String.valueOf(message.getData().getDouble("Minimum")));
                tv_maxresult.setText(String.valueOf(message.getData().getDouble("Maximum")));
                tv_avgresult.setText(String.valueOf(message.getData().getDouble("Average")));
                return false;
            }
        });

        sb_runseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progress == 1) {
                    tv_number.setText(progress + " Time");

                } else {
                    tv_number.setText(progress + " Times");
                }
                number = progress;
            }
        });

        pb_loading.setVisibility(View.GONE);
        ll_progress.setVisibility(View.GONE);

        bt_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (number == 0) {
                    Toast.makeText(MainActivity.this, "Please select the complexity level.", Toast.LENGTH_SHORT).show();
                } else {
                    ll_progress.setVisibility(View.VISIBLE);
                    pb_loading.setVisibility(View.VISIBLE);
                    threadPool.execute(new getNumberAsyncTask());
                }
            }
        });


    }

    public class getNumberAsyncTask implements Runnable {

        @Override
        public void run() {

            Double sum = 0.0;
            Double avg;

            arrayList = HeavyWork.getArrayNumbers(number);

            Double max = arrayList.get(0);
            Double min = arrayList.get(0);
            //Average calculate
            for (int i = 0; i < arrayList.size(); i++) {
                sum += arrayList.get(i);
            }
            avg = sum / arrayList.size();

            //minimum calculate
            for (int i = 1; i < arrayList.size(); i++) {
                if (arrayList.get(i) > max) {
                    max = arrayList.get(i);
                }
            }

            for (int i = 1; i < arrayList.size(); i++) {
                if (arrayList.get(i) < min) {
                    min = arrayList.get(i);
                }
            }


            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putDouble("Minimum", min);
            bundle.putDouble("Maximum", max);
            bundle.putDouble("Average", avg);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }


}
