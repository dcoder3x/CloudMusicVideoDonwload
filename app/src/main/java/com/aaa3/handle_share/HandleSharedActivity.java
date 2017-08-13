package com.aaa3.handle_share;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.aaa3.R;
import com.aaa3.model.FileModel;
import com.aaa3.show_result.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleSharedActivity extends AppCompatActivity {

    private TextView mTextView;
    private EditText mEditText;

    private FileModel mModel = new FileModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_shared);

        initView();
        parseIntent(getIntent());
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv);
        mEditText = (EditText) findViewById(R.id.et);
        CardView cardView = (CardView) findViewById(R.id.click_card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(buildRequest());
                Toast.makeText(HandleSharedActivity.this, "开始下载", Toast.LENGTH_LONG).show();
                startActivity(new Intent(HandleSharedActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void parseIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null && type.equals("text/plain")) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (!text.trim().equals("")) {
                text = text.substring(text.indexOf(" ") + 1);
                // text may be have \r \n , so use [\s\S] match any char.
                String pattern = "([\\s\\S]*)http://music[.]163[.]com/event[?]id=(\\d+)&uid=(\\d+)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(text);
                if (m.find()) {
                    mModel.filename = m.group(2);
                    mModel.url = "http://music.163.com/m/video/redirect?eventId="+ m.group(2) + "&userId=" + m.group(3);
                    mModel.desc = m.group(1);
                    mEditText.setText(mModel.filename + ".mp4");
                    mTextView.setText(mModel.desc);
                } else {

                    Toast.makeText(this, "好像不是一个可下载的视频。。。", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(this, "没有分享任何内容。。。", Toast.LENGTH_LONG).show();
            }
        }
    }

    private DownloadManager.Request buildRequest() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mModel.url));
        mModel.filename = mEditText.getText().toString().replace(".mp4", "");

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setDescription(mModel.desc)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setVisibleInDownloadsUi(false)
                .setTitle(mModel.filename)
                .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, mModel.filename + ".mp4");
        return request;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
