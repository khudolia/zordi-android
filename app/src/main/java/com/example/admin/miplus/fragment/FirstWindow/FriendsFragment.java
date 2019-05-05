package com.example.admin.miplus.fragment.FirstWindow;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.admin.miplus.R;
import com.example.admin.miplus.adapter.FriendRowType;
import com.example.admin.miplus.adapter.AddFriendRowType;
import com.example.admin.miplus.adapter.ItemClickListener;
import com.example.admin.miplus.adapter.MyRecyclerViewAdapter;
import com.example.admin.miplus.adapter.RowType;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment implements ItemClickListener {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();

    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    List<RowType> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.friends_fragment, container, false);

        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            viewSetter(view);
            setHeaderContent(view);
            setCardContent(view);
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                            viewSetter(view);
                            setHeaderContent(view);
                            setCardContent(view);
                        }
                    });
        }

      /*  recyclerView = (RecyclerView) view.findViewById(R.id.friends_container);
        items.add(new FriendRowType());
        items.add(new AddFriendRowType());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyRecyclerViewAdapter(items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);*/

        initToolbar();
        return view;
    }

    private void viewSetter(View view) {
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayShowHomeEnabled(true);
    }

    private void setHeaderContent(View view) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final TextView name = (TextView) view.findViewById(R.id.user_name_google);
        final TextView email = (TextView) view.findViewById(R.id.user_email_google);
        final ImageView logo = (ImageView) view.findViewById(R.id.user_logo_google);
        if (getActivity() != null) {
            name.setText(mAuth.getCurrentUser().getDisplayName());
            email.setText(mAuth.getCurrentUser().getEmail());
            Glide.with(getActivity()).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(logo);
        }
    }

    private  void setCardContent(View view){
        if(getActivity() != null){
            ImageView logo = (ImageView) view.findViewById(R.id.friend_logo);
            Glide.with(getActivity()).load(R.drawable.photo_oleg).apply(RequestOptions.circleCropTransform()).into(logo);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.add_friend_container);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "You can add more friends in the future", Toast.LENGTH_LONG).show();
                }
            });
            SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.geoposition_switch);
            switchCompat.setChecked(profile.getShowGeoposition());
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    profile.setShowGeoposition(isChecked);
                    dataBaseRepository.setProfile(profile);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onClick(View view, int position) {
            AddFriendFragment addFriendFragment = new AddFriendFragment();
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragments_container, addFriendFragment).addToBackStack(null).commit();
    }


}