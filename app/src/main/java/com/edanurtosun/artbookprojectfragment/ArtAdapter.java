package com.edanurtosun.artbookprojectfragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {
    ArrayList<String> artNameList;
    ArrayList<Integer> artIdList;



    public ArtAdapter(ArrayList<String> artNameList, ArrayList<Integer> artIdList) {
        this.artNameList = artNameList;
        this.artIdList = artIdList;
    }


    @Override
    public ArtHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row, parent, false);
        return new ArtHolder(v);
    }

    @Override
    public void onBindViewHolder(ArtHolder holder, int position) {
        holder.textView.setText(artNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return artNameList.size();
    }

    class ArtHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ArtHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerViewTextView);
        }
    }
}