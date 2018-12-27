package it.sudchiamanord.adoptionmngr.ops.results;

import it.sudchiamanord.adoptionmngr.ops.mediator.responses.LoginTec;

/**
 * Created by rita on 12/17/18.
 */
public class RegisterResult extends GeneralResult
{
    public RegisterResult (boolean success, int message, boolean correctSession, LoginTec loginTec)
    {
        super (success, message, correctSession);
        mUser = loginTec.getEmail();
        mPassword = loginTec.getPw();
        mSessionKey = loginTec.getSessionKey();
        mSchoolYear = loginTec.getAnnoa();
    }

    public RegisterResult (boolean success, int message, boolean correctSession, String user)
    {
        super (success, message, correctSession);
        mUser = user;
        mPassword = null;
        mSessionKey = null;
        mSchoolYear = null;
    }

    public String getUser()
    {
        return mUser;
    }

    public String getPw()
    {
        return mPassword;
    }

    public String getSessionKey()
    {
        return mSessionKey;
    }

    public String getSchoolYear()
    {
        return mSchoolYear;
    }

    private final String mUser;
    private final String mPassword;
    private final String mSessionKey;
    private final String mSchoolYear;
}
