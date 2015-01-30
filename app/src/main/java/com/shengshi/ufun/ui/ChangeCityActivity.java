package com.shengshi.ufun.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.shengshi.base.animation.ActivityAnimation;
import com.shengshi.base.tools.Log;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.app.UFunApplication;
import com.shengshi.ufun.bean.CityEntity;
import com.shengshi.ufun.bean.CityEntity.City;
import com.shengshi.ufun.bean.CityEntity.Result;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.utils.UFunTool;
import com.shengshi.ufun.utils.location.LocationResultMgr;
import com.shengshi.ufun.weight.sortlistview.CharacterParser;
import com.shengshi.ufun.weight.sortlistview.ClearEditText;
import com.shengshi.ufun.weight.sortlistview.PinyinComparator;
import com.shengshi.ufun.weight.sortlistview.SideBar;
import com.shengshi.ufun.weight.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.shengshi.ufun.weight.sortlistview.SortAdapter;
import com.shengshi.ufun.weight.sortlistview.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChangeCityActivity extends LifeCircleBaseActivity implements OnClickListener {
    ListView sortListView;
    SideBar sideBar;
    TextView dialog;
    TextView city_name;
    SortAdapter adapter;
    ClearEditText mClearEditText;
    String cityName;
    int cityId = 0;
    int tp;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.change_city);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_change_city;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        tp = this.getIntent().getIntExtra("tp", 1);
        if (tp == 1) {
            setReturnBtnEnable(false);
        } else if (tp == 2) {
            setReturnBtnEnable(true);
            TopUtil.updateTopTextViewIconLeft(this, R.id.lifecircle_top_btn_return,
                    R.drawable.close);
            setOnReturnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.hideSoftInputMode(mClearEditText, ChangeCityActivity.this, true);
                    finish();
                }
            });
        }
        TopUtil.setOnclickListener(mActivity, R.id.city_refresh, this);
    }

    @Override
    protected void initData() {
        requestUrl();
        initViews();
    }

    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("city.getlist");
        encryptInfo.resetParams();
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<CityEntity> jsonCallback = new JsonCallback<CityEntity>() {

        @Override
        public void onSuccess(CityEntity result) {
            hideLoadingBar();
            if (result != null) {
                sideBar.setVisibility(View.VISIBLE);
                SourceDateList = filledData(result);
                //sideBar.setLetter(result.data.list);
                // 根据a-z进行排序源数据
                Collections.sort(SourceDateList, pinyinComparator);
                adapter = new SortAdapter(mActivity, SourceDateList);
                sortListView.setAdapter(adapter);
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideLoadingBar();
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        city_name = findTextViewById(R.id.city_name);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = findTextViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        cityName = LocationResultMgr.getInstance(this).getCityName();
        city_name.setText(cityName);
        city_name.setOnClickListener(this);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                if (s.equals("Hot")) {
                    s = "A";
                }
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                SortModel sortModel = (SortModel) adapter.getItem(position);
                cityName = sortModel.getName();
                cityId = sortModel.getCityid();
                saveCity();
            }
        });
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(CityEntity ce) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        List<Result> result = ce.data.list;
        for (int i = 0; i < result.size(); i++) {
            List<City> list = result.get(i).list;
            for (int ii = 0; ii < list.size(); ii++) {
                SortModel sortModel = new SortModel();
                sortModel.setId(list.get(ii).id);
                sortModel.setCityid(list.get(ii).cityid);
                sortModel.setName(list.get(ii).name);
                sortModel.setIfopen(list.get(ii).ifopen);
                sortModel.setIshot(list.get(ii).ishot);
                if (!result.get(i).firstchar.equals("hot")) {
                    sortModel.setLetters(result.get(i).firstchar);
                    sortModel.setSortLetters(list.get(ii).firstchar);
                } else {
                    sortModel.setLetters("Hot");
                    sortModel.setSortLetters("A");
                }
                if (list.get(ii).name.equals(cityName)) {
                    cityId = list.get(ii).cityid;
                }
                mSortList.add(sortModel);
            }
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    private void saveCity() {
        UFunTool.saveCityId(this, StringUtils.toString(cityId));
        UFunTool.saveCityName(this, cityName);
        BaseEncryptInfo.getInstance(this).setCityid(StringUtils.toString(cityId));
        Intent intent = new Intent();
        if (tp == 1) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            ActivityAnimation.pendingTransitionIn(this);
        } else {
            setResult(RESULT_OK, intent);
        }
        UIHelper.hideSoftInputMode(mClearEditText, ChangeCityActivity.this, true);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.city_name) {
            if (cityId != 0) {
                saveCity();
            } else {
                toast("无当前定位的城市，请选择");
            }
        } else if (id == R.id.city_refresh) {
            city_name.setText("定位中...");
            UFunApplication.initRebateApplication(this, 1, city_name);
        }
    }

    @Override
    public void onBackPressed() {
        UIHelper.hideSoftInputMode(mClearEditText, this, true);
        finish();
    }

}
