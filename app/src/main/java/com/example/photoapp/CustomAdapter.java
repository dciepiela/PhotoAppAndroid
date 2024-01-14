package com.example.photoapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{
    private final Context context;
    //Activity activity; //refresh
    private final ArrayList<ModelRecord> recordsList;
    //Animation translate_anim;


    //db helper
    MyDatabaseHelper myDB;


    //constructor

    public CustomAdapter(Context context, ArrayList<ModelRecord> recordsList) {
        this.context = context;
        this.recordsList = recordsList;
        myDB = new MyDatabaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //get data, set data, handle view click in this method

        //get data
        ModelRecord modelRecord = recordsList.get(position);
        String id = modelRecord.getId();
        String title = modelRecord.getTitle();
        String image = modelRecord.getImage();
        String description = modelRecord.getDescription();
        String keyWords = modelRecord.getKeyWords();
        String addedTime = modelRecord.getAddedTime();
        String updatedTime = modelRecord.getUpdatedTime();
        String address = modelRecord.getAddress();

        //set data
        holder.title_text.setText(title);
        holder.description_text.setText(description);

        if(image.equals("null")){
            //no image
            holder.imageViewPhoto.setImageResource(R.drawable.ic_photo);

        } else{
            holder.imageViewPhoto.setImageURI(Uri.parse(image));
        }

        //handle item clicks (go to detail record activity)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecordDetailActivity.class);
            intent.putExtra("RECORD_ID", id);
            context.startActivity(intent);
        });

        //handle more button click listener (show options)
        holder.moreButton.setOnClickListener(v -> {
            //show options menu
            showMoreDialog(
                    ""+position,
                    ""+id,
                    ""+title,
                    ""+image,
                    ""+description,
                    ""+keyWords,
                    ""+addedTime,
                    ""+updatedTime,
                    ""+address
            );
        });
    }

    private void showMoreDialog(String position, final String id, final String title,final String image,
                                final String description, final String keyWords, final String addedTime,
                                final String updatedTime, final String address) {
        //options to display in dialog
        String[] options = {"Edytuj", "Usuń"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //add items to dialog
        builder.setItems(options, (dialog, which) -> {
            //handle item clicks
            if(which==0){
                //edit is clicked
                Intent intent = new Intent(context, AddAndUpdatePhotoActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("TITLE", title);
                intent.putExtra("IMAGE", image);
                intent.putExtra("DESCRIPTION", description);
                intent.putExtra("KEY_WORDS", keyWords);
                intent.putExtra("ADDED_TIME", addedTime);
                intent.putExtra("UPDATED_TIME", updatedTime);
                intent.putExtra("ADDRESS", address);
                intent.putExtra("isEditMode", true);
                context.startActivity(intent);

            }else if(which==1){
                //delete is clicked
                myDB.deleteRecordById(id);
                //refresh record by calling activities onResume method
                ((MainActivity)context).onResume();
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return recordsList.size(); //return size of list/number of records
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPhoto;
        TextView title_text, description_text;
        //LinearLayout mainLayout;
        ImageButton moreButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
            title_text = itemView.findViewById(R.id.title_text);
            description_text = itemView.findViewById(R.id.description_text);
            moreButton = itemView.findViewById(R.id.moreButton);

            //Animate Recyclerview
            //translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            //mainLayout.setAnimation(translate_anim);
        }
    }
}
