package it.sudchiamanord.adoptionmngr.ops.mediator.requests;

/**
 * Created by rita on 12/27/18.
 */
public class Details
{

    private String stato;
    private String email;
    private String paswd;

    /**
     *
     * @return
     * The stato
     */
    public String getStato()
    {
        return stato;
    }

    /**
     *
     * @param stato
     * The stato
     */
    public void setStato (String stato)
    {
        this.stato = stato;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The paswd
     */
    public String getPaswd() {
        return paswd;
    }

    /**
     *
     * @param paswd
     * The paswd
     */
    public void setPaswd(String paswd) {
        this.paswd = paswd;
    }
}

