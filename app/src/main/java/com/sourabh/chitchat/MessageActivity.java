package com.sourabh.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editMessageInput;
    private TextView txtchattingWith;
    private ProgressBar progressBar;
    private ImageView imgToolbar,sendMessage;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;

    String userNameOfTheRoommate,emailOfRoommate,chatRoomId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        userNameOfTheRoommate=getIntent().getStringExtra("username_of_roommate");
        emailOfRoommate=getIntent().getStringExtra("email_of_roommate");

        recyclerView=findViewById(R.id.recyclerChat);
        editMessageInput=findViewById(R.id.edtText);
        txtchattingWith=findViewById(R.id.txtChatWindow);
        progressBar=findViewById(R.id.ChatProgressBar);
        imgToolbar=findViewById(R.id.img_toolbar);
        sendMessage=findViewById(R.id.imgSendMessage);

        messages=new ArrayList<>();
        txtchattingWith.setText(userNameOfTheRoommate);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("messages/"+chatRoomId).push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),emailOfRoommate,editMessageInput.getText().toString()));
                editMessageInput.setText("");
            }
        });

        messageAdapter=new MessageAdapter(messages,getIntent().getStringExtra("my_image"),getIntent().getStringExtra("img_of_roommate"),MessageActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("img_of_roommate")).placeholder(R.drawable.account_img).error(R.drawable.account_img).into(imgToolbar);

        setUpChatRoom();

    }

    private void setUpChatRoom(){

        FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String myUsername =snapshot.getValue(User.class).getUserName();
                if(userNameOfTheRoommate.compareTo(myUsername)>0){
                    chatRoomId=myUsername+userNameOfTheRoommate;
                }else if(userNameOfTheRoommate.compareTo(myUsername)==0){
                    chatRoomId=myUsername+userNameOfTheRoommate;
                }else{
                    chatRoomId=userNameOfTheRoommate+myUsername;
                }
                attachMessageListener(chatRoomId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void attachMessageListener(String chatRoomId){
        FirebaseDatabase.getInstance().getReference("messages/"+chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Message.class));
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}