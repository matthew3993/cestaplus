package sk.cestaplus.cestaplusapp.extras;

/**
 * Created by matth on 18.02.2017.
 * Link on DOKUMENTATION: https://docs.google.com/document/d/1wy2hv0NAJNKzEhLux-p2Smb9JF3dsEZuCzZun6xQa4U/edit
 */
public interface IErrorCodes {
    //ROLE CODES
    int ROLE_UNDEFINED = -1;
    int ROLE_DEFAULT_VALUE = 50;
    int ROLE_NOT_LOGGED = 0;
    int ROLE_LOGGED_SUBSCRIPTION_OK = 1;       // ONLY role with permission to read locked articles // we CAN use role > 0 check to see if user is logged in
    int ROLE_LOGGED_SUBSCRIPTION_EXPIRED = 2;

    // LOGIN error codes
    int LOGIN_SUCCESSFUL = 0;           // => role logged subscription OK
    int LOGIN_PARTIALLY_SUCCESSFUL = 1; // => role logged subscription expired
    int EMAIL_OR_PASSWORD_MISSING = 11;
    int WRONG_EMAIL_OR_PASSWORD = 12;
    int SERVER_INTERNAL_ERROR = 13;

    // ARTICLE error codes - AEC
    int AEC_OK = 0;             // every thing OK, sending entire article
    int AEC_NO_API_KEY = 1;     // api key was not send in request, user is NOT logged = sending only public part of article
    int AEC_API_KEY_ERROR = 2;  // api key WAS send in request, but there is some error with it = sending only public part of article

    // other error codes
    int JSON_ERROR = 4; // parsing error

    //notification IDs
    int NOTIFICATION_API_KEY_TEST = 5;
}
