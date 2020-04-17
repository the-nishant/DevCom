package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindPeopleActivity extends AppCompatActivity
{
    private Toolbar toolbar ;
    private RecyclerView findPeopleRecyclerList ;

    private DatabaseReference usersReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        usersReference  = FirebaseDatabase.getInstance().getReference().child("Users") ;

        findPeopleRecyclerList = findViewById(R.id.find_people_recycler_list) ;
        findPeopleRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.find_people_toolbar) ;
        setSupportActionBar(toolbar) ;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find People");
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(usersReference, Contacts.class).build() ;


        FirebaseRecyclerAdapter<Contacts, FindPeopleViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindPeopleViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull FindPeopleViewHolder holder, final int position, @NonNull Contacts model)
            {
                holder.username.setText(model.getUsername());
                holder.status.setText(model.getStatus());
                //Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile_image).into(holder.profileImage) ;

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String userID = getRef(position).getKey();

                        Intent visitProfileIntent = new Intent(FindPeopleActivity.this, VisitProfileActivity.class) ;
                        visitProfileIntent.putExtra("UserID", userID) ;
                        startActivity(visitProfileIntent); ;
                    }
                });
            }

            @NonNull
            @Override
            public FindPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false) ;
                return new FindPeopleViewHolder(view);
            }
        } ;

        findPeopleRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindPeopleViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, status ;
        CircleImageView profileImage ;


        public FindPeopleViewHolder(@NonNull View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.username) ;
            status = itemView.findViewById(R.id.status) ;
            profileImage = itemView.findViewById(R.id.profile_image) ;

        }
    }


}
