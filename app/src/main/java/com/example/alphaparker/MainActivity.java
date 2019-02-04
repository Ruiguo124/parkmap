package com.example.alphaparker;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.content.Intent;
import android.media.ExifInterface;
import android.widget.ImageView;
import java.util.Calendar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import android.support.annotation.NonNull;
import android.widget.TextView;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    public static String detectedText;
    public static int hour = 0;
    public static int day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.editText);
        textViewDate.setText(currentDate);
        // set date        //Date currentTime = Calendar.getInstance().getTime();
        //TextView currentTimeView = findViewById(R.id.textView2);
        //currentTimeView.setText(currentTime.toString());


        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);        // set hour
        String strDateFormat2 = "hh";
        DateFormat dateFormat2 = new SimpleDateFormat(strDateFormat2);
        String hourInput = dateFormat2.format(date);
        hour = Integer.parseInt(hourInput);        // set day

        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated
        String dayOfTheWeek = simpleDateformat.format(date);

        if(dayOfTheWeek.equals("Mon")){
            day = 1;
        }
        else if(dayOfTheWeek.equals("Tue")){
            day = 2;
        }
        else if(dayOfTheWeek.equals("Wed")){
            day = 3;
        }
        else if(dayOfTheWeek.equals("Thu")){
            day = 4;
        }
        else if(dayOfTheWeek.equals("Fri")){
            day = 5;
        }
        else if(dayOfTheWeek.equals("Sat")){
            day = 6;
        }
       else if(dayOfTheWeek.equals("Sun")){
            day = 7;
       }
       TextView textViewTime = findViewById(R.id.textView2);
       String formattedDate= dateFormat.format(date);
       textViewTime.setText(hourInput + "h");


    }



    public void openMap(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;
    Uri photoURI;
    float rotateImage;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {

            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }
    //When camera button is clicked
    public void openCamera(View view){
        dispatchTakePictureIntent();
    }
    //Rotate Image




    FirebaseVisionImage image;
    //Intent intentExtra = new Intent(this,MapsActivity.class);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        try {
            image = FirebaseVisionImage.fromFilePath(this, photoURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
            imageView.setRotation(90);

            imageView.setImageURI(photoURI);


            File fdelete = new File(mCurrentPhotoPath);

        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" );
            } else {
                System.out.println("file not Deleted :");
            }
        }
            final FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                textView.setText(firebaseVisionText.getText());
                                detectedText = firebaseVisionText.getText();
                                detectedText = detectedText.replace(" ","");
                                //detectedText = detectedText.replace("\n","");


                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("failed to recognize text");
                                    }
                                });


    }
}
