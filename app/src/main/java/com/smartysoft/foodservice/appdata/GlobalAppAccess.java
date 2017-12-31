package com.smartysoft.foodservice.appdata;



public class GlobalAppAccess {

    public static String BaseUrl = "http://123.49.38.253:8084/bgb/";
    //public static String BaseUrl = "https://b5e99a4d.ngrok.io/bgb/";
    public static final String URL_LOGIN = BaseUrl +   "userLogin";
    public static final String URL_PILLAR_UPDATE = BaseUrl + "updatePillar";
    public static final String URL_PILLAR_INFO =  BaseUrl + "getPillarNames";
    public static final String URL_SOLDIER_LOCATION = BaseUrl +  "updateSoldierLocation";
    public static final String URL_REPORT_TO_BGB =  BaseUrl + "reportToBGB";
    public static final String URL_APP_ACTIVE =  "http://besafebd.com/server/checkApp.php";
    public static final String URL_CHECK_APP_UPDATE =  BaseUrl +  "updateApp";


    public static final  int SUCCESS = 1;
    public static  final  int ERROR = 0;



}
