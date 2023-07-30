package com.keerthi77459.voicenotes;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note,NoteAdapter.NoteViewHolder> {
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.titleView.setText(note.title);
        holder.contentView.setText(note.content);

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context,NotesActivity.class);
            intent.putExtra("title",note.title);
            intent.putExtra("content",note.content);
            String notesId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("notesId",notesId);
            context.startActivity(intent);
        });

        holder.deleteNotes.setOnClickListener((v) -> {
            String notesId = this.getSnapshots().getSnapshot(position).getId();
            DocumentReference documentReference;
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            documentReference = FirebaseFirestore.getInstance().collection("notes")
                    .document(currentUser.getUid()).collection("my_notes").document(notesId);
            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //Toast.makeText(Application.getContext(),"Saved Successfully",Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(NotesActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout,parent,false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView titleView , contentView;
        ImageButton deleteNotes;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
            contentView = itemView.findViewById(R.id.contentView);
            deleteNotes = itemView.findViewById(R.id.deleteNotes);
        }
    }
}
