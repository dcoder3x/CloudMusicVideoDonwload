package com.aaa3.show_result;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.aaa3.R;


public class MainActivity extends AppCompatActivity implements RVAdapter.Listener {

    private DownloadFragment mFragment;

    private DownloadReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new DownloadReceiver();
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        FragmentManager fm = getSupportFragmentManager();
        mFragment = (DownloadFragment) fm.findFragmentById(R.id.container);
        if (mFragment == null) {
            mFragment = new DownloadFragment();
            fm.beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
            fm.executePendingTransactions();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mReceiver = null;
        // when activity opened in other app, return other app, open in task. the same intent again?
        getIntent().setType("useless");
    }

    @Override
    public void playVideo(String fileDir) {
        Uri uri = Uri.parse(fileDir);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);
    }


    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_LONG).show();
               mFragment.loadRecord(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            }
        }
    }
}
