package com.boss.mychatapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class MainActivity extends AppCompatActivity {
    EditText receivePortEditText, targetPortEditText, messageEditText, targetIPEditText;
    TextView chatText;
    ScrollView scrollView2;

    LinearLayout chatLayout;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    static final int MESSAGE_READ=1;
    static  final int  BACKGROUND_COLOR=2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    static final String TAG = "yourTag";
    String Allmssg="";
    View view;



//For sending message-- For sending message one thread to main thread

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_READ:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);

                    String[] array = tempMsg.split("&&&&",tempMsg.length());

                    if(array[0].equals("1"))
                    {
                        Allmssg=Allmssg+"Friend: "+array[1]+"\n";
                        chatText.setText(Allmssg);
                    }

                    else if(array[0].equals("2"))
                    {
                        writeToFile("Saved Messages",array[1]);
                    }

                    else if(array[0].equals("4"))
                    {
                        if(array[1].equals("Blue"))
                        {
                            scrollView2.setBackgroundColor(getResources().getColor(R.color.Blue));
                        }

                        if(array[1].equals("Kolapata"))
                        {
                            scrollView2.setBackgroundColor(getResources().getColor(R.color.kolapata));
                        }

                        if(array[1].equals("Yellow"))
                        {
                            scrollView2.setBackgroundColor(getResources().getColor(R.color.Yellow));
                        }

                        if(array[1].equals("White"))
                        {
                            scrollView2.setBackgroundColor(getResources().getColor(R.color.white1));
                        }


                    }



                    break;
            }


            return true;
        }
    });


//    Handler handler2=new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what)
//            {
//                case MESSAGE_READ:
//                    byte[] readBuff= (byte[]) msg.obj;
//                    String tempMsg=new String(readBuff,0,msg.arg1);
//                    add2(tempMsg);
//                    break;
//            }
//            return true;
//        }
//    });




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = this.getWindow().getDecorView();
        view.setBackgroundResource(R.color.saya1);

        receivePortEditText =(EditText) findViewById(R.id.receiveEditText);
        targetPortEditText = (EditText) findViewById(R.id.targetPortEditText);
        messageEditText = findViewById(R.id.messageEditText);
        targetIPEditText = findViewById(R.id.targetIPEditText);
        scrollView2 = (ScrollView) findViewById(R.id.scrollView2);
        chatText = findViewById(R.id.EditText1);

        scrollView2.setBackgroundColor(getResources().getColor(R.color.kolapata));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyStoragePermissions();
    }





    //For creating toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == R.id.blue) {
            //view.setBackgroundResource(R.color.Blue);
            scrollView2.setBackgroundColor(getResources().getColor(R.color.Blue));
            String msg="4&&&&Blue";
            sendReceive.write(msg.getBytes());
        }
        if (item.getItemId() == R.id.kolapata) {
            //view.setBackgroundResource(R.color.kolapata);
            scrollView2.setBackgroundColor(getResources().getColor(R.color.kolapata));
            //Toast.makeText(getApplicationContext(), "Kkkk", Toast.LENGTH_SHORT).show();
            String msg="4&&&&Kolapata";
            //sendReceive.write(msg.getBytes());
        }

        if (item.getItemId() == R.id.Yellow) {
            //view.setBackgroundResource(R.color.Yellow);

            scrollView2.setBackgroundColor(getResources().getColor(R.color.Yellow));
            String msg="4&&&&Yellow";
            sendReceive.write(msg.getBytes());
        }

        if (item.getItemId() == R.id.white1) {
            //view.setBackgroundResource(R.color.white1);
            scrollView2.setBackgroundColor(getResources().getColor(R.color.white1));
            String msg="4&&&&White";
            sendReceive.write(msg.getBytes());
        }

        if(id == R.id.changecolor){
            {
                //sendReceive.write(msg.getBytes());
                Toast.makeText(getApplicationContext(), "You Click Change Color", Toast.LENGTH_SHORT).show();
            }

        }


        else if(id == R.id.sendFileButton)
        {
            Intent intent = new Intent().setType("text/plain").setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a TXT file"), 123);
            //Toast.makeText(getApplicationContext(), "You Click Send Text File", Toast.LENGTH_SHORT).show();


            String msg="2&&&&";
            //sendReceive.write(msg.getBytes());

        }

        else if(id == R.id.saveButton) {
            //Toast.makeText(getApplicationContext(), "You Click Save Chat", Toast.LENGTH_SHORT).show();

            writeToFile("Saved Messages",Allmssg);

        }

        else if(id == R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            String subject = "Android P2P Chat Application";
            String  body = "This is an amazing chat application \nLink: https://drive.google.com/file/d/1rmh9IzyNlGN8gTCFwPAY12YRvD823Bvq/view";
            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            intent.putExtra(Intent.EXTRA_TEXT,body);
            startActivity(Intent.createChooser(intent," share With "));


        }else if(id == R.id.setting) {
            Toast.makeText(getApplicationContext(), "You Click Setting", Toast.LENGTH_SHORT).show();

        }else if(id == R.id.feedback){
            Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
            startActivity(intent);
        }


        return true;
    }




    public void onStartServerClicked(View v){
        String port = receivePortEditText.getText().toString();
        serverClass = new ServerClass(Integer.parseInt(port));
        serverClass.start();
    }

    public void onConnectClicked(View v){
        String port = targetPortEditText.getText().toString();
        clientClass = new ClientClass(targetIPEditText.getText().toString(), Integer.parseInt(port));
        clientClass.start();
    }

    public void onSendClicked(View v)
    {
        String msg=messageEditText.getText().toString();

        //add(msg);
        Allmssg=Allmssg+"Me: "+msg+"\n";

        chatText.setText(Allmssg);

        msg="1&&&&"+msg;

        sendReceive.write(msg.getBytes());
    }



    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;
        int port;

        public ServerClass(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                serverSocket=new ServerSocket(port);
                Log.d(TAG, "Waiting for client...");
                socket=serverSocket.accept();
                Log.d(TAG, "Connection established from server");
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "ERROR/n"+e);
            }
        }
    }

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt)
        {
            socket=skt;
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer=new byte[1024];
            int bytes;

            while (socket!=null)
            {
                try {
                    bytes=inputStream.read(buffer);
                    if(bytes>0)
                    {
                        handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(final byte[] bytes)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(bytes);
                       // handler2.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;
        int port;

        public  ClientClass(String hostAddress, int port)
        {
            this.port = port;
            this.hostAdd = hostAddress;
        }

        @Override
        public void run() {
            try {

                socket=new Socket(hostAdd, port);
                Log.d(TAG, "Client is connected to server");
                sendReceive=new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Can't connect from client/n"+e);
            }
        }
    }


    public void add(final String message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = new TextView(getApplicationContext());
                text.setTextColor(Color.WHITE);
                text.setText(message);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.weight = 1.0f;
                params.gravity = Gravity.CENTER;

                text.setLayoutParams(params);
                chatLayout.addView(text);
            }
        });
    }


    public void verifyStoragePermissions() {
        // Check if we have write permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    private void writeToFile(String fileName, String data) {
        Long time= System.currentTimeMillis();
        String timeMill = " "+time.toString();
        File defaultDir = Environment.getExternalStorageDirectory();
        File file = new File(defaultDir, fileName+timeMill+".txt");
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file, false);
            stream.write(data.getBytes());
            stream.close();
            Toast.makeText(getApplicationContext(), "file saved in: "+file.getPath(), Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }



    private String uriToString(Uri uri){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append("\n"+line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==123 && resultCode==RESULT_OK) {
            Uri uri = intent.getData();
            String textInsideTheSelectedFile = uriToString(uri);
            String mssg="2&&&&"+textInsideTheSelectedFile;
            sendReceive.write(mssg.getBytes());
        }
    }




//    public void add2(final String message){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                TextView text = new TextView(getApplicationContext());
//                text.setTextColor(Color.WHITE);
//                text.setText(message);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                params.weight = 1.0f;
//                params.gravity = Gravity.RIGHT;
//
//                text.setLayoutParams(params);
//                chatLayout.addView(text);
//            }
//        });
//    }

}


