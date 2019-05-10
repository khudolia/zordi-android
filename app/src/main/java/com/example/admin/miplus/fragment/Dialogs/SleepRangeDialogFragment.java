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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SleepRangeDialogFragment extends DialogFragment implements View.OnClickListener {
    View view;
    private Date startSleep;
    private Date endSleep;
    private PushSleepTarget pushSleepTarget;
    private TextView textView1;
    private TextView textView2;
    private TextView sleepDistance;
    private CircleAlarmTimerView circleAlarmTimerView;
    private float startRadian;
    private float endRadian;

    @SuppressLint("ValidFragment")
    public SleepRangeDialogFragment(Date sleepTarget, Date startSleep, Date endSleep, float startRadian, float endRadian, PushSleepTarget pushSleepTarget) {
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
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view = inflater.inflate(R.layout.sleep_dialog, null);
        view.findViewById(R.id.ok_button_picker).setOnClickListener(this);
        textView1 = view.findViewById(R.id.start);
        textView2 = view.findViewById(R.id.end);

        textView1.setText(new SimpleDateFormat("HH:mm").format(startSleep));
        textView2.setText(new SimpleDateFormat("HH:mm").format(endSleep));
        initView();

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
            pushSleepTarget.sleepTarget(sleepLongDate(startSleep, endSleep));
        }
        dismiss();
    }

    private void initView() {

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        sleepDistance = view.findViewById(R.id.sleep_distance);
        circleAlarmTimerView = view.findViewById(R.id.circle_timer_picker);
        circleAlarmTimerView.setmCurrentRadian1(startRadian);
        circleAlarmTimerView.setmCurrentRadian(endRadian);
        circleAlarmTimerView.setOnTimeChangedListener(new CircleAlarmTimerView.OnTimeChangedListener() {
            @Override
            public void start(String starting) {
                try {
                    startSleep = simpleDateFormat.parse(starting);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                sleepDistance.setText(new SimpleDateFormat("HH:mm").format(sleepLongDate(startSleep, endSleep)));
                textView1.setText(simpleDateFormat.format(startSleep));
            }

            @Override
            public void end(String ending) {
                if("00:00".compareTo(ending) != 0){
                    try {
                        endSleep = simpleDateFormat.parse(ending);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                sleepDistance.setText(new SimpleDateFormat("HH:mm").format(sleepLongDate(startSleep, endSleep)));
                textView2.setText(simpleDateFormat.format(endSleep));
            }

        });

        //sleepDistance.setText(new SimpleDateFormat("HH:mm").format(sleepLongDate(startSleep, endSleep)));
    }

    private Date sleepLongDate(Date startSleep, Date endSleep) {
        Date date = new Date();
        if(startSleep != null && endSleep != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            if(startSleep.getTime() >= endSleep.getTime()){
                date.setTime(75600000 - startSleep.getTime() + endSleep.getTime());
                Log.d("<><><><>",   "start " + startSleep.getTime() + "  " + simpleDateFormat.format(startSleep.getTime()) + "   end "+ endSleep.getTime() + "  " + simpleDateFormat.format(endSleep.getTime())  + "   range " + (86400000 - startSleep.getTime() + endSleep.getTime()) + "  " + simpleDateFormat.format(86400000 - startSleep.getTime() + endSleep.getTime()));
            } else{
                if(startSleep.getTime() < endSleep.getTime()){
                    date.setTime(endSleep.getTime() - startSleep.getTime() - 10800000);
                    Log.d("<><><><>",   "start " + startSleep.getTime() + "  " + simpleDateFormat.format(startSleep.getTime()) + "   end "+ endSleep.getTime() + "  " + simpleDateFormat.format(endSleep.getTime())  + "   range " + (endSleep.getTime() - startSleep.getTime()) + "  " + simpleDateFormat.format(endSleep.getTime() - startSleep.getTime()));
                }
            }
            return date;
        } else{
            return date;
        }
    }

    public interface PushSleepTarget {
        void sleepTarget(Date sleepTarget);

        void startSleep(Date startSleep);

        void endSleep(Date endSleep);

        void startRadian(float startRadian);

        void endRadian(float endRadian);
    }
}
