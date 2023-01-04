package com.example.umlife;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.message.ChatRoomActivity;
import com.example.model.EventInfo;
import com.example.model.Review;
import com.example.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EventDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventDetailFragment newInstance(String param1, String param2) {
        EventDetailFragment fragment = new EventDetailFragment();
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
    EventInfo eventInfo;
    List<UserInfo> userInfoList = new ArrayList<>();
    List<Review> reviewList = new ArrayList<>();
    UserInfo userInfo;
    Button btnContact;
    CardView organiserImage;
    Button btnJoin;
    FragmentActivity fragmentActivity;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        btnContact = view.findViewById(R.id.BtnEventDetailContact);
        btnJoin = view.findViewById(R.id.BtnEventDetailJoin);

        organiserImage = view.findViewById(R.id.OrganiserCardView);

        ImageView EventDetailImage = view.findViewById(R.id.IVEventDetailImage);
        TextView EventDetailName = view.findViewById(R.id.TVEventDetailTitle);
        ImageView OrganiserImage = view.findViewById(R.id.IVEventDetailOrganiser);
        TextView OrganiserName = view.findViewById(R.id.TVOrganiserName);
        TextView EventDetailDate = view.findViewById(R.id.TVEventDetailDate);
        TextView EventDetailVenue = view.findViewById(R.id.TVEventDetailVenue);
        TextView EventDetailRegistrationDate = view.findViewById(R.id.TVEventDetailRegistrationDate);
        RatingBar EventDetailRating = view.findViewById(R.id.EventDetailRatingBar);
        TextView Rating = view.findViewById(R.id.TVEventDetailRating);
        TextView EventDetailInfo = view.findViewById(R.id.TVEventDetailInfo);
        TextView AppBarEventName = view.findViewById(R.id.appBarEventName);

        db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    userInfoList.clear();
                    for(DocumentSnapshot d : list){
                        String id = d.getId();
                        UserInfo user = d.toObject(UserInfo.class);
                        user.setUuid(id);
                        userInfoList.add(user);
                        if(id.equals(eventInfo.getUuid())){
                            userInfo = d.toObject(UserInfo.class);
                            OrganiserName.setText(userInfo.getUsername());
                            Picasso.get().load(userInfo.getProfileImage()).into(OrganiserImage);
                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(), "No data fetched", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });

        db.collection("reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    reviewList.clear();
                    for(DocumentSnapshot d : list){
                        Review review = d.toObject(Review.class);
                        db.collection("users").document(review.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.getString("profileImage")!= null){
                                        //Toast.makeText(getActivity(),temp.toString(),Toast.LENGTH_LONG).show();
                                        review.setUserImage(document.getString("profileImage"));
                                        //profilePicture.setImageURI(temp);
                                    }
                                }
                            }
                        });
                        review.setReviewId(d.getId());
                        if(review.getOrganiserId().equals(eventInfo.getUuid()))
                            reviewList.add(review);
                    }
                }
                else{
                    Toast.makeText(getContext(), "No data fetched", Toast.LENGTH_SHORT).show();
                }
                double overallRating = 0;
                for(int i=0; i<reviewList.size(); i++){
                    overallRating+=reviewList.get(i).getRating();
                }
                overallRating /= reviewList.size();
                EventDetailRating.setRating(Float.parseFloat(String.valueOf(overallRating)));
                Rating.setText(String.format("%.2f", overallRating));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });


        Picasso.get().load(eventInfo.getmImageUrl()).into(EventDetailImage);
        AppBarEventName.setText(eventInfo.getEventName());
        EventDetailName.setText(eventInfo.getEventName());
        EventDetailDate.setText(eventInfo.getEventDate());
        EventDetailVenue.setText(eventInfo.getEventVenue());
        EventDetailRegistrationDate.setText(String.format("%s - %s", eventInfo.getOpenRegistration(), eventInfo.getEndRegistration()));
        EventDetailInfo.setText(eventInfo.getEventDetail());

        organiserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListAllReviewFragment listAllReviewFragment = new ListAllReviewFragment();
                listAllReviewFragment.setEvent(userInfo, reviewList, fragmentActivity);
                fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.container, listAllReviewFragment).addToBackStack(null).commit();
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoinEventFragment joinEventFragment = new JoinEventFragment();
                joinEventFragment.setEvent(eventInfo);
                fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.container, joinEventFragment).addToBackStack(null).commit();
            }
        });


        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ChatRoomActivity.class);
                intent.putExtra("user_name",userInfo.getUsername());
                intent.putExtra("room_name",eventInfo.getEventName());
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    public void setPosition(EventInfo eventInfo, FragmentActivity fragmentActivity){
        this.eventInfo = eventInfo;
        this.fragmentActivity = fragmentActivity;
    }
}