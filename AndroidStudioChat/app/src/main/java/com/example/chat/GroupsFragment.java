package com.example.chat;

import android.content.Intent;
import android.icu.text.Edits;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment
{

    private View groupFragmentView ;
    private ListView listView ;
    private ArrayAdapter<String> arrayAdapter ;
    private ArrayList<String> groupList = new ArrayList<>() ;

    private DatabaseReference groupsReference ;

    public GroupsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);

        groupsReference = FirebaseDatabase.getInstance().getReference().child("Groups") ;

        InitializeFields() ;

        RetrieveAndDisplayGroups() ;

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
       {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id)
           {
               String groupname = parent.getItemAtPosition(position).toString() ;
               Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class) ;
               groupChatIntent.putExtra("Groupname", groupname) ;
               startActivity(groupChatIntent);

           }
       });

        return groupFragmentView ;
    }

    private void RetrieveAndDisplayGroups()
    {
        groupsReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>() ;

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    set.add(Objects.requireNonNull(snapshot.getKey()).toString());

                groupList.clear();
                groupList.addAll(set) ;
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        }) ;
    }

    private void InitializeFields()
    {
        listView = groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, groupList) ;
        listView.setAdapter(arrayAdapter) ;
    }
}
