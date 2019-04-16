package com.example.admin.miplus.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.miplus.CustomXML.CircleProgressBar;
import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.StepsData;
import com.example.admin.miplus.fragment.FirstWindow.SleepInformationFragment;
import com.example.admin.miplus.fragment.FirstWindow.StepsInformationFragment;
import com.example.admin.miplus.pedometr.StepCounterService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirstFragment extends Fragment implements StepCounterService.CallBack {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();
    private StepsData stepsData = new StepsData();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CircleProgressBar circleProgressBar;
    private int steps;
    private StepCounterService stepCounterService;
    private List<StepsData> stepsDataList = new ArrayList<StepsData>();

    public void setSteps(int steps) {
        this.steps = steps;
        if (getView() != null) {
            TextView stepsText = (TextView) getView().findViewById(R.id.steps_cuantity_text);
            stepsText.setText(String.valueOf(steps));
            circleProgressBar = (CircleProgressBar) getView().findViewById(R.id.circle_progress_bar);
            circleProgressBar.progressChange(steps, profile.getStepsTarget());
            TextView cardDistanceText = (TextView) getView().findViewById(R.id.distance_card_text);
            float distance = ((((profile.getHeight() * 0.01f)/ 4) + 0.37f) * steps) * 0.001f;
            String formattedDouble = new DecimalFormat("#0.00").format(distance);
            cardDistanceText.setText(formattedDouble + " km");
        }
    }

    public static FirstFragment getInstance() {
        Bundle args = new Bundle();
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.first_activity, container, false);
        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            viewSetter(view);
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                            viewSetter(view);
                        }
                    });
        }

        dataBaseRepository.getStepsDataList()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            stepsDataList = task.getResult().toObjects(StepsData.class);
                            for (int i = 0; i < stepsDataList.size(); i++){
                                stepsData = stepsDataList.get(i);
                                if(stepsData.getSteps() > steps){
                                    steps = stepsData.getSteps();
                                    viewSetter(view);
                                }
                            }
                        } else {
                            stepsData.setDefaultInstance();
                            dataBaseRepository.setStepsData(stepsData);
                            viewSetter(view);
                        }
                    }
                });

        bindService();

        RelativeLayout stepsRelativeLayout = (RelativeLayout) view.findViewById(R.id.toStepsInformationCard);
        stepsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StepsInformationFragment stepsInformationFragment = new StepsInformationFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragments_container, stepsInformationFragment).addToBackStack(null).commit();
            }
        });

        RelativeLayout sleepRelativeLayout = (RelativeLayout) view.findViewById(R.id.toSleepInformationCard);
        sleepRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SleepInformationFragment sleepInformationFragment = new SleepInformationFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragments_container, sleepInformationFragment).addToBackStack(null).commit();
            }
        });

        RelativeLayout pulseRelativeLayout = (RelativeLayout) view.findViewById(R.id.toPulseInformationCard);
        pulseRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    public void viewSetter(View view) {
        TextView stepsText = (TextView) view.findViewById(R.id.steps_cuantity_text);
        circleProgressBar = (CircleProgressBar) view.findViewById(R.id.circle_progress_bar);
        TextView cardDistanceText = (TextView) view.findViewById(R.id.distance_card_text);

        if (stepsData != null && profile != null){
            stepsText.setText(String.valueOf(stepsData.getSteps()));
            circleProgressBar.progressChange(stepsData.getSteps(), profile.getStepsTarget());
            float distance = ((((profile.getHeight() * 0.01f)/ 4) + 0.37f) * stepsData.getSteps()) * 0.001f;
            String formattedDouble = new DecimalFormat("#0.00").format(distance);
            cardDistanceText.setText(formattedDouble + " km");
        }

    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            StepCounterService.MyBinder binder = (StepCounterService.MyBinder) service;
            stepCounterService = binder.getService();
            stepCounterService.setCallBack(FirstFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if (stepCounterService != null) stepCounterService.setCallBack(null);
        }
    };

    private void bindService() {
        Intent intent = new Intent(getActivity(), StepCounterService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        if (stepsData != null) {
            stepsData.setSteps(steps);
            stepsData.setDate(new Date());
            dataBaseRepository.setStepsData(stepsData);
        }
        super.onPause();
    }
}
