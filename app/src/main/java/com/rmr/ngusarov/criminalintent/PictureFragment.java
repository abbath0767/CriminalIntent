package com.rmr.ngusarov.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class PictureFragment extends DialogFragment {
    public static final String EXTRA_PHOTO_FILE = "com.rmr.ngusarov.picture_file_photo";

    private ImageView mImageView;

    private String mPhotoPath;
    private File mPhotoFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoPath = getArguments().getString(EXTRA_PHOTO_FILE);
//        mPhotoFile = new File(mPhotoPath);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_photo, null);

        mImageView = (ImageView) view.findViewById(R.id.dialog_photo_image_view);
        Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoPath, getActivity());
        mImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    public static PictureFragment newInstance(String photoFile) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PHOTO_FILE, photoFile);
        fragment.setArguments(args);

        return fragment;
    }
}
