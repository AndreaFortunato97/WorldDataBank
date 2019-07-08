package it.apperol.group.worlddatabank.myfragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.Objects;

import it.apperol.group.worlddatabank.R;
import it.apperol.group.worlddatabank.myactivities.CountryActivity;
import it.apperol.group.worlddatabank.myactivities.TopicActivity;
import it.apperol.group.worlddatabank.myviews.MyTextView;

public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private View myView;
    private MyTextView myTvTitle, myTvChoose, myTvChooseInfo, myTvOffline;
    private MaterialButton mbCoArIn, mbArInCo, mbRefreshConnection;
    private boolean isTextViewClicked = false;
    public static int count;

    private Integer iStart, iEnd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.welcome_layout, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mbRefreshConnection = Objects.requireNonNull(getActivity()).findViewById(R.id.mbRefreshConnection);
        myTvTitle = Objects.requireNonNull(getActivity()).findViewById(R.id.myTvTitle);
        myTvChooseInfo = Objects.requireNonNull(getActivity()).findViewById(R.id.myTvChooseInfo);
        mbCoArIn = Objects.requireNonNull(getActivity()).findViewById(R.id.mbCoArIn);
        mbArInCo = Objects.requireNonNull(getActivity()).findViewById(R.id.mbArInCo);
        myTvOffline = Objects.requireNonNull(getActivity()).findViewById(R.id.myTvOffline);
        myTvChoose = Objects.requireNonNull(getActivity()).findViewById(R.id.myTvChoose);


        myTvTitle.setText(String.format("%s App", getResources().getString(R.string.app_title)));

        myTvTitle.measure(0,0);
        Shader textShader = new LinearGradient(0, (float)myTvTitle.getMeasuredHeight()/2, (float)myTvTitle.getMeasuredWidth(), (float)myTvTitle.getMeasuredHeight()/2,
                new int[]{
                        Color.parseColor("#F97C3C"),
                        Color.parseColor("#FDB54E"),
                        Color.parseColor("#64B678"),
                        Color.parseColor("#478AEA"),
                        Color.parseColor("#8446CC"),
                }, null, Shader.TileMode.CLAMP);
        myTvTitle.getPaint().setShader(textShader);
        myTvTitle.setTextColor(Color.parseColor("#FFFFFF"));

        underlineChoose();

        myTvChooseInfo.setOnClickListener(this);
        mbRefreshConnection.setOnClickListener(this);

        if(!isConnected()) {
            myTvChoose.setVisibility(View.INVISIBLE);
            myTvChooseInfo.setVisibility(View.INVISIBLE);
            mbCoArIn.setEnabled(false);
            mbArInCo.setEnabled(false);
            myTvOffline.setVisibility(View.VISIBLE);
            mbRefreshConnection.setVisibility(View.VISIBLE);
        }

        mbCoArIn.setOnClickListener(this);
        mbArInCo.setOnClickListener(this);
        mbRefreshConnection.setOnClickListener(this);
    }

    private void underlineChoose() {
        Spannable spannable = new SpannableString(getString(R.string.choose_info));
        iStart = 1;
        iEnd = iStart + 19;

        SpannableString ssText = new SpannableString(spannable);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));

        ssText.setSpan(foregroundColorSpan, iStart, iEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        myTvChooseInfo.setText(ssText);
        myTvChooseInfo.setMovementMethod(LinkMovementMethod.getInstance());
        myTvChooseInfo.setHighlightColor(Color.TRANSPARENT);
        myTvChooseInfo.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mbRefreshConnection:
                if(isConnected()) {
                    getActivity().recreate();
                }
                break;
            case R.id.mbCoArIn:
                if(isConnected()) {
                    count = 0;
                    Intent countryIntent = new Intent(getActivity(), CountryActivity.class);
                    startActivity(countryIntent);
                } else {
                    getActivity().recreate();
                }

                break;
            case R.id.mbArInCo:
                if(isConnected()) {
                    count = 1;
                    Intent topicIntent = new Intent(getActivity(), TopicActivity.class);
                    startActivity(topicIntent);
                } else {
                    getActivity().recreate();
                }
                break;
            case R.id.myTvChooseInfo:

                if(isTextViewClicked){
                    ObjectAnimator animation = ObjectAnimator.ofInt(
                            myTvChooseInfo,
                            "maxLines",
                            1);
                    animation.setDuration(300);
                    animation.start();
                    isTextViewClicked = false;
                } else {
                    ObjectAnimator animation = ObjectAnimator.ofInt(
                            myTvChooseInfo,
                            "maxLines",
                            myTvChooseInfo.getLineCount());
                    animation.setDuration(300);
                    animation.start();
                    isTextViewClicked = true;
                }
        }
    }

    private Boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
            return false;
    }
}
