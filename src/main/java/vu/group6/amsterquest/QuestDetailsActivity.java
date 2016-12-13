package vu.group6.amsterquest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class QuestDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_details);

        final Quest quest = getIntent().getParcelableExtra("quest");

        TextView titleText = (TextView) findViewById(R.id.title_text);
        ImageView questImage = (ImageView) findViewById(R.id.quest_image);
        TextView detailsText = (TextView) findViewById(R.id.details_text);
        TextView calenderText = (TextView) findViewById(R.id.calender_text);
        TextView rewardText = (TextView) findViewById(R.id.reward_text);
        Button startQuestButton = (Button) findViewById(R.id.start_quest_button);

        titleText.setText(quest.getTitleEN());
        Utils.setImageViewFromUrl(questImage, quest.getMedia().split(",")[0]);
        detailsText.setText(quest.getLongdescriptionEN());
        calenderText.setText(quest.getCalendarsummaryEN());
        rewardText.setText("Quest reward: " + quest.getReward() + " AmsterPoints");
        startQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questDetails = String.format("Start quest: %s\n%s\n%s\n%s", quest.getTitleEN(), quest.getAdres(), quest.getZipcode(), quest.getCity());
                Intent data = new Intent();
                data.putExtra("quest_details", questDetails);
                setResult(ChatActivity.REQUEST_QUEST, data);
                finish();
            }
        });
    }
}
