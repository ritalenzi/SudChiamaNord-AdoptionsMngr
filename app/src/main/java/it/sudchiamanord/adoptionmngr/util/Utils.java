package it.sudchiamanord.adoptionmngr.util;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import it.sudchiamanord.adoptionmngr.R;
//import it.sudchiamanord.adoptionmngr.activities.LoadActivity;
//import it.sudchiamanord.adoptionmngr.activities.SearchResultActivity;
//import it.sudchiamanord.adoptionmngr.info.KidInfo;
//import it.sudchiamanord.adoptionmngr.info.ListaAdozioni;
//import it.sudchiamanord.adoptionmngr.provider.DBContract.KidEntry;
//import it.sudchiamanord.adoptionmngr.provider.DBContract.AdoptionEntry;
//import it.sudchiamanord.adoptionmngr.provider.DBHelper;
//import it.sudchiamanord.adoptionmngr.provider.query.Query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Rita Lenzi on 11/12/18.
 */
public class Utils
{
    private static final String TAG = Utils.class.getSimpleName();

    public static void close (InputStream is)
    {
        if (is == null) {
            return;
        }

        try {
            is.close();
        }
        catch (IOException e) {
            Log.e (TAG, "Impossible to close the input stream");
        }
    }

    public static void close (OutputStream os)
    {
        if (os == null) {
            return;
        }

        try {
            os.close();
        }
        catch (IOException e) {
            Log.e (TAG, "Impossible to close the output stream");
        }
    }

//    public static Properties getProperties (File file)
//    {
//        Properties properties = new Properties();
//        if (file.exists()) {
//            FileInputStream fis = null;
//            try {
//                fis = new FileInputStream (file);
//                properties.load (fis);
//            }
//            catch (Exception e) {
//                Log.e (TAG, "Impossible to load the properties in the file", e);
//            }
//            finally {
//                Utils.close (fis);
//            }
//        }
//
//        return properties;
//    }

    public static Properties getProperties (File file, int errorMessageCode, Context context)
    {
        Properties properties = new Properties();
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream (file);
                properties.load (fis);
            }
            catch (Exception e) {
                Log.e (TAG, "Impossible to load the properties in the file", e);
                generateErrorDialog(errorMessageCode, context);
            }
            finally {
                Utils.close (fis);
            }
        }

        return properties;
    }

    public static void updateConfig (File file, Properties properties, Context context)
    {
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput (file.getPath(), Context.MODE_PRIVATE);
            properties.store(fos, "");
        }
        catch (java.io.IOException e) {
            Log.e (TAG, "Exception in updating the configuration file on the device", e);
            Utils.generateErrorDialog (R.string.configFileUpdateError, context);
        }
        finally {
            Utils.close (fos);
        }
    }

//    public static boolean saveFile (File file, String content, Context context)
//    {
//        if (file.exists()) {
//            file.delete();
//        }
//
//        FileOutputStream fos = null;
//        PrintStream printStream = null;
//        try {
//            fos = context.openFileOutput (file.getPath(), Context.MODE_PRIVATE);
//            printStream = new PrintStream (fos);
//            printStream.print (content);
//        }
//        catch (java.io.IOException e) {
//            Log.e (TAG, "Exception in saving the file on the device", e);
//            Utils.generateErrorDialog (R.string.fileSaveError, context);
//            return false;
//        }
//        finally {
//            Utils.close (printStream);
//            Utils.close (fos);
//        }
//
//        Log.d (TAG, "Saved file: " + file.getAbsolutePath());
//
//        return true;
//    }

    public static void generateErrorDialog (int messageCode, Context context)
    {
        new AlertDialog.Builder (context)
                .setTitle (R.string.generalError)
                .setMessage (messageCode)
                .setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick (DialogInterface dialog, int which)
                    {
                    }
                })
                .setIcon (android.R.drawable.ic_dialog_alert)
                .show();
    }

//    public static String convertStreamToString (InputStream is)
//    {
//        BufferedReader reader = new BufferedReader (new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//
//        String line;
//        try {
//            while ((line = reader.readLine()) != null) {
//                sb.append (line).append ('\n');
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        finally {
//            close (is);
//        }
//        return sb.toString();
//    }

//    public static boolean insert (Context context, KidInfo kidInfo)
//    {
//        ContentValues cv = DBHelper.getKidContentValues(kidInfo);
//        Uri uri = context.getContentResolver().insert (KidEntry.KID_CONTENT_URI, cv);
//        Log.d (TAG, "Uri: " + uri);
//        if (uri == null) {
//            return false;
//        }
//
////        String whereClause = KidEntry.COLUMN_SERVER_ID + " = ?";
////        String[] whereArgs = new String[1];
////        whereArgs[0] = String.valueOf (kidInfo.getIdado());
//
//        long row = KidEntry.getRowFromURI (uri);
//        if (row <= 0) {
//            return false;
//        }
//
//        String whereClause = KidEntry._ID + " = ?";
//        String[] whereArgs = new String[1];
//        whereArgs[0] = String.valueOf (row);
//
////        Log.d (TAG, "KidInfo values - idado: " + kidInfo.getIdado());
////        Log.d (TAG, "KidInfo values - name: " + kidInfo.getAnome());
////        Log.d (TAG, "KidInfo values - lastname: " + kidInfo.getAcogn());
////        Log.d (TAG, "KidInfo values - middlename: " + kidInfo.getAnick());
////        Log.d (TAG, "N adoptions: " + (kidInfo.getListaAdozioni() != null ? kidInfo.getListaAdozioni().size() : 0));
//
//        Cursor cursor = context.getContentResolver().query (KidEntry.KID_CONTENT_URI, null,
//                whereClause, whereArgs, null);
//
//        Log.d (TAG, "Name: " + kidInfo.getAnome());
//        Log.d (TAG, "Loaded id: " + kidInfo.getDbId());
//        Log.d (TAG, "Row: " + row);
//
//        if (cursor.moveToFirst()) {
//            int idx = cursor.getColumnIndex (KidEntry._ID);
//            Log.d (TAG, "cursor.getInt(idx): " + cursor.getInt(idx));
//            if (kidInfo.getListaAdozioni() == null) {
//                return true;
//            }
//            for (ListaAdozioni adoptionInfo : kidInfo.getListaAdozioni()) {
//                cv = DBHelper.getAdoptionInfoValues (adoptionInfo, cursor.getInt(idx));
//                context.getContentResolver().insert (AdoptionEntry.ADOPTION_CONTENT_URI, cv);
//            }
//        }
//        cursor.close();
//
//        return true;
//    }
    
////    public static List<KidInfo> getKidInfo (Context context, Uri uri, String[] projection,
////                                            String whereClause, String[] whereArgs, String sortOrder)
//    public static List<KidInfo> getKidInfo (Context context, Query query)
//    {
//        Cursor cursor = context.getContentResolver().query (query.uri, query.projection,
//                query.whereClause, query.whereArgs, query.sortOrder);
//
//        //Log.d (TAG, "Found " + cursor.getCount() + " kids in the db");
//
//        List<KidInfo> kidsInfo = new ArrayList<>();
//
//        while (cursor.moveToNext()) {
//            KidInfo kidInfo = getKidInfo (context, cursor);
//
//            int idx = cursor.getColumnIndex (KidEntry._ID);
//            String whereClauseAd = AdoptionEntry.COLUMN_KID_ID + " = ?";
//            String[] whereArgsAd = new String[1];
//            whereArgsAd[0] = String.valueOf (cursor.getInt (idx));
//            List<ListaAdozioni> adoptionList = getAdoptionsList (context,
//                    new Query (AdoptionEntry.ADOPTION_CONTENT_URI, null, whereClauseAd,
//                    whereArgsAd, null));
//
//            kidInfo.setListaAdozioni (adoptionList);
//
//            kidsInfo.add (kidInfo);
//        }
//        cursor.close();
//
//        return kidsInfo;
//    }

//    @NonNull
//    private static KidInfo getKidInfo (Context context, Cursor cursor)
//    {
//        KidInfo kidInfo = new KidInfo();
//        kidInfo.setDbId (cursor.getInt (cursor.getColumnIndex (KidEntry._ID)));
//        kidInfo.setIdado (cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_SERVER_ID)));
//        kidInfo.setStato (cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_STATUS)));
//        kidInfo.setAnome (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_KID_NAME)));
//        kidInfo.setAcogn (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_KID_LASTNAME)));
//        kidInfo.setAnick (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_KID_NICKNAME)));
//        kidInfo.setAdsex (cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_SEX)));
//        kidInfo.setDatan (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_BDAY)));
//        kidInfo.setIndir (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_ADDRESS)));
//        kidInfo.setCitta (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_CITY)));
//        kidInfo.setAdtel (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_PHONE)));
//        int alive = cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_DAD_ALIVE));
//        kidInfo.setDadAlive (alive == 1);   // 0 (false) and 1 (true)
//        kidInfo.setPnome (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_DAD_NAME)));
//        kidInfo.setPcogn (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_DAD_LASTNAME)));
//        kidInfo.setPnick (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_DAD_NICKNAME)));
//        kidInfo.setPajob (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_DAD_JOB)));
//        kidInfo.setMomAlive (alive == 1);   // 0 (false) and 1 (true)
//        kidInfo.setMnome (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_MOM_NAME)));
//        kidInfo.setMcogn (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_MOM_LASTNAME)));
//        kidInfo.setMnick (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_MOM_NICKNAME)));
//        kidInfo.setMajob (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_MOM_JOB)));
//        kidInfo.setFfoto (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_PHOTO_NAME)));
//        kidInfo.setAnote (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_NOTES)));
//        kidInfo.setCreil (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_CREATE_DATE)));
//        kidInfo.setCreda (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_CREATE_BY)));
//        kidInfo.setScuol (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_SCHOOL_ID)));
//        kidInfo.setAnnos (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_SCHOOL_YEAR)));
//        kidInfo.setSclas (cursor.getInt (cursor.getColumnIndex (KidEntry.COLUMN_CLASS)));
//        kidInfo.setLastt (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_LAST_UPDATE_DATE)));
//        kidInfo.setLastu (cursor.getString (cursor.getColumnIndex (KidEntry.COLUMN_LAST_UPDATE_USER)));
//        kidInfo.setLocalPhotoFile (cursor.getString (cursor.getColumnIndex (
//                KidEntry.COLUMN_LOCAL_PHOTO_FILE)));
//
//        if (kidInfo.getLocalPhotoFile() != null) {
//            File filePath = context.getFileStreamPath (kidInfo.getLocalPhotoFile());
//            kidInfo.setPhoto (decodeBitmap (filePath, 90, 90));
//        }
//
//        return kidInfo;
//    }

//    public static List<ListaAdozioni> getAdoptionsList (Context context, String id)
//    {
//        Uri uriAd = AdoptionEntry.ADOPTION_CONTENT_URI;
//        String whereClauseAd = AdoptionEntry.COLUMN_KID_ID + " = ?";
//        String[] whereArgsAd = new String[1];
//        whereArgsAd[0] = id;
//
//        Cursor adoptCursor = context.getContentResolver().query (uriAd, null, whereClauseAd,
//                whereArgsAd, null);
//
//        //Log.d (TAG, "Found " + adoptCursor.getCount() + " adoption for the kid in the db");
//
//        List<ListaAdozioni> adoptionList = new ArrayList<>();
//        while (adoptCursor.moveToNext()) {
//            ListaAdozioni adoption = getAdoptionsList (adoptCursor);
//
//            adoptionList.add (adoption);
//        }
//
//        adoptCursor.close();
//
//        return adoptionList;
//    }

////    public static List<ListaAdozioni> getAdoptionsList (Context context, String[] projection,
////                                                        String whereClause, String[] whereArgs,
////                                                        String sortOrder)
//    public static List<ListaAdozioni> getAdoptionsList (Context context, Query query)
//    {
////        Uri uriAd = AdoptionEntry.ADOPTION_CONTENT_URI;
//
//        Cursor adoptCursor = context.getContentResolver().query (query.uri, query.projection,
//                query.whereClause, query.whereArgs, query.sortOrder);
//
//        //Log.d (TAG, "Found " + adoptCursor.getCount() + " adoption for the kid in the db");
//
//        List<ListaAdozioni> adoptionList = new ArrayList<>();
//        while (adoptCursor.moveToNext()) {
//            ListaAdozioni adoption = getAdoptionsList (adoptCursor);
//
//            adoptionList.add (adoption);
//        }
//
//        adoptCursor.close();
//
//        return adoptionList;
//    }

//    @NonNull
//    private static ListaAdozioni getAdoptionsList (Cursor adoptCursor)
//    {
//        ListaAdozioni adoption = new ListaAdozioni();
//
//        adoption.setIdpag (adoptCursor.getInt (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_PAYMENT_CODE)));
//        adoption.setPclas (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_CLASS)));
//        adoption.setCodic (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTION_CODE)));
//        adoption.setIdscu (adoptCursor.getInt (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_SCHOOL_ID)));
//        adoption.setScuno (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_SCHOOL_NAME)));
//        adoption.setScuci (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_SCHOOL_CITY)));
//        adoption.setScupr (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTION_PREFIX)));
//        adoption.setAddid (adoptCursor.getInt (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTER_ID)));
//        adoption.setAdnom (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTER_NAME)));
//        adoption.setAdcog (adoptCursor.getString (adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_ADOPTER_LASTNAME)));
//        adoption.setAnnos(adoptCursor.getString(adoptCursor.getColumnIndex(AdoptionEntry.COLUMN_SCHOOL_YEAR)));
//        return adoption;
//    }

//    public static Bitmap decodeBitmap (File filePath, int reqWidth, int reqHeight)
//    {
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream (filePath);
//
//            // First decode with inJustDecodeBounds=true to check dimensions
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream (fis, null, options);
//
//            // Calculate inSampleSize
//            options.inSampleSize = calculateInSampleSize (options, reqWidth, reqHeight);
//
//            fis.close();
//
//            fis = new FileInputStream (filePath);
//
//            // Decode bitmap with inSampleSize set
//            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeStream (fis, null, options);
//        }
//        catch (Exception e) {
//            Log.e (TAG, "getThumbnail() on internal storage", e);
//        }
//        finally {
//            Utils.close (fis);
//        }
//
//        return null;
//    }

//    public static int calculateInSampleSize (BitmapFactory.Options options, int reqWidth,
//                                             int reqHeight)
//    {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }

//    public static List<Integer> getKidIds (Context context, Query query)
//    {
//        Cursor adoptCursor = context.getContentResolver().query (query.uri, query.projection,
//                query.whereClause, query.whereArgs, query.sortOrder);
//
//        List<Integer> kidIds = new ArrayList<>();
//        while (adoptCursor.moveToNext()) {
//            Integer id = adoptCursor.getInt(adoptCursor.getColumnIndex (AdoptionEntry.COLUMN_KID_ID));
//            if (kidIds.contains (id)) {
//                continue;
//            }
//            kidIds.add (id);
//        }
//
//        adoptCursor.close();
//
//        return kidIds;
//    }

//    public static int deleteAllImages (Context context)
//    {
//        File privateDir = context.getFilesDir();
//        int count = 0;
//        if (privateDir.exists()) {
//            for (File file : privateDir.listFiles()) {
//                if (file.getName().endsWith ("jpg")) {
//                    if (file.delete()) {
//                        count++;
//                    }
//                }
//            }
//        }
//
//        return count;
//    }
}
