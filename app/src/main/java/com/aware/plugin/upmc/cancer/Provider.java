package com.aware.plugin.upmc.cancer;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

import java.nio.file.NotDirectoryException;
import java.util.HashMap;

public class Provider extends ContentProvider {

    public static final int DATABASE_VERSION = 3;
    public static String AUTHORITY = "com.aware.plugin.upmc.cancer.provider.survey";

    private static final int ANSWERS = 1;
    private static final int ANSWERS_ID = 2;
    private static final int NOTIFICATIONS = 3;
    private static final int NOTIFICATIONS_ID = 4;

    public static final class Symptom_Data implements BaseColumns {
        private Symptom_Data(){}

        public static final Uri CONTENT_URI = Uri.parse("content://" + Provider.AUTHORITY + "/upmc_cancer");
        static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.upmc.cancer";
        static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.upmc.cancer";

        static final String _ID = "_id";
        static final String TIMESTAMP = "timestamp";
        static final String DEVICE_ID = "device_id";
    }

    public static final class Notification_Data implements BaseColumns {
        private Notification_Data(){}

        public static final Uri CONTENT_URI = Uri.parse("content://" + Provider.AUTHORITY + "/upmc_cancer_motivation");
        static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.upmc.cancer.notification";
        static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.upmc.cancer.notification";

        static final String _ID = "_id";
        static final String TIMESTAMP = "timestamp";
        static final String FIRED = "fired";
        static final String DEVICE_ID = "device_id";
        static final String STATUS = "status";
    }

    public static String DATABASE_NAME = "plugin_upmc_cancer.db";

    public static final String[] DATABASE_TABLES = {
            "upmc_cancer",
            "upmc_cancer_motivation"
    };

    public static final String[] TABLES_FIELDS = {
            Symptom_Data._ID + " integer primary key autoincrement," +
                    Symptom_Data.TIMESTAMP + " real default 0," +
                    Symptom_Data.DEVICE_ID + " text default ''",

            Notification_Data._ID + " integer primary key autoincrement," +
                    Notification_Data.TIMESTAMP + " real default 0," +
                    Notification_Data.FIRED + " text default 0," +
                    Notification_Data.DEVICE_ID + " text default '',"+
                    Notification_Data.STATUS + " text default ''"
    };


    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> surveyMap = null;
    private static HashMap<String, String> notificationMap = null;
    private DatabaseHelper dbHelper = null;
    private static SQLiteDatabase database = null;

    private void initialiseDatabase() {
        if (dbHelper == null)
            dbHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if (database == null)
            database = dbHelper.getWritableDatabase();
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.survey";

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Provider.AUTHORITY, DATABASE_TABLES[0], ANSWERS);
        sUriMatcher.addURI(Provider.AUTHORITY, DATABASE_TABLES[0] + "/#", ANSWERS_ID);
        sUriMatcher.addURI(Provider.AUTHORITY, DATABASE_TABLES[1], NOTIFICATIONS);
        sUriMatcher.addURI(Provider.AUTHORITY, DATABASE_TABLES[1] + "/#", NOTIFICATIONS_ID);

        notificationMap = new HashMap<>();
        notificationMap.put(Notification_Data._ID, Notification_Data._ID);
        notificationMap.put(Notification_Data.TIMESTAMP, Notification_Data.TIMESTAMP);
        notificationMap.put(Notification_Data.FIRED, Notification_Data.FIRED);
        notificationMap.put(Notification_Data.DEVICE_ID, Notification_Data.DEVICE_ID);
        notificationMap.put(Notification_Data.STATUS, Notification_Data.STATUS);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        initialiseDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        Log.d("Niels debug", "URI value: " + String.valueOf(uri));

        switch (sUriMatcher.match(uri)) {

            case ANSWERS:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(surveyMap);
                break;
            case NOTIFICATIONS:

                Log.d("Niels debug", "Case notifications");

                qb.setTables(DATABASE_TABLES[1]);
                qb.setProjectionMap(notificationMap);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            if (Aware.DEBUG)
                Log.e(Aware.TAG, e.getMessage());

            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ANSWERS:
                return Symptom_Data.CONTENT_TYPE;
            case ANSWERS_ID:
                return Symptom_Data.CONTENT_ITEM_TYPE;
            case NOTIFICATIONS:
                return Notification_Data.CONTENT_TYPE;
            case NOTIFICATIONS_ID:
                return Notification_Data.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues initialValues) {
        initialiseDatabase();
        if (database == null) return null;

        ContentValues values = (initialValues != null) ? new ContentValues(initialValues) : new ContentValues();

        database.beginTransaction();

        switch (sUriMatcher.match(uri)) {
            case ANSWERS:
                long quest_id = database.insertWithOnConflict(DATABASE_TABLES[0],
                        Symptom_Data.DEVICE_ID, values, SQLiteDatabase.CONFLICT_IGNORE);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (quest_id > 0) {
                    Uri questUri = ContentUris.withAppendedId(Symptom_Data.CONTENT_URI,
                            quest_id);
                    getContext().getContentResolver().notifyChange(questUri, null);
                    return questUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            case NOTIFICATIONS:
                long notif_id = database.insertWithOnConflict(DATABASE_TABLES[1],
                        Notification_Data.DEVICE_ID, values, SQLiteDatabase.CONFLICT_IGNORE);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (notif_id > 0) {
                    Uri questUri = ContentUris.withAppendedId(Notification_Data.CONTENT_URI,
                            notif_id);
                    getContext().getContentResolver().notifyChange(questUri, null);
                    return questUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        initialiseDatabase();
        if (database == null) return 0;

        database.beginTransaction();

        int count;
        switch (sUriMatcher.match(uri)) {
            case NOTIFICATIONS:
                count = database.delete(DATABASE_TABLES[1], selection, selectionArgs);
                break;
            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        database.setTransactionSuccessful();
        database.endTransaction();

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        initialiseDatabase();
        if (database == null) return 0;

        database.beginTransaction();

        int count;
        switch (sUriMatcher.match(uri)) {
            case NOTIFICATIONS:
                count = database.update(DATABASE_TABLES[1], values, selection, selectionArgs);
                break;
            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
