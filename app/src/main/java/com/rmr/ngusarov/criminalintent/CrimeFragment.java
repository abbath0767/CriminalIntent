package com.rmr.ngusarov.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "com.rmr.ngusarov.criminalintent.crime_id";
    public static final String DIALOG_DATE = "date";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_CONTACT = 1;
    private Uri uriContact;
    private String contactID;

    private EditText mEditText;
    private CheckBox mSolvedCheckBox;
    private Button mDateButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private Crime mCrime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        getActivity().setTitle("Crime");

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
            mCallButton.setText(mCrime.getPhone());
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

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date d = (Date) data.getSerializableExtra(DateAndTimeDialog.EXTRA_DATE);
            mCrime.setDate(d);
            updateDateOnButton(d);
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
                mCallButton.setText(phone);
                mCallButton.setVisibility(View.VISIBLE);
            } else {
                mCallButton.setText("");
                mCallButton.setVisibility(View.INVISIBLE);
            }
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
}
