package com.example.internapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private RecyclerView contactsRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsRecView = findViewById(R.id.contactsRecView);

        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new Contact("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new Contact("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new Contact("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new Contact("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new Contact("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new Contact("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new Contact("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));


        ContactsRecViewAdapter adapter = new ContactsRecViewAdapter(this);
        adapter.setContacts(contacts);
        contactsRecView.setAdapter(adapter);
        contactsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


    }
}