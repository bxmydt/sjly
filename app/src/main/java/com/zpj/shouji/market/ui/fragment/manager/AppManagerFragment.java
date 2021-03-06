package com.zpj.shouji.market.ui.fragment.manager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zpj.fragmentation.BaseFragment;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.widget.ScaleTransitionPagerTitleView;
import com.zpj.utils.ScreenUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class AppManagerFragment extends BaseFragment {

    private static final String[] TAB_TITLES = {"下载管理", "更新", "已安装", "安装包"};

    public AppManagerFragment() {
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_manager;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {

        DownloadFragment downloadFragment = findChildFragment(DownloadFragment.class);
        if (downloadFragment == null) {
            downloadFragment = new DownloadFragment();
        }

        UpdateFragment updateFragment = findChildFragment(UpdateFragment.class);
        if (updateFragment == null) {
            updateFragment = new UpdateFragment();
        }

        InstalledFragment installedFragment = findChildFragment(InstalledFragment.class);
        if (installedFragment == null) {
            installedFragment = new InstalledFragment();
        }

        PackageFragment packageFragment = findChildFragment(PackageFragment.class);
        if (packageFragment == null) {
            packageFragment = new PackageFragment();
        }

        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(downloadFragment);
        fragments.add(updateFragment);
        fragments.add(installedFragment);
        fragments.add(packageFragment);

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, TAB_TITLES);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        toolbar.getRightImageButton().setImageResource(R.drawable.ic_settings_white_24dp);
                        break;
                    case 1:
                        toolbar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
                        break;
                    case 2:
                        toolbar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
                        break;
                    case 3:
                        toolbar.getRightImageButton().setImageResource(R.drawable.ic_search_white_24dp);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return TAB_TITLES.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(Color.WHITE);
                titleView.setSelectedColor(Color.WHITE);
                titleView.setTextSize(14);
                titleView.setText(TAB_TITLES[index]);
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(ScreenUtils.dp2px(context, 4f));
                indicator.setLineWidth(ScreenUtils.dp2px(context, 12f));
                indicator.setRoundRadius(ScreenUtils.dp2px(context, 4f));
                indicator.setColors(Color.WHITE, Color.WHITE);
                return indicator;
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

}
