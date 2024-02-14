package com.example.mythread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.example.mythread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    static String myText1, myText2, myText3;

    Handler handler;

    public static Object lock = new Object();
    public static int now_num = 0;
    public static int length1;
    public static String mytext = "";
    public static boolean p = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                char[] chars = (char[]) msg.obj;
                String str;
                str = String.valueOf(chars);
                if (p){
                    mytext += str + ' ';
                    binding.TV.setText(mytext);
                    p = false;}
                else {
                    binding.TV.setText(mytext + str);
                }
            }
        };

        binding.B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myText1 = binding.ET1.getText().toString();
                myText2 = binding.ET2.getText().toString();
                myText3 = binding.ET3.getText().toString();

                length1 = myText1.split("\\s+").length + myText2.split("\\s+").length + myText3.split("\\s+").length;

                MyThread myThread1 = new MyThread(myText1, 0);
                MyThread myThread2 = new MyThread(myText2, 1);
                MyThread myThread3 = new MyThread(myText3, 2);

                myThread1.start();
                myThread2.start();
                myThread3.start();

            }
        });
    }

    class MyThread extends Thread {
        private char[] TextToView;
        private String text;
        private int num;
        private int now_i = 0;

        public MyThread(String text, int num) {
            this.text = text;
            this.num = num;
            this.TextToView = new char[text.length()];
        }

        @Override
        public void run() {
            try {
            while (now_num < length1) {
                if (now_i == text.length())
                    length1++;
                synchronized (lock) {
                    while (num != now_num % 3) {
                        lock.wait();}
                        char[] textchars = text.toCharArray();
                        char ch;
                        for (int i = this.now_i; i < textchars.length; i++) {
                            if (textchars[i] != ' ')
                                ch = textchars[i];
                            else
                                ch = Character.MIN_VALUE;

                            if(i == textchars.length - 1 || textchars[i] == ' '){
                                this.now_i = i + 1;
                                p=true;}

                            TextToView[i] = ch;
                            Message msg = new Message();
                            msg.obj = TextToView;
                            handler.sendMessage(msg);

                            try {
                                sleep(200);
                            } catch (InterruptedException e) {
                                throw new RuntimeException();
                            }
                            if (textchars[i] == ' ' || i == textchars.length - 1){
                                try {
                                    sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException();
                                }

                                this.TextToView  = new char[text.length()];
                                break;}
                        }
                    now_num++;
                    lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {throw new RuntimeException(e);}
        }
    }
}