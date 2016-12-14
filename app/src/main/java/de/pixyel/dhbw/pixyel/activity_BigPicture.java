package de.pixyel.dhbw.pixyel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;
import java.util.List;

public class activity_BigPicture extends Activity {

    private EditText textField;
    private Uri uri;
    private String comment;
    private ListView commentListView;
    private ImageView bigImage;
    public static LinkedList<String> commentList;
    public static final String KEY = "Picture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__big_picture);

        textField = (EditText) findViewById(R.id.Input);
        commentListView = (ListView) findViewById(R.id.CommentList);
        commentList = new LinkedList<String>();
        bigImage = (ImageView) findViewById(R.id.BigImage);

        if (getIntent().hasExtra(KEY)) {
            String uri = getIntent().getStringExtra(KEY);
            Glide.with(activity_BigPicture.this).load(uri).into(bigImage);
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + KEY);
        }

        // Switch Activity
        final ImageButton Back = (ImageButton) findViewById(R.id.back);
        Back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(activity_BigPicture.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // On Click Comment Button, add to CommentList
        final ImageButton buttonComment = (ImageButton) findViewById(R.id.commit);
        buttonComment.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                comment = textField.getText().toString();
                if (!comment.isEmpty()){
                    commentList.add(comment);
                    addCommentToListView();
                    textField.setText("");
                }
            }
        });

    }

    // Create List View
    public void addCommentToListView(){
        String[] commentArray = commentList.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, commentArray);
        commentListView.setAdapter(adapter);
    }

    public void setComments(List<String> comments){
        String[] commentArray = comments.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, commentArray);
        commentListView.setAdapter(adapter);
    }

}
