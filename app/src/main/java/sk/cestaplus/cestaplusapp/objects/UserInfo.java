package sk.cestaplus.cestaplusapp.objects;

import java.util.Date;

/**
 * Created by matt on 05. 05. 2017.
 * Information about user loaded from API.
 * Naming according to API.
 */
public class UserInfo {
    private String name;
    private String surname;
    private Date subscription_start;
    private Date subscription_end;
    private String subscription_name;

    public UserInfo(String name, String surname, Date subscription_start, Date subscription_end, String subscription_name) {
        this.name = name;
        this.surname = surname;
        this.subscription_start = subscription_start;
        this.subscription_end = subscription_end;
        this.subscription_name = subscription_name;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Date getSubscription_start() {
        return subscription_start;
    }

    public Date getSubscription_end() {
        return subscription_end;
    }

    public String getSubscription_name() {
        return subscription_name;
    }
}
