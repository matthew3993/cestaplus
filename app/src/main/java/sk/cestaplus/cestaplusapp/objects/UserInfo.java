package sk.cestaplus.cestaplusapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by matt on 05. 05. 2017.
 * Information about user loaded from API.
 * Naming according to API.
 */
public class UserInfo
        implements Parcelable {

    /**
     * Only first name.
     */
    private String name; //Only first name
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

    /**
     * WARNING!: MUST same order as in 'writeToParcel()' method
     */
    public UserInfo(Parcel in) {
        this.name = in.readString();
        this.surname = in.readString();
        this.subscription_start = new Date(in.readLong());
        this.subscription_end = new Date(in.readLong());
        this.subscription_name = in.readString();

    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName(){
        return String.format("%s %s", name, surname);
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

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * WARNING!: MUST same order as in 'UserInfo(Parcel in)' constructor
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(surname);
        parcel.writeLong(subscription_start.getTime());
        parcel.writeLong(subscription_end.getTime());
        parcel.writeString(subscription_name);
    }

    public static final Parcelable.Creator<UserInfo> CREATOR
            = new Parcelable.Creator<UserInfo>() {

        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
