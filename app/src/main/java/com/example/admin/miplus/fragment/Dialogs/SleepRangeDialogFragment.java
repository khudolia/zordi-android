package com.example.admin.miplus.fragment.Dialogs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.miplus.CustomXML.CircleAlarmTimerView;
import com.example.admin.miplus.R;
import com.example.admin.miplus.fragment.ThirdFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SleepRangeDialogFragment extends DialogFragment implements View.OnClickListener {
    View view;
    private String sleepTarget;
    private String startSleep;
    private String endSleep;
    private PushSleepTarget pushSleepTarget;
    private TextView textView1;
    private TextView textView2;
    private TextView sleepDistance;
    private CircleAlarmTimerView circleAlarmTimerView;
    private float startRadian;
    private float endRadian;

    Date startSleepDate = null;
    Date endSleepDate = null;


    @SuppressLint("ValidFragment")
    public SleepRangeDialogFragment(String sleepTarget, String startSleep, String endSleep, float startRadian, float endRadian, PushSleepTarget pushSleepTarget) {
        this.sleepTarget = sleepTarget;
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.pushSleepTarget = pushSleepTarget;
        this.startRadian = startRadian;
        this.endRadian = endRadian;
    }

    public SleepRangeDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view = inflater.inflate(R.layout.sleep_dialog, null);
        view.findViewById(R.id.ok_button_picker).setOnClickListener(this);
        textView1 = (TextView) view.findViewById(R.id.start);
        textView2 = (TextView) view.findViewById(R.id.end);
//        textView1.setText(startSleep);
//        textView2.setText(endSleep);

        textView1.setText(startSleep);
        textView2.setText(endSleep);
        initView();
        //  circleAlarmTimerView.setStartRadian(startSleep, endSleep);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (pushSleepTarget != null) {
            startRadian = circleAlarmTimerView.getmCurrentRadian1();
            endRadian = circleAlarmTimerView.getmCurrentRadian();
            pushSleepTarget.startRadian(startRadian);
            pushSleepTarget.endRadian(endRadian);
            pushSleepTarget.startSleep(startSleep);
            pushSleepTarget.endSleep(endSleep);
            pushSleepTarget.sleepTarget(sleepLong(startSleep, endSleep));
        }
        dismiss();
    }

    private void initView() {


        sleepDistance = (TextView) view.findViewById(R.id.sleep_distance);
        circleAlarmTimerView = (CircleAlarmTimerView) view.findViewById(R.id.circle_timer_picker);
        circleAlarmTimerView.setmCurrentRadian1(startRadian);
        circleAlarmTimerView.setmCurrentRadian(endRadian);
        circleAlarmTimerView.setOnTimeChangedListener(new CircleAlarmTimerView.OnTimeChangedListener() {
            @Override
            public void start(String starting) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                try {
                    startSleepDate = simpleDateFormat.parse(starting);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sleepDistance.setText(sleepLongDate(startSleepDate, endSleepDate));
                textView1.setText(simpleDateFormat.format(startSleepDate));
            }

            @Override
            public void end(String ending) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                try {
                    endSleepDate = simpleDateFormat.parse(ending);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sleepDistance.setText(sleepLongDate(startSleepDate, endSleepDate));
                textView2.setText(simpleDateFormat.format(endSleepDate));
            }

        });
        sleepDistance.setText(sleepLongDate(startSleepDate, endSleepDate));
        textView2.setText(endSleep);
    }

    private String sleepLong(String startSleep, String endSleep) {
        String[] partStartSleep = startSleep.split(":");
        String[] partEndSleep = endSleep.split(":");
        if (Integer.parseInt(partEndSleep[0]) >= Integer.parseInt(partStartSleep[0])) {
            int differenceTime = Integer.parseInt(partEndSleep[0]) - Integer.parseInt(partStartSleep[0]);
            if (differenceTime < 10) {
                sleepTarget = "0" + String.valueOf(differenceTime);
            } else {
                sleepTarget = String.valueOf(differenceTime);
            }
        } else {
            int differenceTime = 24 - (Integer.parseInt(partStartSleep[0]) - Integer.parseInt(partEndSleep[0]));
            if (differenceTime < 10) {
                sleepTarget = "0" + String.valueOf(differenceTime);
            } else {
                sleepTarget = String.valueOf(differenceTime);
            }
        }

        if (Integer.parseInt(partEndSleep[1]) >= Integer.parseInt(partStartSleep[1])) {
            int differenceTime = Integer.parseInt(partEndSleep[1]) - Integer.parseInt(partStartSleep[1]);
            if (differenceTime < 10) {
                sleepTarget = sleepTarget + ":0" + differenceTime;
            } else {
                sleepTarget = sleepTarget + ":" + differenceTime;
            }
        } else {
            int differenceTime = 60 - (Integer.parseInt(partStartSleep[1]) + 1 - Integer.parseInt(partEndSleep[1]));
            if (differenceTime < 10) {
                if (Integer.parseInt(sleepTarget) - 1 < 10) {
                    sleepTarget = "0" + String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":0" + differenceTime;
                } else {
                    sleepTarget = String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":0" + differenceTime;
                }
            } else {
                if (Integer.parseInt(sleepTarget) - 1 != -1) {
                    if (Integer.parseInt(sleepTarget) - 1 < 10) {
                        sleepTarget = "0" + String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":" + differenceTime;
                    } else {
                        sleepTarget = String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":" + differenceTime;
                    }
                } else {
                    sleepTarget = "23:" + differenceTime;
                }

            }
        }
        return sleepTarget;
    }

    private String sleepLongDate(Date startSleep, Date endSleep) {
        if(startSleep != null && endSleep != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            if(startSleep.getTime() >= endSleep.getTime()){
                sleepTarget = simpleDateFormat.format(86400000 - startSleep.getTime() + endSleep.getTime());
            } else{
                if(startSleep.getTime() < endSleep.getTime()){
                    sleepTarget = simpleDateFormat.format( endSleep.getTime() - startSleep.getTime());
                }
            }
            return sleepTarget;
        } else{
            return "";
        }
    }

    public interface PushSleepTarget {
        void sleepTarget(String sleepTarget);

        void startSleep(String startSleep);

        void endSleep(String endSleep);

        void startRadian(float startRadian);

        void endRadian(float endRadian);
    }
}
