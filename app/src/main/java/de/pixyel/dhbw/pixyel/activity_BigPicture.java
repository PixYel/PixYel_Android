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

import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;
import de.pixyel.dhbw.pixyel.ConnectionManager.XML;

public class activity_BigPicture extends Activity {

    private EditText textField;
    private String comment;
    private ListView commentListView;
    private ImageView bigImage;
    public static LinkedList<String> commentList;
    public static final String KEY = "Picture";
    String uri;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__big_picture);

        textField = (EditText) findViewById(R.id.Input);
        commentListView = (ListView) findViewById(R.id.CommentList);
        bigImage = (ImageView) findViewById(R.id.BigImage);

        if (getIntent().hasExtra(KEY)) {
            id = getIntent().getStringExtra(KEY);
            uri = MainActivity.cacheFolder + "/" + id + ".jpg";
            Glide.with(activity_BigPicture.this).load(uri).into(bigImage);
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + KEY);
        }
        commentList = PicComment.hm.get(id);
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
                    XML xml = XML.createNewXML("addComment");
                    xml.addChild("id").setContent(id);
                    xml.addChild("content").setContent(comment);
                    ConnectionManager.sendToServer(xml);
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
