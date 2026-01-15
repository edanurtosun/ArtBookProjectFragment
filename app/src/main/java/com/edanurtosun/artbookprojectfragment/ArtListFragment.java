package com.edanurtosun.artbookprojectfragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArtListFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> artNameList = new ArrayList<>();
    ArrayList<Integer> artIdList = new ArrayList<>();

    SQLiteDatabase database;

    public ArtListFragment() {
        super(R.layout.fragment_art_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataFromSQLite();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_art_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database = requireActivity().openOrCreateDatabase("Arts", getContext().MODE_PRIVATE, null);

        getDataFromSQLite();
    }
    private void getDataFromSQLite() {

        try {

            Cursor cursor = database.rawQuery("SELECT id, artname FROM arts", null);

            int idIx = cursor.getColumnIndex("id");
            int nameIx = cursor.getColumnIndex("artname");

            artNameList.clear();
            artIdList.clear();

            while (cursor.moveToNext()) {
                artIdList.add(cursor.getInt(idIx));
                artNameList.add(cursor.getString(nameIx));
            }

            ArtAdapter adapter = new ArtAdapter(artNameList, artIdList);
            recyclerView.setAdapter(adapter);

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


