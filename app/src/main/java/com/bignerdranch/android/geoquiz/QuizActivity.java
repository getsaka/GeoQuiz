package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERS = "answers_array";
    private static final String KEY_CORRECT = "correct_answers";
    private static final String KEY_TOTAL = "total_answers";
    private static final String KEY_CHEATER = "cheater";
    private static final String KEY_CHEATS_ARRAY = "cheats_array";
    private static final String KEY_TOTAL_CHEATS = "total_cheats";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPreviousButton;
    private Button mCheatButton;
    /*****Challenge: From Button to ImageButton*****/
    /*private ImageButton mNextButton;
    private ImageButton mPreviousButton;*/

    private TextView mQuestionTextView;
    private int mCurrentIndex = 0;
    private double correctAnswers = 0;
    private boolean mIsCheater;
    private int mTotalCheats = 0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_australia, true)
    };
    private boolean[] mAnsweredQuestions = new boolean[mQuestionBank.length];
    private boolean[] mCheatedQuestions = new boolean[mQuestionBank.length];
    private int totalQuestionsAnswered = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState!=null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mAnsweredQuestions = savedInstanceState.getBooleanArray(KEY_ANSWERS);
            correctAnswers = savedInstanceState.getDouble(KEY_CORRECT);
            totalQuestionsAnswered = savedInstanceState.getInt(KEY_TOTAL);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER);
            mCheatedQuestions = savedInstanceState.getBooleanArray(KEY_CHEATS_ARRAY);
            mTotalCheats = savedInstanceState.getInt(KEY_TOTAL_CHEATS);
        }
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPreviousButton = (Button) findViewById(R.id.previous_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        /*****Challenge: From Button to ImageButton*****/
        /*mNextButton = (ImageButton) findViewById(R.id.next_button);
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);*/
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        /*****Challenge: Limited Cheats*****/
        if(mTotalCheats>2) {
            mCheatButton.setEnabled(false);
        }
        mTrueButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkAnswer(true);
                        disableButtons();
                    }
                }
        );
        mFalseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkAnswer(false);
                        disableButtons();
                        /*****Challenge: Make Toast appear on top of the screen******
                         Toast falseToast = Toast.makeText(QuizActivity.this,R.string.incorrect_toast,Toast.LENGTH_SHORT);
                         falseToast.setGravity(Gravity.TOP,0, 0);
                         falseToast.show();*/
                    }
                }
        );
        mNextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                        mIsCheater = false;
                        updateQuestion();
                    }
                }
        );
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex == 0) {
                    mCurrentIndex = mQuestionBank.length -1;
                }
                else{
                    mCurrentIndex = (mCurrentIndex -1)%mQuestionBank.length;
                }
                mIsCheater = false;
                updateQuestion();
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=CheatActivity.newIntent(QuizActivity.this,mQuestionBank[mCurrentIndex].isAnswerTrue());
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }
        });

        /*****Challenge: Add a Listener to the TextView*****/
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex +1)%mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });
        /*****Challenge: Add a Previous Button*****/
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT) {
            if(data == null) {
                return;
            }
        }
        mIsCheater = CheatActivity.wasAnswerShown(data);
        mCheatedQuestions[mCurrentIndex] = mIsCheater;
        /*****Challenge: Limited Cheats*****/
        if(mIsCheater) {
            mTotalCheats++;
        }
        if(mTotalCheats>2) {
            mCheatButton.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(KEY_INDEX,mCurrentIndex);
        outState.putBooleanArray(KEY_ANSWERS,mAnsweredQuestions);
        outState.putDouble(KEY_CORRECT,correctAnswers);
        outState.putInt(KEY_TOTAL,totalQuestionsAnswered);
        outState.putBoolean(KEY_CHEATER,mIsCheater);
        outState.putBooleanArray(KEY_CHEATS_ARRAY,mCheatedQuestions);
        outState.putInt(KEY_TOTAL_CHEATS,mTotalCheats);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        enableButtons();
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        mAnsweredQuestions[mCurrentIndex] = true;
        totalQuestionsAnswered++;

        if(mCheatedQuestions[mCurrentIndex] ==true) {
            messageResId = R.string.judgement_toast;
            correctAnswers++;
        }
        else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                correctAnswers++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        if (totalQuestionsAnswered == (mQuestionBank.length)) {
            calculatePercentage();
        }
    }

    private void disableButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private void enableButtons() {
        if (mAnsweredQuestions[mCurrentIndex]) {
            disableButtons();
        }
        else{
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void calculatePercentage() {
        Log.d(TAG, String.valueOf(correctAnswers));
        Log.d(TAG, String.valueOf(mQuestionBank.length));

        String percentage = String.valueOf((correctAnswers*100)/mQuestionBank.length);
        Log.d(TAG, String.valueOf(percentage));
        percentage = percentage.substring(0,percentage.indexOf(".")+2) + "%";
        String text = String.format(getResources().getString(R.string.percentage_toast),percentage);
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
}
