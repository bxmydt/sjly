package com.zpj.shouji.market.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.felix.atoast.library.AToast;
import com.zpj.fragmentation.SupportActivity;
import com.zpj.fragmentation.SupportFragment;
import com.zpj.fragmentation.anim.DefaultHorizontalAnimator;
import com.zpj.fragmentation.anim.FragmentAnimator;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpPreLoader;
import com.zpj.shouji.market.manager.AppInstalledManager;
import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.ui.fragment.MainFragment;
import com.zpj.utils.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class MainActivity extends SupportActivity {

    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        OpeningStartAnimation openingStartAnimation3 = new OpeningStartAnimation.Builder(MainActivity.this)
                .setDrawStategy(new NormalDrawStrategy())
                .setAppName("手机乐园")
                .setAppStatement("分享优质应用")
                .setAnimationInterval(1500)
                .setAppIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .setAnimationListener(new OpeningStartAnimation.AnimationListener() {
                    @Override
                    public void onFinish(OpeningStartAnimation openingStartAnimation, Activity activity) {

                        openingStartAnimation.dismiss(activity);

                        MainFragment mainFragment = findFragment(MainFragment.class);
                        if (mainFragment == null) {
                            mainFragment = new MainFragment();
                            loadRootFragment(R.id.content, mainFragment);
                        }

                        StatusBarUtils.setDarkMode(getWindow());
                        AppUpdateManager.getInstance().checkUpdate(MainActivity.this);
                    }
                })
                .create();
        openingStartAnimation3.show(MainActivity.this);

        UserManager.getInstance().init();

        HttpPreLoader.getInstance().loadHomepage();

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        AppInstalledManager.getInstance().loadApps(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else if (System.currentTimeMillis() - firstTime > 2000) {
            AToast.warning("再次点击退出！");
            firstTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    @Subscribe
    public void startFragment(SupportFragment fragment) {
        start(fragment);
    }

}
