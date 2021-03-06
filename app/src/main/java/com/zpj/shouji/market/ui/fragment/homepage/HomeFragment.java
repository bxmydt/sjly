package com.zpj.shouji.market.ui.fragment.homepage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.ui.fragment.manager.AppManagerFragment;
import com.zpj.shouji.market.ui.fragment.search.SearchFragment;
import com.zpj.shouji.market.ui.widget.ScaleTransitionPagerTitleView;
import com.zpj.utils.ScreenUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"推荐", "发现", "乐图"};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        ArrayList<Fragment> list = new ArrayList<>();
        RecommendFragment2 recommendFragment = findChildFragment(RecommendFragment2.class);
        if (recommendFragment == null) {
            recommendFragment = new RecommendFragment2();
        }
        DiscoverFragment exploreFragment = findChildFragment(DiscoverFragment.class);
        if (exploreFragment == null) {
            exploreFragment = DiscoverFragment.newInstance();
        }
        WallpaperFragment wallpaperFragment = findChildFragment(WallpaperFragment.class);
        if (wallpaperFragment == null) {
            wallpaperFragment = new WallpaperFragment();
        }
        list.add(recommendFragment);
        list.add(exploreFragment);
        list.add(wallpaperFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), list, TAB_TITLES);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        MagicIndicator magicIndicator = (MagicIndicator) toolbar.getCenterCustomView();
        CommonNavigator navigator = new CommonNavigator(context);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ScaleTransitionPagerTitleView titleView = new ScaleTransitionPagerTitleView(context);
                titleView.setNormalColor(getResources().getColor(R.color.color_text_major));
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                indicator.setColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);

    }

    @Override
    public void onSupportVisible() {
        darkStatusBar();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    @Override
    public void toolbarRightCustomView(@NonNull View view) {
        ImageView manageBtn = view.findViewById(R.id.btn_manage);
        manageBtn.setOnClickListener(v -> {
            _mActivity.start(new AppManagerFragment());
        });
        view.findViewById(R.id.btn_search).setOnClickListener(v -> _mActivity.start(new SearchFragment()));
    }
}
