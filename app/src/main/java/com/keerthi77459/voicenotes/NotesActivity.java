package com.keerthi77459.voicenotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import org.w3c.dom.Document;

import java.security.Timestamp;

public class NotesActivity extends AppCompatActivity {

    EditText notesTitle,notesContent;
    ImageButton saveNotes;
    FloatingActionButton voiceAssistant;
    TextView newNotesHeading;
    String editTitle,editContent,notesId;
    boolean EditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notesTitle = findViewById(R.id.notesTitle);
        notesContent = findViewById(R.id.notesContent);
        saveNotes = findViewById(R.id.saveNotes);
        voiceAssistant = findViewById(R.id.voiceAssistant);
        newNotesHeading = findViewById(R.id.newNotesHeading);

        editTitle = getIntent().getStringExtra("title");
        editContent = getIntent().getStringExtra("content");
        notesId = getIntent().getStringExtra("notesId");

        if(notesId != null && !notesId.isEmpty()){
            EditMode = true;
        }

        notesTitle.setText(editTitle);
        notesContent.setText(editContent);
        if(EditMode){
            newNotesHeading.setText("Edit Notes");
        }

        saveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToFirebase();
            }
        });

        voiceAssistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechToText(view);
            }
        });
    }

    private void speechToText(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,10);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Listening");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            String oldContent = notesContent.getText().toString();
            String newContent = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            String finalContent = oldContent +" "+newContent;
            notesContent.setText(finalContent);
        }
    }

    private void saveToFirebase() {
        String title = notesTitle.getText().toString();
        String content = notesContent.getText().toString();

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);

        saveFirebase(note);

    }
    private void saveFirebase(Note note){
        DocumentReference documentReference;

       if(EditMode){
           FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
           documentReference = FirebaseFirestore.getInstance().collection("notes")
                   .document(currentUser.getUid()).collection("my_notes").document(notesId);
       } else {
           FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
           documentReference = FirebaseFirestore.getInstance().collection("notes")
                   .document(currentUser.getUid()).collection("my_notes").document();
       }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NotesActivity.this,"Saved Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NotesActivity.this,MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(NotesActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
