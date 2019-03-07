package de.reikodd.meinwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BirthAdressbook {

    public static void getBirthPattern(Context context) {

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = PreferenceManager.getDefaultSharedPreferences(context);

        StringBuilder gPattern = new StringBuilder();

        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
        };

        String where = ContactsContract.Data.MIMETYPE + "= ? AND "
                + ContactsContract.CommonDataKinds.Event.TYPE + "="
                + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[]{ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
        String sortOrder = null;
        Cursor cur = context.getContentResolver().query(uri, projection, where, selectionArgs, sortOrder);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.");
        Date initDate;
        String parsedDate;

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String birthday = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                try {
                    if (birthday.length() == 7) {
                        initDate = new SimpleDateFormat("--MM-dd").parse(birthday);
                        parsedDate = formatter.format(initDate);
                        gPattern.append(parsedDate);
                        gPattern.append("|");
                    } else if (birthday.length() == 10) {
                        initDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
                        parsedDate = formatter.format(initDate);
                        gPattern.append(parsedDate);
                        gPattern.append("|");
                    }
                } catch (ParseException e) {

                }
            }
        }
        cur.close();
        editor = settings.edit();
        editor.putString("patternBirthday", gPattern.toString().replaceFirst(".$", ""));
        editor.apply();
    }
}