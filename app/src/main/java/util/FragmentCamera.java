package util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sellcom.tracker.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dmolinero on 27/05/14.
 */
public class FragmentCamera extends Fragment implements View.OnClickListener{
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri imageUri;

    LinearLayout pictureButton;
    ImageView previewImage;
    String nameFile,dir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        try {
            dir = Environment.getExternalStorageDirectory() + File.separator +".photo";
            nameFile = Environment.getExternalStorageDirectory() + File.separator +".photo/"+ "test.png";
            previewImage = (ImageView) getActivity().findViewById(R.id.preview_picture);
            pictureButton = (LinearLayout) getActivity().findViewById(R.id.content_camera);
            pictureButton.setOnClickListener(this);

            checkExistDirectory();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean checkExistDirectory(){
        String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+".photo";
        File directory = new File(file);
        Log.e("DIR Directory", "Directory: "+file);
        if(directory.exists()){
//			directory.delete();
            Log.e("Directory EXIST", "EXIST");
            return true;
        }else{
            directory.mkdirs();
            return false;
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void takePhoto() {
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT) .show();
        }else {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = new File(nameFile);
                photoFile.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                imageUri = Uri.fromFile(photoFile); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // set the image file name
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera,container,false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.content_camera:
                takePhoto();
                break;
        }
    }

    public Bitmap onResult(Intent data) {
        Bitmap image = null;
        if (data != null) {
            try {
                if (data.hasExtra("data")) {
                    Bitmap ima = data.getParcelableExtra("data");
                    ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                    FileOutputStream fOut = new FileOutputStream(nameFile);

                    ima.compress(CompressFormat.PNG, 100, out2);
                    ima.compress(CompressFormat.PNG, 100, fOut);
                    ima.recycle();

                    byte[] bytes =  out2.toByteArray();
                    try {
                        out2.close();
                        fOut.flush();
                        fOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bitmap unObjetoSerializable;
                    unObjetoSerializable = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);

                    Bitmap toyImageScaled = Bitmap.createScaledBitmap(unObjetoSerializable, 200, 200
                            * unObjetoSerializable.getHeight() / unObjetoSerializable.getWidth(), false);

                    // Override Android default landscape orientation and save portrait
                    Matrix matrix = new Matrix();
                    matrix.postRotate(0);
                    Bitmap rotatedScaledToyImage = Bitmap.createBitmap(toyImageScaled, 0,
                            0, toyImageScaled.getWidth(), toyImageScaled.getHeight(),
                            matrix, true);

                    ExifInterface exif = new ExifInterface(nameFile);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                    Log.w("Orientacion imagen","orientacion="+orientation);

                    image = rotatedScaledToyImage;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Bitmap ima = BitmapFactory.decodeFile(nameFile);
                ExifInterface exif = new ExifInterface(nameFile);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                Log.w("Orientacion preview","orientacion="+orientation);

                if(orientation==6) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rotatedScaledToyImage = Bitmap.createBitmap(ima, 0,
                            0, ima.getWidth(), ima.getHeight(),
                            matrix, true);
                    image = rotatedScaledToyImage;
                }
                if (orientation==1) {
                    image = ima;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }
}
