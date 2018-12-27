package it.sudchiamanord.adoptionmngr.ops.mediator;

import android.util.Log;

import com.google.gson.Gson;
import it.sudchiamanord.adoptionmngr.R;
import it.sudchiamanord.adoptionmngr.ops.mediator.requests.Action;
import it.sudchiamanord.adoptionmngr.ops.mediator.requests.Details;
import it.sudchiamanord.adoptionmngr.ops.mediator.requests.Request;
import it.sudchiamanord.adoptionmngr.ops.mediator.responses.LoginTec;
import it.sudchiamanord.adoptionmngr.ops.results.RegisterResult;
import it.sudchiamanord.adoptionmngr.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rita on 12/27/18.
 */
class RegisterUserProxy
{
    private final String TAG = RegisterUserProxy.class.getSimpleName();

    private HttpURLConnection httpConn;

    RegisterUserProxy (String serverURL) throws IOException
    {
        URL url = new URL (serverURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput (true);
        httpConn.setRequestMethod ("POST");
        httpConn.setConnectTimeout (10000);
        httpConn.setRequestProperty ("Content-Type", "application/json");
    }

    void request (String user, String password) throws IOException
    {
        Gson gson = new Gson();
        Request request = new Request();
        request.setAction (Action.login.value());
        Details details = new Details();
        details.setEmail (user);
        details.setPaswd (password);
        request.setDetails (details);
        String dataRequest = gson.toJson (request);
        try {
            OutputStreamWriter wr = new OutputStreamWriter (httpConn.getOutputStream());
            wr.write (dataRequest);
            wr.flush();
        }
        catch (IOException e) {
            Log.e (TAG, "Problem in opening the connection", e);
            throw new IOException (e);
        }
    }

    RegisterResult getResponse (String password) throws IOException
    {
        int responseCode = httpConn.getResponseCode();
        Log.d(TAG, "Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = new BufferedInputStream (httpConn.getInputStream());
            String response = Utils.convertStreamToString (is);
            Log.d (TAG, "Response: " + response);

            is.close();
            httpConn.disconnect();

            try {
                JSONObject jsonResponse = new JSONObject (response);

                boolean res = jsonResponse.getBoolean ("res");
                if (!res) {
                    throw new IOException ("Problem in registering the user: res=false");
                }

                String num = jsonResponse.getString ("num");
                switch (num) {
                    case "s001":
                        return new RegisterResult (true, R.string.registerUserSuccessful, true,
                                extractInfo (jsonResponse.getJSONObject ("tec"), password));

                    // TODO: 12/10/15: MANAGE ERRORS! (the download was not successful because res=false

                    default:
                        throw new JSONException ("Wrong num value " + num);
                }
            }
            catch (JSONException e) {
                Log.e (TAG, "Problem in parsing the response", e);
                throw new IOException (e);
            }
        }

        throw new IOException ("Received response code " + responseCode + " instead of " +
                HttpURLConnection.HTTP_OK);
    }

    private LoginTec extractInfo (JSONObject tec, String password) throws JSONException
    {
        if (tec == null) {
            throw new JSONException ("The tec object in the json is null");
        }

        Gson gson = new Gson();
        LoginTec loginTec = gson.fromJson (tec.toString(), LoginTec.class);
        loginTec.setPw (password);

        return loginTec;
    }
}
