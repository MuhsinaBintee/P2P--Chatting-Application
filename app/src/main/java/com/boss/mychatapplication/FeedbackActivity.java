package com.boss.mychatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton, clearButton;
    private EditText nameEditText, messageEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        sendButton = findViewById(R.id.sendButton);
        clearButton = findViewById(R.id.clearButton);

        nameEditText = findViewById(R.id.nameEditText);
        messageEditText = findViewById(R.id.messageEditText);

        sendButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        try {

            String name = nameEditText.getEditableText().toString();
            String message = messageEditText.getEditableText().toString();


            if (view.getId() == R.id.sendButton) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/email");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mdbiplobhridoy68@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from My Chat App!");
                intent.putExtra(Intent.EXTRA_TEXT, "Name : " + name);
                intent.putExtra(Intent.EXTRA_TEXT, "Message : " + message);
                startActivity(Intent.createChooser(intent, " Feedback With "));
            } else if (view.getId() == R.id.clearButton) {
                nameEditText.setText(" ");
                messageEditText.setText(" ");

            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception: "+e, Toast.LENGTH_SHORT).show();

        }



    }
}
