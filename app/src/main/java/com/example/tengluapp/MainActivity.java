package com.example.tengluapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //UI views for the recognition
    public MaterialButton inputImageBtn;
    public MaterialButton recognizedTextBtn;
    public ShapeableImageView imageIv;
    public EditText recognizedTextEt;

    //TAG
    private static final String TAG ="MAIN_TAG";

    //Uri of the image that wr will take from Camera/Gallery
    private Uri imageUri = null;

    //to handle the results of the Camera/Gallery permissions
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    //arrays of permission required to pick image from Camera/Gallery
    private  String[] cameraPermissions;
    private  String[] storagePermissions;

    //progress dialog
    private ProgressDialog progressDialog;

    //text recognizer
    private TextRecognizer textRecognizer;

    //UI views for lists
    public Button list_tension_metre;
    public Button list_gluco_metre;

    //directory and file that contains the apps stored data
    String dirname = "TenGluApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //init UI views for the recognition
        inputImageBtn = findViewById(R.id.inputImageBtn);
        recognizedTextBtn = findViewById(R.id.recognizedTextBtn);
        imageIv = findViewById(R.id.imageIv);
        recognizedTextEt = findViewById(R.id.recognizedTextEt);

        //init UI views for the lists
        list_tension_metre = findViewById(R.id.list_tension_metre);
        list_gluco_metre = findViewById(R.id.list_gluco_metre);

        int list_tension_metreId = list_tension_metre.getId();
        int list_gluco_metreId = list_gluco_metre.getId();

        //init arrays of permissions required for Camera/Gallery
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        //init setup the progress dialog, show while text from image is being recognized
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Patientez");
        progressDialog.setCanceledOnTouchOutside(false);

        //init Text Recognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), dirname);
        File fileG = new File(dir, "listGResults.txt");
        File fileT = new File(dir, "listTResults.txt");

        //for the lists
        list_tension_metre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    createDirectoryFileT (dir, fileT);
                } catch (IOException e) {
                    Log.d(TAG, "onClick: ", e);
                    Toast.makeText(MainActivity.this, "Failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                openListTensResults();
            }
        });

        list_gluco_metre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createDirectoryFileG(dir, fileG);
                } catch (IOException e) {
                    Log.d(TAG, "onClick: ", e);
                    Toast.makeText(MainActivity.this, "Failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                openListGluResults();
            }
        });

        //for the recognition
        inputImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showInputImageDialog();

            }
        });

        //handle click, start recognizing text from image we took from camera/gallery
        recognizedTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if image is picked not, picked if imageUri is not null
                if (imageUri == null){
                    //imageUri is null, which means we haven't picked an image yet, can't recognize text
                    Toast.makeText(MainActivity.this, "Inserez une image d'abord", Toast.LENGTH_SHORT).show();
                }else{
                    //imageUri is null, which means we have picked image, we can recognize text
                    recognizeTextFromImage();
                }
            }
        });
    }

    private void recognizeTextFromImage() {
        Log.d(TAG, "recognizeTextFromImage: ");
        //set message and show progress dialog
        progressDialog.setMessage("preparer l'image");
        progressDialog.show();

        try {
            //prepare InputImage from image uri
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);
            //image prepared, we are about to start text recognition process, change progress message
            progressDialog.setMessage("Reconnaissance d'image");
            //start text recognition
            Task<Text> textTaskResult = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            //process completed, dismiss dialog
                            progressDialog.dismiss();
                            //get the recognized text to edit text
                            String recognizedText = text.getText();
                            Log.d(TAG, "onSuccess: recognizedText:"+recognizedText);
                            //set the recognized text to edit text
                            recognizedTextEt.setText(recognizedText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed recognizing text from image, dismiss dialog, show reason in Toast
                            progressDialog.dismiss();
                            Log.d(TAG, "onFailure: ", e);
                            Toast.makeText(MainActivity.this, "Failed recognizing text due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            //Exception occurred while preparing InputImage, dismiss dialog, show reason in Toast
            progressDialog.dismiss();
            Log.d(TAG, "recognizeTextFromImage: ", e);
            Toast.makeText(MainActivity.this, "Failed preparing image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void showInputImageDialog() {
        //init PopupMenu param 1 is context, param 2 is UI view where you want to show PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, inputImageBtn);

        //add items camera, gallery to PopupMenu, param 2 is menu id. param 3 is position of this menu item in menu item list, param 4 is title of the menu
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CAMERA");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "GALLERIE");

        //show PopupMenu
        popupMenu.show();

        //handle PopupMenu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //get item ID that is clicked from PopupMenu
                int id = menuItem.getItemId();
                if(id==1){
                    //camera is clicked, check if camera permissions are granted
                    Log.d(TAG, "onMenuItemClick: Camera Clicked...");
                    if(checkCameraPermissions()){
                        //camera permissions granted, we can launch camera intent
                        pickImageCamera();
                    }
                    else{
                        //camera permissions not granted, request the camera permissions
                        requestCameraPermissions();
                    }
                }
                else if (id == 2){
                    //gallery is clicked, check if storage permission is granted or not
                    Log.d(TAG, "onMenuItemClick: Gallery Clicked...");
                    if(checkStoragePermission()){
                        //storage permission granted, we can launch gallery intent
                        pickImageGallery();
                    }
                    else{
                        //storage permission not granted, request the storage permission
                        requestStoragePermission();
                    }
                }
                return true;
            }
        });

    }

    private void pickImageGallery(){
        Log.d(TAG, "pickImageGallery: ");
        //intent to pick image from gallery, will show all resources from where we can pick the image
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set type of file we want to pick i.e. image
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will receive the image, if picked
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //image picked
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri"+imageUri);
                        Log.e(TAG, "onActivityResult: imageUri before crop "+imageUri);

                        Intent intent = new Intent(MainActivity.this, ActivityCropper.class);
                        intent.putExtra("IMAGE",imageUri.toString());
                        startActivityForResult(intent,700);

                        //set to image view
                        imageIv.setImageURI(imageUri);
                        Log.e(TAG, "onActivityResult: imageUri after crop "+imageUri);
                        Log.d(TAG, "onActivityResult: imageUri"+imageUri);
                        //set to image view
//                        imageIv.setImageURI(imageUri);
                    }
                    else{
                        Log.d(TAG, "onActivityResult: Cancelled");
                        //cancelled
                        Toast.makeText(MainActivity.this, "cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageCamera(){

        Log.d(TAG, "pickImageCamera: ");
        //get ready the image data to store in MediaStore
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");
        //image Uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Intent to launch camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will receive the image, if taken from camera
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //we already have the image in imageUri using the function pickImageCamera()
                        Log.d(TAG, "onActivityResult: imageUri"+imageUri);
                        Intent intent = new Intent(MainActivity.this, ActivityCropper.class);
                        intent.putExtra("IMAGE",imageUri.toString());
                        startActivityForResult(intent,700);
                        //imageIv.setImageURI(imageUri);
                    }
                    else{
                        //cancelled
                        Log.d(TAG, "onActivityResult: Cancelled");
                        Toast.makeText(MainActivity.this, "cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission (){

        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        /*check if camera and storage permissions are allowed
        or not. return true if allowed, false if not allowed*/
        boolean cameraRequest = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean storageRequest = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return cameraRequest && storageRequest;
    }

    private void requestCameraPermissions() {
        //request camera permissions (for camera intent)
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -1 && requestCode==700){
            String result = data.getStringExtra("RESULTAT");
            Uri resultUri = null;
            if(result!=null){
                resultUri=Uri.parse(result);
            }

            imageIv.setImageURI(resultUri);
            imageUri = resultUri;

        }
    }

    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantedResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //check if some action from permission dialog performed or not allow/deny
                if (grantedResults.length>0){
                    //check if camera, storage permissions granted, contains boolean results either true or false
                    boolean cameraAccepted = grantedResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantedResults[1] == PackageManager.PERMISSION_GRANTED;
                    //check if both permissions are granted or not
                    if(cameraAccepted && storageAccepted){
                        //both permissions (camera, storage) are granted, we can launch camera intent
                        pickImageCamera();
                    }
                    else{
                        //one or both permissions are denied, can't launch camera intent
                        Toast.makeText(this, "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //neither allowed nor denied, rather cancelled
                    Toast.makeText(this, "Cancelled...", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                //check if some action from permission dialog performed or not allow/deny
                if (grantedResults.length>0){
                    //check if storage permissions granted, contain boolean results either true or false
                    boolean storageAccepted = grantedResults[0] == PackageManager.PERMISSION_GRANTED;
                    //check if storage permission is granted or not
                    if(storageAccepted){
                        //storage permission granted, we can launch gallery intent
                        pickImageGallery();
                    }
                    else{
                        //storage permission denied, we can't launch gallery intent
                        Toast.makeText(this, "Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    //displaying the results in form of arrays in activity_list_tens_results
    public void createDirectoryFileT (File dir, File file) throws IOException {

        if (!dir.exists()) {
            dir.mkdir();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        String[] values = recognizedTextEt.getText().toString().split("\n");

        String line = TextUtils.join(",", values);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a");

        String date = dateFormat.format(calendar.getTime());
        String hour = hourFormat.format(calendar.getTime());

        // Add the gluco_metre_results string to the file
        try {
            if (file.length() == 0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.write(Paths.get(String.valueOf(file)), (date + "," + hour + "," + line).getBytes(), StandardOpenOption.APPEND);
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.write(Paths.get(String.valueOf(file)), ("\n" + date + "," + hour + "," + line).getBytes(), StandardOpenOption.APPEND);
                }
            }

            Toast.makeText(MainActivity.this, "Data written successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d(TAG, "createDirectoryFile: ", e);
            Toast.makeText(MainActivity.this, "Failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //displaying the results in form of arrays in activity_list_tens_results
    public void createDirectoryFileG (File dir, File file) throws IOException {


        if (!dir.exists()) {
            dir.mkdir();
        }

        if (!file.exists()) {
            file.createNewFile();
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a");

        String date = dateFormat.format(calendar.getTime());
        String hour = hourFormat.format(calendar.getTime());

        // Add the gluco_metre_results string to the file
        try {
            if (file.length() == 0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.write(Paths.get(String.valueOf(file)), (date + "," + hour + "," + recognizedTextEt.getText().toString()).getBytes(), StandardOpenOption.APPEND);
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.write(Paths.get(String.valueOf(file)), ("\n" + date + "," + hour + "," + recognizedTextEt.getText().toString()).getBytes(), StandardOpenOption.APPEND);
                }
            }

            Toast.makeText(MainActivity.this, "Data written successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.d(TAG, "createDirectoryFile: ", e);
            Toast.makeText(MainActivity.this, "Failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openListTensResults() {
        Intent intent =new Intent( this, ListTensResults.class);
        startActivity(intent);
    }

    public void openListGluResults() {
        Intent intent =new Intent( this, ListGluResults.class);
        startActivity(intent);
    }

}