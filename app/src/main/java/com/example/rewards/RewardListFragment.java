package com.example.rewards;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.callbacks.QueryCompleteCallback;
import com.example.model.Reward;
import com.example.model.UserInfo;
import com.example.umlife.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RewardListFragment extends Fragment {

    // Recycler View object
    RecyclerView RVRewards;

    List<Reward> rewards = new ArrayList<>();
    List<Reward> redeemedRewards = new ArrayList<>();
    List<String> redeemedRewardsName = new ArrayList<>();

    // Layout manager
    RecyclerView.LayoutManager RVRewardsLayoutManager;

    LinearLayoutManager VerticalLayout;

    FirebaseFirestore db;
    FirebaseUser curUser;
    UserInfo curUserInfo;

    RewardListAdapter rewardListAdapter;

    private int tabPosition;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RewardListFragment() {
        // Required empty public constructor
    }

    public RewardListFragment(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    public static RewardListFragment newInstance(String param1, String param2) {
        RewardListFragment fragment = new RewardListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reward_list, container, false);

        // Callback to update reward list
        QueryCompleteCallback<Reward> queryCompleteCallback = new QueryCompleteCallback<Reward>() {
            @Override
            public void onQueryComplete(List<Reward> list) {
                rewardListAdapter = new RewardListAdapter(getActivity(), rewards, tabPosition);
                RVRewards.setAdapter(rewardListAdapter);
            }
        };

        RVRewards = view.findViewById(R.id.rewardList);

        db = FirebaseFirestore.getInstance();

        // Get current user
        curUser = FirebaseAuth.getInstance().getCurrentUser();
        String curUserId = curUser.getUid();

        db.collection("users").document(curUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if(document.exists()) {
                        UserInfo userInfo = document.toObject(UserInfo.class);
                        redeemedRewardsName = userInfo.getRedeemedRewards();
                        curUserInfo = document.toObject(UserInfo.class);
                        redeemedRewardsName = curUserInfo.getRedeemedRewards();

                        db.collection("rewards").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();

                                    // Sometime reward get by user is null
                                    if (redeemedRewardsName == null)
                                        redeemedRewardsName = new ArrayList<>();

                                    if(!querySnapshot.isEmpty()) {
                                        if(tabPosition == 0) {
                                            for(DocumentSnapshot d : querySnapshot){
                                                if (!d.exists()) continue;
                                                Reward reward = d.toObject(Reward.class);
                                                if (!redeemedRewardsName.contains(reward.getRewardName()))
                                                    rewards.add(reward);
                                            }
                                        } else {
                                            for(DocumentSnapshot d : querySnapshot){
                                                if (!d.exists()) continue;
                                                Reward reward = d.toObject(Reward.class);
                                                if (redeemedRewardsName.contains(reward.getRewardName()))
                                                    redeemedRewards.add(reward);
                                            }
                                        }
                                        rewardListAdapter.notifyDataSetChanged();
                                    }

                                } else {
                                    Toast.makeText(getContext(), "Failed to fetch rewards", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to fetch rewards", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });

        RVRewardsLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RVRewards.setLayoutManager(RVRewardsLayoutManager);
        if (tabPosition == 0)
            rewardListAdapter = new RewardListAdapter(getActivity(), rewards, tabPosition);
        else
            rewardListAdapter = new RewardListAdapter(getActivity(), redeemedRewards, tabPosition);
        VerticalLayout = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        RVRewards.setLayoutManager(VerticalLayout);
        RVRewards.setAdapter(rewardListAdapter);

        return view;
    }
}