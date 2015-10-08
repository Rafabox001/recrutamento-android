package com.blackboxstudios.rafael.reclutamento_android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackboxstudios.rafael.reclutamento_android.adapter.EpisodeAdapter;
import com.blackboxstudios.rafael.reclutamento_android.network.TraktClient;
import com.blackboxstudios.rafael.reclutamento_android.objects.Episode;
import com.blackboxstudios.rafael.reclutamento_android.utils.Constants;
import com.blackboxstudios.rafael.reclutamento_android.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements ObservableScrollViewCallbacks {

    @Bind(R.id.list_background) View mListBackgroundView;
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.header) FrameLayout mHeader;
    @Bind(R.id.thumbnail) ImageView thumbnail;
    @Bind(R.id.season_cover) ImageView poster;
    @Bind(R.id.list)ObservableListView mListView;
    @Bind(R.id.header_rating)TextView mRating_textView;
    int mParallaxImageHeight;
    Context mContext;

    private AnimationSet set;
    private LayoutAnimationController controller;

    public List<Episode> episodesList = new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        set = new AnimationSet(true);
        Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.animation_list);
        animation.setDuration(1200);
        set.addAnimation(animation);

        controller = new LayoutAnimationController(set, 0.5f);

        mContext = getActivity();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.season);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mListView.setScrollViewCallbacks(this);
        mListView.setLayoutAnimation(controller);
        View paddingView = new View(getActivity());
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mParallaxImageHeight);
        paddingView.setLayoutParams(lp);
        paddingView.setClickable(true);
        mListView.addHeaderView(paddingView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.CYAN);
        }



        getConnection();

        return rootView;
    }

    private void getConnection() {
        if (Utils.isNetworkAvailable(getActivity())) {
            new GetRatingsAsync().execute();

            Glide.with(mContext).load(Constants.thumbnailImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(thumbnail);

            Glide.with(mContext).load(Constants.coverImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(poster);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.internet_message)
                    .setTitle(R.string.internet_title)
                    .setPositiveButton(R.string.internet_settings_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    WifiManager wifi;
                                    wifi = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
                                    wifi.setWifiEnabled(true);

                                    final ProgressDialog progressDialog;
                                    progressDialog = new ProgressDialog(mContext);
                                    progressDialog.setMessage(getString(R.string.internet_turning_wifiOn));
                                    progressDialog.setIndeterminate(false);
                                    progressDialog.setCancelable(false);
                                    progressDialog.getWindow().setGravity(Gravity.CENTER);
                                    progressDialog.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            getConnection();
                                            if(progressDialog.isShowing())
                                                progressDialog.dismiss();
                                        }
                                    }, 4000);
                                }
                            }
                    )
                    .setNegativeButton(R.string.internet_cancel_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().finish();
                                }
                            }
                    );
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }



    public void setAdapterList() {
        EpisodeAdapter listAdapter = new EpisodeAdapter(mContext, episodesList);
        mListView.setAdapter(listAdapter);
    }

    //region listView methods implementations
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mHeader, -scrollY / 2);
        // Translate list background
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mParallaxImageHeight));
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }
    //endregion


    public class GetEpisodesInfo extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(getString(R.string.get_episodes));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setGravity(Gravity.CENTER);
            progressDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            return TraktClient.getEpisodes();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            if (jsonArray.length() != 0 && jsonArray != null) {
                try {
                    jsonToList(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setAdapterList();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setMessage(getResources().getString(R.string.error_getting_data));
                alertDialog.setPositiveButton(R.string.internet_settings_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getConnection();
                    }
                });
                alertDialog.show();
            }
        }
    }

    public class GetRatingsAsync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(getString(R.string.get_rating));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setGravity(Gravity.CENTER);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return TraktClient.getRating();
        }

        @Override
        protected void onPostExecute(String rating) {
            super.onPostExecute(rating);
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            if (rating != null)
                mRating_textView.setText(rating);
            else
                mRating_textView.setText("N/A");

            new GetEpisodesInfo().execute();
            //jsonToList(jsonObject);
            //setAdapterList();

        }
    }
    private void jsonToList(JSONArray jsonArray) throws JSONException {
        if (jsonArray != null) {
            for(int position = 0; position < jsonArray.length(); position++) {
                Episode episode = new Episode();
                episode.setNumber(jsonArray.getJSONObject(position).get("number").toString());
                episode.setTitle(jsonArray.getJSONObject(position).get("title").toString());
                episode.setSeason(jsonArray.getJSONObject(position).get("season").toString());

                episodesList.add(episode);
            }
        }
    }
}
