package it.sudchiamanord.adoptionmngr.ops.mediator.requests;

/**
 * Created by rita on 12/27/18.
 */
public enum Action
{
    kidList ("getListBambini"),
    login ("login");

    Action (String actionName)
    {
        _name = actionName;
    }

    public String value()
    {
        return _name;
    }

    private String _name;
}
