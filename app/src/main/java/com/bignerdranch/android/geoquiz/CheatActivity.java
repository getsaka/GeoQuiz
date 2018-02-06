package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.QuizActivity.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.QuizActivity.answer_shown";
    private static final String KEY_IS_CHEATER = "cheater";
    private static final String KEY_ANSWER = "answer";

    private boolean mAnswerIsrue;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mAPILevel;

    private boolean mIsCheater;
    private int mAnswer;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext,CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mAPILevel = (TextView) findViewById(R.id.api_level_text_view);

        if (savedInstanceState!=null) {
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER,false);
            if(mIsCheater) {
                mAnswer = savedInstanceState.getInt(KEY_ANSWER);
                mAnswerTextView.setText(mAnswer);
                setAnswerShownResult(mIsCheater);
            }
        }

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsCheater = true;
                if(mAnswerIsrue) {
                    mAnswer = R.string.true_button;
                }
                else {
                    mAnswer = R.string.false_button;
                }
                mAnswerTextView.setText(mAnswer);
                setAnswerShownResult(mIsCheater);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                }
                else{
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        /*****Challenge: Reporting the Build Version*****/
        int apiLevel = Build.VERSION.SDK_INT;
        mAPILevel.setText(String.format(getResources().getString(R.string.api_level_text),apiLevel));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_CHEATER,mIsCheater);
        outState.putInt(KEY_ANSWER,mAnswer);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown);
        setResult(RESULT_OK, data);
    }
}
