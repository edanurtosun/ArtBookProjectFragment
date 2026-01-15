package com.edanurtosun.artbookprojectfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class ArtFragment  extends Fragment {

    ImageView imageView;
    EditText artName, painterName, yearText;
    Button saveButton;
    Bitmap selectedImage;

    SQLiteDatabase database;

    ActivityResultLauncher<Intent> imageLauncher;


    public ArtFragment(){
        super(R.layout.fragment_art);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_art, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        database = requireActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);


        imageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                        Uri imageUri = result.getData().getData();

                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(
                                        requireActivity().getContentResolver(), imageUri);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                            } else {
                                selectedImage = MediaStore.Images.Media.getBitmap(
                                        requireActivity().getContentResolver(), imageUri);
                            }

                            imageView.setImageBitmap(selectedImage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        imageView = view.findViewById(R.id.imageView);
        artName = view.findViewById(R.id.editTextText);
        painterName = view.findViewById(R.id.editTextText2);
        yearText = view.findViewById(R.id.editTextText3);
        saveButton = view.findViewById(R.id.button);

        imageView.setOnClickListener(v -> selectImage());
        saveButton.setOnClickListener(v -> saveArt());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageLauncher.launch(intent);
    }

    private void saveArt() {

            String name = artName.getText().toString();
            String artistName = painterName.getText().toString();
            String year = yearText.getText().toString();

            Bitmap smallImage = makeSmallerImage(selectedImage, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            byte [] byteArray = outputStream.toByteArray();


            //db
            try{
                database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artname VARCHAR, artistname VARCHAR, year VARCHAR, image BLOB)");

                String sqlString = "INSERT INTO arts (artname, artistname, year, image) VALUES(?, ?, ?, ?)";
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1, name);
                sqLiteStatement.bindString(2, artistName);
                sqLiteStatement.bindString(3, year);
                sqLiteStatement.bindBlob(4, byteArray);
                sqLiteStatement.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }


        /*Cursor c = database.rawQuery("SELECT COUNT(*) FROM arts", null);
        c.moveToFirst();
        System.out.println("DB ROW COUNT = " + c.getInt(0));
        c.close();*/

            goToArtList();
    }


    //image boyutunu kucultme
    public Bitmap makeSmallerImage(Bitmap image, int maximumSize){
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if(bitmapRatio > 1){
            //landscape image
            width = maximumSize;
            height = (int)(width / bitmapRatio);
        }else{
            //portrait image
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width,height,true);
    }

    private void goToArtList() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new ArtListFragment())
                .commit();
    }


}
