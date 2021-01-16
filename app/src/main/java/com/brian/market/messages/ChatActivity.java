package com.brian.market.messages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.brian.market.R;
import com.brian.market.utills.SettingsMain;

public class ChatActivity extends AppCompatActivity {

    SettingsMain settingsMain;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.chat));
        setSupportActionBar(toolbar);
        settingsMain = new SettingsMain(this);


        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        if (!intent.getStringExtra("receiverId").equals("")) {

            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("chatId", intent.getStringExtra("chatId"));
            bundle.putString("receiverId", intent.getStringExtra("receiverId"));
            bundle.putString("sender_name", intent.getStringExtra("sender_name"));
            bundle.putString("text", intent.getStringExtra("text"));
            bundle.putString("topic", intent.getStringExtra("topic"));
            bundle.putString("img", intent.getStringExtra("img"));
            bundle.putBoolean("type", intent.getBooleanExtra("type", false));
//            bundle.putString("is_block", intent.getStringExtra("is_block"));

            chatFragment.setArguments(bundle);
            startFragment(chatFragment);
        } else {
            RecievedOffersList recievedOffersList = new RecievedOffersList();
            Bundle bundle = new Bundle();
            bundle.putString("adId", intent.getStringExtra("adId"));
            recievedOffersList.setArguments(bundle);
            startFragment(recievedOffersList);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.message, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            ChatActivity.this.recreate();
            return true;
        }
//        if (id == R.id.block_Button) {
//                ChatFragment chatFragment = new ChatFragment();
//                    chatFragment.getContext();
//                    chatFragment.adforest_BlockChat();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void startFragment(Fragment someFragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frameContainer);

        if (fragment == null) {
            fragment = someFragment;
            fm.beginTransaction()
                    .add(R.id.frameContainer, fragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
