package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.salmi.bouchelaghem.studynet.Adapters.OnboardingAdapter;
import com.salmi.bouchelaghem.studynet.Models.OnboardingItem;
import com.salmi.bouchelaghem.studynet.R;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    // Views
    private Button btnGetStarted;
    private ViewPager2 onboardingViewPager;
    private LinearLayout layoutOnboardingIndicators;

    // Onboarding
    OnboardingAdapter adapter;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        setupOnboardingItems();
        initViews();
        setupOnboardingIndicators();

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setupCurrentOnboardingIndicator(position);
            }
        });


        btnGetStarted.setOnClickListener(v -> {
            if (onboardingViewPager.getCurrentItem() + 1 < adapter.getItemCount()) { // if we didn't reach the end yet
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1); // go to next item
            } else {
                startActivity(new Intent(OnboardingActivity.this, LoginActivity.class)); // take the user to another activity
                finish();
            }
        });
    }

    private void initViews() {
        btnGetStarted = findViewById(R.id.btnGetStarted);
        onboardingViewPager = findViewById(R.id.onBoardingViewPager);
        onboardingViewPager.setAdapter(adapter);
        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
    }

    void setupOnboardingItems() {
        List<OnboardingItem> itemsList = new ArrayList<>();

        OnboardingItem firstItem = new OnboardingItem();
        firstItem.setTitle(getString(R.string.onboarding_title1));
        firstItem.setDescription(getString(R.string.onboarding_description1));
        firstItem.setImage(R.drawable.get_started_vector1);

        OnboardingItem secondItem = new OnboardingItem();
        secondItem.setTitle(getString(R.string.onboarding_title2));
        secondItem.setDescription(getString(R.string.onboarding_description2));
        secondItem.setImage(R.drawable.get_started_vector2);

        OnboardingItem thirdItem = new OnboardingItem();
        thirdItem.setTitle(getString(R.string.onboarding_title3));
        thirdItem.setDescription(getString(R.string.onboarding_description3));
        thirdItem.setImage(R.drawable.get_started_vector3);

        itemsList.add(firstItem);
        itemsList.add(secondItem);
        itemsList.add(thirdItem);

        adapter = new OnboardingAdapter(itemsList);
    }

    private void setupOnboardingIndicators() {
        ImageView[] indicators = new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);

        }
    }

    private void setupCurrentOnboardingIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            }
        }
        if (index == adapter.getItemCount() - 1) {
            btnGetStarted.setText(R.string.get_started);
        } else {
            btnGetStarted.setText(R.string.next);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.click_back_again_msg), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}