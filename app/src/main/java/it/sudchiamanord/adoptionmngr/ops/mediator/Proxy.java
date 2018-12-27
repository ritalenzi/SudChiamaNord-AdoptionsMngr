package it.sudchiamanord.adoptionmngr.ops.mediator;

import android.util.Log;

import it.sudchiamanord.adoptionmngr.R;
import it.sudchiamanord.adoptionmngr.ops.results.RegisterResult;

import java.io.IOException;

/**
 * Created by rita on 12/27/18.
 */
public class Proxy
{
    private static final String TAG = Proxy.class.getSimpleName();

//    public static DownloadResult downloadKidsInfo (String sessionKey, String serverURL, String schoolYear)
//    {
//        try {
//            KidsListProxy klUtility = new KidsListProxy (serverURL);
//            klUtility.request (sessionKey);
//            klUtility.setSchoolYear (schoolYear);
//
//            return klUtility.getResponse();
//        }
//        catch (IOException e) {
//            Log.e (TAG, "Problem in getting the kids info", e);
//        }
//
//        return null;
//    }
//
//    public static ImageDownloadResult downloadImage (String imageURL, Context context)
//    {
//        try {
//            ImageProxy iUtility = new ImageProxy (imageURL, context);
//            iUtility.request();
//
//            return iUtility.getResponse();
//        }
//        catch (IOException e) {
//            Log.e (TAG, "Problem in getting the kids info", e);
//        }
//
//        return null;
//    }

    public static RegisterResult doRegisterUser (String user, String password, String serverURL)
    {
        try {
            RegisterUserProxy ruUtility = new RegisterUserProxy (serverURL);
            ruUtility.request (user, password);

            return ruUtility.getResponse (password);
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in registering the user with the server", e);
        }

        return new RegisterResult (false, R.string.registerUserFailed, true, user);
    }
}
