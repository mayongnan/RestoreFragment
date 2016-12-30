package com.example.yon.testfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    private final String KEY_TAGS = "viewpager_fragment_tags";
    private MyPageAdapter mPageAdapter;
    private final int PAGE_COUNT = 3;
    //当Activity需要与具体的Fragment交互时（如TitleBar中有个刷新按钮）可能需要保持对ViewPager中Fragments的引用
    private Fragment[] fragments = new Fragment[PAGE_COUNT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        if (savedInstanceState == null) {
            //第一次初始化的情况下直接创建
            for (int i = 0; i < PAGE_COUNT; i++) {
                fragments[i] = getFragment(i);
            }
        } else {
            //根据保存的tag信息，复用restore产生的Fragment
            String[] tags = savedInstanceState.getStringArray(KEY_TAGS);
            for (int i = 0; i < PAGE_COUNT; i++) {
                fragments[i] = getFragmentWithTags(i, tags);
            }
        }
        mPageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mPageAdapter);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().add(R.id.fl_container,  new Fragment2()).addToBackStack(null).commitAllowingStateLoss();
            }
        });
    }

    /**
     * 根据保存的tag信息，复用restore过程中产生的Fragment，Fragment不存在时直接创建
     *
     * @param position 要初始化Fragment的位置
     * @param tags     {@link #onSaveInstanceState}中保存的ViewPager中Fragment的Tag信息
     * @return FragmentManager中已有的Fragment或new出来的Fragment实例
     */
    private Fragment getFragmentWithTags(int position, String[] tags) {
        if (tags == null) {
            return getFragment(position);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (tags[position] != null) {
            Fragment targetFragment = fragmentManager.findFragmentByTag(tags[position]);
            return targetFragment == null ? getFragment(position) : targetFragment;
        }
        return getFragment(position);
    }

    private Fragment getFragment(int position) {
        switch (position) {
            case 0:
                return new Fragment0();
            case 1:
                return new Fragment1();
            case 2:
                return new Fragment2();
            default:
                return new Fragment0();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(KEY_TAGS, mPageAdapter.getInstantiateTags());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class MyPageAdapter extends FragmentPagerAdapter {

        private String[] mFragmentTags;
        private Fragment[] mFragments = null;

        public MyPageAdapter(FragmentManager mFragmentManager, @NonNull Fragment[] fragments) {
            super(mFragmentManager);
            this.mFragments = fragments;
            mFragmentTags = new String[mFragments.length];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            //因为父类（FragmentPagerAdapter）是在本方法中为Fragment设置tag，所以我们在此方法中进行tag保存
            mFragmentTags[position] = fragment.getTag();
            return fragment;
        }

        /**
         * 获取实例化的ItemFragment设置的tag
         *
         * @return 与 {@link #PAGE_COUNT}对应长度的tag数组
         */
        public String[] getInstantiateTags() {
            return mFragmentTags;
        }
    }

}
