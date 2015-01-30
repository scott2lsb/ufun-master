package com.shengshi.ufun.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.ufun.R;
import com.shengshi.ufun.common.KeywordsDao;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;

import java.util.ArrayList;

public class HomeSearchActivity extends LifeCircleBaseListActivity implements
        OnClickListener {
    private ArrayAdapter<String> mSearchAdapter;
    private ArrayList<String> mSearchList;
    private EditText editText;
    private Button btn_clear_search_history;
    private LinearLayout search_historylin;

    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        TopUtil.showView(mActivity, R.id.lifecircle_search);
        TopUtil.updateLeft(mActivity, R.id.lifecircle_top_btn_return,
                R.drawable.close);

        mListView = findXListViewById(R.id.home_nearest_search);
        btn_clear_search_history = findButtonById(R.id.btn_clear_search_history);
        btn_clear_search_history.setOnClickListener(this);
        editText = findEditTextById(R.id.lifecircle_search);
        search_historylin = findLinearLayoutById(R.id.search_historylin);

        mListView.setPullLoadEnable(false);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                String keyword = mSearchList.get(position - 1);
                if (!TextUtils.isEmpty(keyword)) {
                    Intent intent = new Intent(mContext, SearchResultActivity.class);
                    intent.putExtra("keyword", keyword);
                    startActivity(intent);
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                    etSearch();
                    return true;
                }
                return false;
            }
        });

    }

    private void etSearch() {
        String keyword = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(keyword)) {
            UIHelper.hideSoftInputMode(editText, this, true);
            KeywordsDao dao = new KeywordsDao(mContext);
            dao.insert(keyword);
            Intent intent = new Intent(this, SearchResultActivity.class);
            intent.putExtra("keyword", keyword);
            startActivity(intent);
        } else {
            toast("搜索字段不能为空！");
        }
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_home_search;
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(true);
    }

    private void initKeywordData(int page) {
        new QueryKewywordTask().execute();
    }

    class QueryKewywordTask extends AsyncTask<Integer, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            KeywordsDao dao = new KeywordsDao(mContext);
            ArrayList<String> keywordList = dao.queryKeywordList();
            return keywordList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> keywordList) {
            // loadingView.setVisibility(View.GONE);
            if (keywordList != null) {
                mSearchList = new ArrayList<String>();
                mSearchAdapter = new ArrayAdapter<String>(mActivity,
                        R.layout.searchrecord_listitem, R.id.searchrecord_listitemtv, mSearchList);
                mListView.setAdapter(mSearchAdapter);
                search_historylin.setVisibility(View.VISIBLE);
                mSearchList.addAll(keywordList);
                mSearchAdapter.notifyDataSetChanged();
            } else {
                search_historylin.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public String getTopTitle() {

        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear_search_history:
                KeywordsDao dao = new KeywordsDao(mContext);
                dao.removeAllSearchKeywords();
                mSearchAdapter.notifyDataSetChanged();
                search_historylin.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        initKeywordData(1);
        super.onRestart();
    }

}
