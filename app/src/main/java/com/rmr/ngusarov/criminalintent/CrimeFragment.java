package com.rmr.ngusarov.criminalintent;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "com.rmr.ngusarov.criminalintent.crime_id";
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 2;
    public static final String DIALOG_DATE = "date";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_CONTACT = 1;
    public static final int REQUEST_PHOTO = 3;
    private Uri uriContact;
    private String contactID;

    private EditText mEditText;
    private CheckBox mSolvedCheckBox;
    private Button mDateButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Crime mCrime;
    private File mPhotoFile;
    private Callbacks mCallbacks;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            getPermissionToReadUserContacts();
        }

        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

//        getActivity().setTitle("Crime");

        mEditText = (EditText) v.findViewById(R.id.crime_title);
        mEditText.setText(mCrime.getTitle());
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //void field
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //void field ..
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDateOnButton(mCrime.getDate());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                DateAndTimeDialog dialog = DateAndTimeDialog.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
                //DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
//                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
//                dialog.show(fm, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report_button);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo need update - shareCompat
                Intent ishc = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(ishc);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.shoose_suspect_button);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null)
            mSuspectButton.setText(mCrime.getSuspect());

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mCallButton = (Button) v.findViewById(R.id.crime_call_button);
        if (mCrime.getPhone() == null)
            mCallButton.setVisibility(View.INVISIBLE);
        else {
            mCallButton.setText(getString(R.string.crime_call_text) + " " + mCrime.getPhone());
            mCallButton.setVisibility(View.VISIBLE);
        }
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mCrime.getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                startActivity(intent);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera_image_button);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo_image_view);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                PictureFragment fragment = PictureFragment.newInstance(mPhotoFile.getPath());
                fragment.show(fm, "myDialog");
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date d = (Date) data.getSerializableExtra(DateAndTimeDialog.EXTRA_DATE);
            mCrime.setDate(d);
            updateDateOnButton(d);
            updateCrime();
        } //else if (requestCode == REQUEST_CONTACT && data != null) {
//            Uri contactUri = data.getData();
//            String[] queryStringField = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
//            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryStringField, null, null, null);
//
//            try {
//                if (cursor == null)
//                    return;
//                cursor.moveToFirst();
//                String suspect = cursor.getString(0);
//                mCrime.setSuspect(suspect);
//                mSuspectButton.setText(suspect);
//            } finally {
//                 cursor.close();
//            }
//        }
        else if (requestCode == REQUEST_CONTACT && data != null) {
            //TODO Chrome - add permmission 
            uriContact = data.getData();
            String name = getSuspectName();
            mCrime.setSuspect(name);
            mSuspectButton.setText(name);
            //todo need debug with phone == null;
            String phone = getSuspectNumber();
            Log.d(CrimeListFragment.TAG, "new phone = " + phone);
            mCrime.setPhone(phone);
            if (phone != null) {
                mCallButton.setText(getString(R.string.crime_call_text) + " " + phone);
                mCallButton.setVisibility(View.VISIBLE);
            } else {
                mCallButton.setText("");
                mCallButton.setVisibility(View.INVISIBLE);
            }
            updateCrime();
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
            updateCrime();
        }

    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists())
            mPhotoView.setImageDrawable(null);
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

    }

    private String getSuspectName() {
        String contactName = null;

        // querying contact data store
        Cursor cursor = getActivity().getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d(CrimeListFragment.TAG, "Contact Name: " + contactName);

        return contactName;
    }

    private String getSuspectNumber() {
        String contactNumber = null;
        // getting contacts ID
        Cursor cursorID = getActivity().getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(CrimeListFragment.TAG, "Contact ID: " + contactID);

        Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(CrimeListFragment.TAG, "Contact Phone Number (Mobile): " + contactNumber);

        return contactNumber;
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved())
            solvedString = getString(R.string.crime_report_solved);
         else
            solvedString = getString(R.string.crime_report_unsolved);

        String dateFormat = "EEEE, dd MMMM yyyy, HH:mm";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspectString = mCrime.getSuspect();
        if (suspectString == null) {
            suspectString = getString(R.string.crime_report_no_suspect);
        } else {
            suspectString = getString(R.string.crime_report_suspect, suspectString);
        }

        String reportString = getString(R.string.crime_report, mCrime.getTitle(), dateString,
                solvedString, suspectString);

        return reportString;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (getActivity().shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void updateDateOnButton(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm");
        String formatDate = sdf.format(d);
        mDateButton.setText(formatDate);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, R.id.deleteButtonInBar, 0, R.string.delete_crime).setIcon(R.drawable.ic_delete_crime)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null)
//                    CrimeLab.get(getActivity()).deleteCrime(mCrime);
                    NavUtils.navigateUpFromSameTask(getActivity());
                getActivity().finish();
                return true;
            case R.id.deleteButtonInBar:
                Log.d(CrimeListFragment.TAG, "delete button tap");
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                if (NavUtils.getParentActivityName(getActivity()) != null)
//                    CrimeLab.get(getActivity()).deleteCrime(mCrime);
                    NavUtils.navigateUpFromSameTask(getActivity());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }


}
