package sk.cestaplus.cestaplusapp.extras;

/**
 * Created by matth on 18.02.2017.
 */
public interface IErrorCodes {
    //ROLE CODES
    int ROLE_DEFAULT_VALUE = 50;
    int ROLE_NOT_LOGGED = 0;
    int ROLE_LOGGED = 1;    // we CAN use role > 0 check to see if user is logged in //RENAME TO: ROLE_LOGGED_SUBSCRIPTION_OK
    int ROLE_LOGGED_SUBSCRIPTION_EXPIRED = 2; // not use for now

    // LOGIN error codes
    int LOGIN_SUCCESSFUL = 0;
    int EMAIL_OR_PASSWORD_MISSING = 1;
    int WRONG_EMAIL_OR_PASSWORD = 2;
    int SERVER_INTERNAL_ERROR = 3;

    // ARTICLE error codes - AEC
    int AEC_OK = 0;             // every thing OK, sending entire article
    int AEC_NO_API_KEY = 1;     // api key was not send in request, user is NOT logged = sending only public part of article
    int AEC_API_KEY_ERROR = 2;  // api key WAS send in request, but there is some error with it = sending only public part of article

    // other error codes
    int JSON_ERROR = 4; // parsing error
}
