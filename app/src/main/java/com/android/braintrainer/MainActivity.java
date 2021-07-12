package com.android.braintrainer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    //Layouts
    RelativeLayout gameLayout;
    RelativeLayout mainLayout;
    GridLayout options;

    //TextViews
    TextView timerDisplay;
    TextView points;
    TextView resultDisplay;
    TextView question;
    TextView yourScoreTextView;

    //Buttons
    Button play;
    Button playAgain;

    Random numberGenerator;//used to generate all the random numbers in the game

    final long generalTimeLimitForTheGame = 60*1000;

    int number1,number2;//main numbers used in the game
    int boundForNumber1 = 10000,boundForNumber2 = 10000;
    long timeChosen = generalTimeLimitForTheGame;//time chosen TODO -- may become variable in the future version (use SeekBar distinct)
    long currentTime;




    //Timers
    CountDownTimer timer;
    CountDownTimer correctWrong;
    //invokes the game timer
    private void startTimer(){
        timer = new CountDownTimer(timeChosen + 1000, 1000) {
            @Override
            public void onTick(long l) {
                currentTime = l;
                timerDisplay.setText(formatTime(l));
                if(currentTime < 1000){
                    timer.onFinish();
                    timer.cancel();
                }
            }

            @Override
            public void onFinish() {

                clearQuestions();
                optionsNotNeededIs(true);
                setTimeChosen(generalTimeLimitForTheGame);

                //GAME OVER --code
                if(playAgain.getVisibility() == View.GONE) {
                    showScoreTextView(true);
                    timerDisplay.setText("00:00");
                    playAgain.setVisibility(View.VISIBLE);
                    animateButton(300, playAgain);
                    setTextAt(resultDisplay, 0, 0);
                    resultDisplay.setTextColor(getColor(R.color.questionColor));
                    resultDisplay.setText("GAME OVER!");
                    animateText(resultDisplay, 1, 1, 300);
                }
            }
        }.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Layouts
        gameLayout = findViewById(R.id.gameLayout);
        mainLayout = findViewById(R.id.mainLayout);
        options = findViewById(R.id.options);

        //TextViews
        points = findViewById(R.id.points);
        timerDisplay = findViewById(R.id.timer);
        resultDisplay = findViewById(R.id.resultPerQ);
        question = findViewById(R.id.question);
        yourScoreTextView = findViewById(R.id.yourScoreTextView);

        //Buttons
        play = findViewById(R.id.playButton);
        playAgain = findViewById(R.id.playAgain);

        //Random number initialized
        numberGenerator = new Random();



        correctWrong = new CountDownTimer(300,300) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                animateText(resultDisplay,0,0,100);
                startTimer();
                updateQuestion();
            }
        };


        initializeUI();
    }


    //on click method for play button
    public void setPlay(View v){
        mainLayout.setVisibility(View.GONE);
        setGameLayoutAt(0,0.5f);
        animateGame(1,300);
        resetGame();
    }
    //on click method for play again button
    public void setPlayAgain(View v){
        animateGame(200,300);
        resetGame();
    }
    //invokes animate game layout method
    public void backButton(View v){
        animateGameOut(200);
        resetGame();
    }
    //option click method
    public void setOptions(View view) {
        optionsNotNeededIs(true);
        Button option = (Button) view;
        setTextAt(resultDisplay,0,0);
        if(option.getText().toString().equals(Integer.valueOf(number1+number2).toString())){
            resultDisplay.setTextColor(getColor(R.color.correctAnswer));
            resultDisplay.setText("CORRECT!");
            updatePoints(true);
        }else{
            resultDisplay.setTextColor(getColor(R.color.wrongAnswer));
            resultDisplay.setText("WRONG!");
            updatePoints(false);
        }
        animateText(resultDisplay,1,1,100);

        timer.cancel();
        if(timeChosen < 1000) {
            correctWrong.onFinish();
            timer.onFinish();
            timer.cancel();
        }
        else{
            timeChosen = currentTime-1300;
            correctWrong.start();
        }
    }


    //formats time to the proper representation
    private String formatTime(long timeInMilliSeconds){
        timeInMilliSeconds/=1000;
        return timeCorrection(timeInMilliSeconds/60) +":"+ timeCorrection(timeInMilliSeconds%60);
    }
    //corrects single digit time
    private String timeCorrection(long time){
        String timeS = time +"";
        if(timeS.length()==1)
            timeS = "0" + timeS;
        return timeS;
    }





    //the functions together form and update a new question
    private void updateQuestion(){
        number1 = numberGenerator.nextInt(boundForNumber1);
        number2 = numberGenerator.nextInt(boundForNumber2);
        question.setText(number1+" + "+number2);
        updateOptions();
    }
    private void updateOptions(){
        optionsNotNeededIs(false);
        String pos = numberGenerator.nextInt(options.getChildCount())+"";
        for(int i=0;i<options.getChildCount();i++){
            Button option = (Button) options.getChildAt(i);
            if(option.getTag().toString().equals(pos)){
                option.setText(Integer.valueOf(number1+number2).toString());
            }
            else{
                int number = numberGenerator.nextInt(number1 + number2 + 2000);
                while(number == number1+number2){
                    number = numberGenerator.nextInt(number1 + number2 + 2000);
                }
                option.setText(number+"");
            }
        }
    }
    //updates Points based on correct or wrong
    private void updatePoints(boolean correct){
        String[] point = points.getText().toString().split("/",2);
        if(correct){
            point[0] = Integer.valueOf(Integer.parseInt(point[0])+1).toString();
        }
        point[1] = Integer.valueOf(Integer.parseInt(point[1])+1).toString();
        Log.i("TAG",point[0]+point[1]);
        points.setText(point[0]+"/"+point[1]);
    }








    //initializes the UI components to default
    private void initializeUI(){
        gameLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        playAgain.setVisibility(View.GONE);
        animateButton(600,play);
        showScoreTextView(false);
    }
    //sets the game layout at a certain point, mainly considering scales x and y, and alpha
    private void setGameLayoutAt(float value,float alpha){
        gameLayout.setScaleX(value);
        gameLayout.setScaleY(value);
        gameLayout.setAlpha(alpha);
    }
    private void resetGame(){
        setTimeChosen(generalTimeLimitForTheGame);
        points.setText(R.string.game_points_default);
        setTextAt(resultDisplay,0,0);
        showScoreTextView(false);
    }
    //sets TextView at desired pos and alpha
    private void setTextAt(TextView text,float value1,float value2){
        text.setScaleX(value1);
        text.setScaleY(value1);
        text.setAlpha(value2);
    }
    //sets the time chosen
    private void setTimeChosen(long value){
        timeChosen = value;
    }



    private void clearQuestions(){
        question.setText("");
        for(int i=0;i<options.getChildCount();i++){
            Button option = (Button) options.getChildAt(i);
            option.setText("");
        }
    }
    private void optionsNotNeededIs(boolean notNeeded){
        for(int i=0;i<options.getChildCount();i++){
            Button option = (Button) options.getChildAt(i);
            option.setEnabled(!notNeeded);
        }
    }
    private void showScoreTextView(boolean show){
        if(show){
            yourScoreTextView.setVisibility(View.VISIBLE);
            setTextAt(yourScoreTextView,0,0.5f);
            animateText(yourScoreTextView,1,1,300);
        }
        else{
            yourScoreTextView.setVisibility(View.GONE);
            setTextAt(yourScoreTextView,0,0.5f);
        }
    }





    //animates any button in
    private void animateButton(long duration, Button btn){
        btn.setAlpha(0.5f);
        btn.setScaleX(0);
        btn.setScaleY(0);
        btn.animate().scaleX(1).scaleY(1).alpha(1).setDuration(duration);
    }
    //animates the game layout in while removing the main layout, and invokes the start of the game
    private void animateGame(final long duration1, final long duration2){
        gameLayout.setVisibility(View.VISIBLE);
        CountDownTimer animate = new CountDownTimer(duration1*2-10,duration1) {
            @Override
            public void onTick(long l) {
                gameLayout.animate().scaleX(0).scaleY(0).alpha(0.5f).setDuration(duration1);
            }

            @Override
            public void onFinish() {
                playAgain.setVisibility(View.GONE);

                updateQuestion();

                gameLayout.animate().scaleX(1).scaleY(1).alpha(1).setDuration(duration2);
                startTimer();
            }
        }.start();
    }
    //animates the game layout out and displays the main layout by initializing the UI
    private void animateGameOut(final long duration1){
        CountDownTimer animate = new CountDownTimer(duration1*2,duration1) {
            @Override
            public void onTick(long l) {
                gameLayout.animate().scaleX(0).scaleY(0).alpha(0.5f).setDuration(duration1);
                timer.cancel();
            }

            @Override
            public void onFinish() {
                initializeUI();
                setGameLayoutAt(1,1);
            }
        }.start();
    }
    //animates the desired text to the desired scale and alpha
    private void animateText(TextView text,float value1,float value2,long duration){
        text.animate().scaleX(value1).scaleY(value1).alpha(value2).setDuration(duration);
    }
}











//TESTING
                /*CountDownTimer animator = new CountDownTimer(605,300) {
                    @Override
                    public void onTick(long l) {
                        gameLayout.animate().scaleY(0).scaleX(0).setDuration(200);
                    }

                    @Override
                    public void onFinish() {
                        gameLayout.setVisibility(View.GONE);
                    }
                }.start();*/