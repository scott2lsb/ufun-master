package com.shengshi.ufun.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.mine.AttentionEntity;
import com.shengshi.ufun.bean.mine.AttentionEntity.Attentions;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;

import java.util.ArrayList;
import java.util.List;

public class MyAttentionActivity extends LifeCircleBaseListActivity implements OnItemClickListener {

    int page = 1;
    int count = 0;
    List<Attentions> mData = new ArrayList<Attentions>();
    MineAttentionAdapter mAdapter;
    int attentionTouid = 0;
    int is_attention = 1;
    ImageView mjoinIv;
    TextView mcancelTv;
    int mposition = 0;
    int touid = 0;
    Boolean resul_ok = false;

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine_attention);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_my_concern;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        mListView.setOnItemClickListener(this);
        setOnReturnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (touid == 0 && resul_ok) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        touid = getIntent().getIntExtra("touid", 0);
        Log.i("------->touid==" + touid);
        requestUrl();
    }

    /**
     * 获取用户的关注列表
     */
    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.get_user_attentions");
        encryptInfo.resetParams();
        if (touid > 0) {
            encryptInfo.putParam("touid", touid);
        }
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", UFunConstants.PAGE_SIZE);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<AttentionEntity> jsonCallback = new JsonCallback<AttentionEntity>() {

        @Override
        public void onSuccess(AttentionEntity result) {
            if (result == null || result.data == null) {
                showFailLayout();
                return;
            }
            fetchData(result);
            hideLoadingBar();
            refreshXListView(page, count);
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            hideLoadingBar();
            refreshXListView(page, count);
        }
    };

    class MineAttentionAdapter extends SimpleBaseAdapter<Attentions> {

        public MineAttentionAdapter(Context context, List<Attentions> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.addcircle_second_listitem;
        }

        @Override
        public View getItemView(final int position, View convertView, ViewHolder holder) {
            final Attentions mAttentions = data.get(position);
            TextView nameTv = holder.getView(R.id.secondlistitem_name_tv);
            TextView subtitleTv = holder.getView(R.id.secondlistitem_subtitle_tv);
            ImageView iv = holder.getView(R.id.secondlistitem_iv);
            final ImageView joinIv = holder.getView(R.id.secondlistitem_join_iv);
            final TextView cancelTv = holder.getView(R.id.secondlistitem_cancel_tv);
            imageLoader.displayImage(mAttentions.icon, iv);
            nameTv.setText(mAttentions.username);
            subtitleTv.setText(mAttentions.signature);

            Log.i("---->is_attention==" + mData.get(position).is_attention);
            if (touid == 0) {
                if (mData.get(position).is_attention == 1) {
                    joinIv.setVisibility(View.GONE);
                    cancelTv.setVisibility(View.VISIBLE);
                } else {
                    joinIv.setVisibility(View.VISIBLE);
                    cancelTv.setVisibility(View.GONE);
                }
            }

            joinIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    is_attention = 1;
                    attentionTouid = mAttentions.uid;
                    setAttention(joinIv, cancelTv, position);
                }
            });
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    is_attention = 0;
                    attentionTouid = mAttentions.uid;
                    setAttention(joinIv, cancelTv, position);
                }
            });
            return convertView;
        }

        public void setAttention(ImageView joinIv, TextView cancelTv, int position) {
            /*
			 * if (cancelTv.getVisibility() == View.VISIBLE) {
			 * cancelTv.setVisibility(View.GONE);
			 * joinIv.setVisibility(View.VISIBLE);
			 * mData.get(position).is_attention = 0; } else {
			 * cancelTv.setVisibility(View.VISIBLE);
			 * joinIv.setVisibility(View.GONE); mData.get(position).is_attention
			 * = 1; }
			 */
            mjoinIv = joinIv;
            mcancelTv = cancelTv;
            mposition = position;
            requestAttentionUrl();
        }
    }

    protected void fetchData(AttentionEntity result) {
        try {
            count = result.data.attentions.size();
            List<Attentions> attentions = result.data.attentions;
            if (page == 1) {
                mData.clear();
                mData.addAll(attentions);
                mAdapter = new MineAttentionAdapter(mContext, attentions);
                mListView.setAdapter(mAdapter);
            } else {
                mData.addAll(attentions);
                mAdapter.addAll(attentions);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestUrl();
    }

    @Override
    public void onLoadMore() {
        if (refreshXListView(count)) {
            return;
        }
        page++;
        requestUrl();
    }

    @Override
    public void requestAgain() {
        super.requestAgain();
        onRefresh();
    }

    /**
     * 关注/取消用户
     */
    private void requestAttentionUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        if (is_attention == 0) {
            encryptInfo.setCallback("user.cancel_attention");
        } else if (is_attention == 1) {
            encryptInfo.setCallback("user.pay_attention");
        }
        encryptInfo.resetParams();
        encryptInfo.putParam("touid", attentionTouid);
        request.setCallback(jsonAttentionCallback);
        request.execute();
    }

    JsonCallback<BaseEntity> jsonAttentionCallback = new JsonCallback<BaseEntity>() {

        @Override
        public void onSuccess(BaseEntity result) {
            if (result != null) {
                toast(result.errMessage);
                if (result.errCode == 0) {
                    resul_ok = true;
                    if (is_attention == 0) {
                        mcancelTv.setVisibility(View.GONE);
                        mjoinIv.setVisibility(View.VISIBLE);
                        mData.get(mposition).is_attention = 0;
                    } else if (is_attention == 1) {
                        mcancelTv.setVisibility(View.VISIBLE);
                        mjoinIv.setVisibility(View.GONE);
                        mData.get(mposition).is_attention = 1;
                    }
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        position = position - 1;
        if (mData != null && mData.size() > position) {
            Intent intent = new Intent(this, TaPeopleActivity.class);
            intent.putExtra("uid", mData.get(position).uid);
            intent.putExtra("username", mData.get(position).username);
            startActivity(intent);
        }
    }

}
