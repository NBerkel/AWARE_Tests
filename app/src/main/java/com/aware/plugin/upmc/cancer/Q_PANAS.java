package com.aware.plugin.upmc.cancer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aware.Aware;
import com.aware.Aware_Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by nielsv on 09-27-2017.
 */

public class Q_PANAS extends AppCompatActivity {
    public long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_panas);

        // Update last notification status to "Opened"
        ContentValues vals = new ContentValues();
//        Cursor lastNotification = getApplicationContext().getContentResolver().query(Provider.Notification_data.CONTENT_URI, null, null, null, Provider.Notification_data.TIMESTAMP + " DESC LIMIT 1");
//        if( lastNotification != null && lastNotification.moveToFirst()) {
//            vals.put(Provider.Notification_data.STATUS, "Opened");
//            getContentResolver().update(Provider.Notification_data.CONTENT_URI, vals, Provider.Notification_data._ID + "=" + lastNotification.getInt(lastNotification.getColumnIndex(Provider.Notification_data._ID)), null);
//        }
//        if( lastNotification != null && ! lastNotification.isClosed() ) lastNotification.close();

        startTime = System.currentTimeMillis();

        fillPanas();

        Button Q1PANAS_buttonNext = (Button) findViewById(R.id.Q1PANAS_buttonNext);
        Q1PANAS_buttonNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });
    }

    ArrayList<RadioGroup> radioGroups = new ArrayList<>();

    private void fillPanas() {
        // Upset Hostile Alert Ashamed Inspired Nervous Determined Attentive Afraid Active

        ArrayList<Object> panasItems = new ArrayList<>();
        panasItems.add("Upset");
        panasItems.add("Hostile");
        panasItems.add("Alert");
        panasItems.add("Ashamed");
        panasItems.add("Inspired");
        panasItems.add("Nervous");
        panasItems.add("Determined");
        panasItems.add("Attentive");
        panasItems.add("Afraid");
        panasItems.add("Active");

        // Randomise order of questionnaire items
        Collections.shuffle(panasItems);

        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.PanasItemsLinearLayout);

        for (int i = 0; i < panasItems.size(); i++) {
            LinearLayout panasContainer = new LinearLayout(this);
            panasContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams panasContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            panasContainerParams.setMargins(0,0,0,50);
            panasContainer.setLayoutParams(panasContainerParams);

            RadioGroup panasRatingRadioGroup = new RadioGroup(this);
            panasRatingRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
            panasRatingRadioGroup.setGravity(Gravity.CENTER_HORIZONTAL);

            panasRatingRadioGroup.setContentDescription((CharSequence) panasItems.get(i));

            radioGroups.add(panasRatingRadioGroup);

            for (int j = 0; j < 5; j++) {
                RadioButton radioButton = new RadioButton(this);
                panasRatingRadioGroup.addView(radioButton);

                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (50 * scale + 0.5f);

                radioButton.getLayoutParams().width = pixels;
                radioButton.getLayoutParams().height = pixels;

                TypedValue typedValue = new TypedValue();
                this.getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorSingle, typedValue, true);
                if (typedValue.resourceId != 0) {
                    radioButton.setButtonDrawable(null);
                    radioButton.setBackgroundResource(typedValue.resourceId);
                }
            }

            if(i == 0) {
                // Legend
                LinearLayout panasLegendLL = new LinearLayout(this);
                panasLegendLL.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams panasLegendLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                panasLegendLL.setLayoutParams(panasLegendLLParams);

                TextView minLabel = new TextView(this);
                minLabel.setText(R.string.likertMin);
                minLabel.setTextSize(18);
                minLabel.setGravity(Gravity.LEFT);
                LinearLayout.LayoutParams minLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                minLabelParams.setMargins(56,0,0,20);
                minLabel.setLayoutParams(minLabelParams);

                panasLegendLL.addView(minLabel);

                TextView maxLabel = new TextView(this);
                maxLabel.setText(R.string.likertMax);
                maxLabel.setTextSize(18);
                maxLabel.setGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams maxLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                maxLabelParams.setMargins(0,0,56,20);
                maxLabel.setLayoutParams(maxLabelParams);

                panasLegendLL.addView(maxLabel);

                rootLayout.addView(panasLegendLL);
            }

            LinearLayout panasItemLL = new LinearLayout(this);
            panasItemLL.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams panasItemLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            panasItemLL.setLayoutParams(panasItemLLParams);

            TextView panasItem = new TextView(this);
            panasItem.setText((CharSequence) panasItems.get(i));
            panasItem.setTextSize(20);
            panasItem.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams panasItemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            panasItemParams.weight = 1;
            panasItem.setLayoutParams(panasItemParams);
            panasItemLL.addView(panasItem);

            panasContainer.addView(panasItemLL);
            panasContainer.addView(panasRatingRadioGroup);

            rootLayout.addView(panasContainer);

            if(i == panasItems.size() - 1) {
                // Legend
                LinearLayout panasLegendLL = new LinearLayout(this);
                panasLegendLL.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams panasLegendLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                panasLegendLL.setLayoutParams(panasLegendLLParams);

                TextView minLabel = new TextView(this);
                minLabel.setText(R.string.likertMin);
                minLabel.setTextSize(18);
                minLabel.setGravity(Gravity.LEFT);
                LinearLayout.LayoutParams minLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                minLabelParams.setMargins(56,0,0,20);
                minLabel.setLayoutParams(minLabelParams);

                panasLegendLL.addView(minLabel);

                TextView maxLabel = new TextView(this);
                maxLabel.setText(R.string.likertMax);
                maxLabel.setTextSize(18);
                maxLabel.setGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams maxLabelParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                maxLabelParams.setMargins(20,0,56,10);
                maxLabel.setLayoutParams(maxLabelParams);

                panasLegendLL.addView(maxLabel);

                rootLayout.addView(panasLegendLL);
            }
        }
    }

    private void nextQuestion() {
//        ArrayList<Class<?>> classes = Plugin.classes;

        // int id[i] = radioGroups.get(i).getCheckedRadioButtonId();

        // check if question has been answered
        int groupsCount = radioGroups.size();

        String collectedAnswers = "";

        for (int i=0;i<groupsCount;i++) {
            RadioGroup radioGroup = radioGroups.get(i);
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

            if (selectedRadioButtonId == -1) {
                //Snackbar incompleteSnackbar = Snackbar.make(findViewById(R.id.LayoutPANAS), "Please complete all fields.", Snackbar.LENGTH_SHORT);
                //incompleteSnackbar.show();

                return;
            } else {
                // Take modulus because ID can be any multiple of 10.
                Log.d("Niels", radioGroup.getContentDescription() + ":" + (Math.abs(selectedRadioButtonId - (i * 5)) % 10));
                collectedAnswers = collectedAnswers.concat(radioGroup.getContentDescription() + ":" + (Math.abs(selectedRadioButtonId - (i * 5)) % 10) + ";");
            }
        }

        Log.d("Niels", "start time: " + startTime);

        ContentValues panas_data = new ContentValues();
//        panas_data.put(Provider.Cognitive_data.START_TIME, Long.toString(startTime));
//        panas_data.put(Provider.Cognitive_data.TIMESTAMP, System.currentTimeMillis());
////        panas_data.put(Provider.Cognitive_data.TIMESTAMP,
//        panas_data.put(Provider.Cognitive_data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
//        panas_data.put(Provider.Cognitive_data.QUESTION_ID, Aware.getSetting(getApplicationContext(), Settings.QUESTION_ID));
//        panas_data.put(Provider.Cognitive_data.QUESTION_TYPE, "PANAS");
//        panas_data.put(Provider.Cognitive_data.ANSWER, collectedAnswers);
//        panas_data.put(Provider.Cognitive_data.CORRECTANSWER, "-");
//        panas_data.put(Provider.Cognitive_data.TODAYS_NOTIF_NR, Aware.getSetting(getApplicationContext(), Settings.TODAYS_NOTIF_NR));
//        panas_data.put(Provider.Cognitive_data.STUDY_DAY, Aware.getSetting(getApplicationContext(), Settings.STUDY_DAY));
//
//        //Insert the data to the ContentProvider
//        getContentResolver().insert(Provider.Cognitive_data.CONTENT_URI, panas_data);

//        if (classes.size() != 0) {
//            final int min = 0;
//            final int max = classes.size();
//            Random random = new Random();
//            int nextClass = random.nextInt(max - min) + min;
//
//            Log.d("Niels", String.valueOf(nextClass));
//
////            Intent intent = new Intent(this, Plugin.classes.get(nextClass));
////            startActivity(intent);
//        } else {
//            // All questions answered, time to exit
//            finish();
//        }
    }
}
