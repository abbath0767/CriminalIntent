package com.rmr.ngusarov.criminalintent;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class CriminalIntentJSONSerializer {
    private Context mContext;
    private String mFileName;

    public CriminalIntentJSONSerializer(Context c, String f) {
        mContext = c;
        mFileName = f;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws IOException, JSONException {
        JSONArray jarray = new JSONArray();
        for (Crime crime: crimes) {
            jarray.put(crime.toJSON());
        }

        Writer writer = null;
        try {
            OutputStream out = mContext
                    .openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(jarray.toString());
        } finally {
            if (writer != null)
                writer.close();
        }

    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<>();
        BufferedReader br = null;

        try {
            InputStream in = mContext.openFileInput(mFileName);
            br = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();

            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Происходит при начале "с нуля"; не обращайте внимания
        } finally {
            if (br != null)
                br.close();
        }

        return crimes;
    }
}
