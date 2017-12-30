package com.smartysoft.foodservice.appdata;



public class GlobalAppAccess {

    private static final String BASE_URL = "http://longlife.com.ng/longlife/api/";

    public static final String URL_LOGIN = BASE_URL + "login.php";
    public static final String URL_REGISTER = BASE_URL + "register.php";
    public static final String URL_ALL_CATEGORYLIST = BASE_URL + "allCategories.php";
    public static final String URL_SELECT_CATEGORY = BASE_URL + "userSelectedCategories.php";
    public static final String URL_USER_CATEGORYLIST = BASE_URL + "user_categorylist.php";
    public static final String URL_SERVICE = BASE_URL + "servicelist.php";
    public static final String URL_STATES = BASE_URL + "nigerian_states.php";
    public static final String URL_LOCAL_GOVT = BASE_URL + "local_govt.php";
    public static final String URL_LOCAL_STATES_GOVTS = BASE_URL + "states_govts.php";
    public static final String URL_FORGOT_PASSWORD = BASE_URL + "forgotPassword.php";
    public static final String URL_CHANGE_PASSWORD = BASE_URL + "changePassword.php";


    public static final  int SUCCESS = 1;
    public static  final  int ERROR = 0;



}
