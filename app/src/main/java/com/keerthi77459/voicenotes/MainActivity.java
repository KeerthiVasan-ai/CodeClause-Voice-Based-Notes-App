package com.keerthi77459.voicenotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNotes;
    RecyclerView recycle;
    NoteAdapter adapter;
    ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNotes = findViewById(R.id.addNotes);
        recycle = findViewById(R.id.recycle);
        menuButton = findViewById(R.id.menu);

        loadNotes();

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });

        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NotesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuButton);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle() == "Logout"){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this,LoginActivity2.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void loadNotes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query  = FirebaseFirestore.getInstance().collection("notes")
                .document(currentUser.getUid()).collection("my_notes")
                .orderBy("content", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> option = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();

        recycle.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new NoteAdapter(option,this);
        recycle.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}












