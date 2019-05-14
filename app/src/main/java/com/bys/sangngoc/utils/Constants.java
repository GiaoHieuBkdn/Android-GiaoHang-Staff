package com.bys.sangngoc.utils;

/**
 * Created by Admin on 3/9/2018.
 */

public class Constants {
    public static double LATITUDE = -1;
    public static double LONGITUDE = -1;
    public static int ROLE_DRIVER = 0;
    public static int ROLE_CONSTRUCTION = 1;

    public static String STATUS_PROCESSING = "Inprogress";
    public static String STATUS_DONT_PROCESSING = "New";
    public static String STATUS_CANCEL = "Canceled";
    public static String STATUS_TROUBLE = "Issued";
    public static String STATUS_PROCESSED = "Completed";
    public static String STATUS_FAILDED = "Failed";
    public static String STATUS_COMPLETE_NEED_ACCEPT = "CompletedNeedAccepting";
    public static String STATUS_COMPLETE_ACCEPTED = "CompletedAccepted";

    public static String STATUS_WORK_WILL_PROCESS = "New";
    public static String STATUS_WORK_PROCESSING = "Inprogress";
    public static String STATUS_WORK_PROCESSED = "Completed";
    public static String STATUS_WORK_TROUBLE = "Issued";
    public static String STATUS_WORK_CANCEL = "Canceled";
    public static String STATUS_WORK_CHECKING = "Auditing";

    public static String PREF_USER_PROFILE = "PREF_USER_PROFILE";
    public static String EXTRAX_DELIVERY_POINT = "EXTRAX_DELIVERY_POINT";
    public static String EXTRAX_PRODUCTS_LIST = "EXTRAX_PRODUCTS_LIST";
    public static String EXTRAX_FROM_REWARD_DISCIPLINE = "EXTRAX_FROM_REWARD_DISCIPLINE";
    public static String EXTRAX_CURRENT_BRANCH = "EXTRAX_CURRENT_BRANCH";
    public static final String CHARSET = "UTF-8";
    public static int FAILURE_SESSION_EXPIRED = 2;
    public static String PREF_SESSION_ID = "PREF_SESSION_ID";
    public static String PREF_USER_ID = "PREF_USER_ID";
    public static String PREF_ROLE = "PREF_ROLE";
    public static String PREF_EMPLOYEES_ID = "PREF_EMPLOYEES_ID";
    public static String PREF_GENERAL_DATA = "PREF_GENERAL_DATA";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD_NAME = "password";
    public static final String EXTRAX_EMAIL = "email";
    public static final String EXTRAX_TOKEN_CODE = "token_code";
    public static final String EXTRAX_ROLE = "EXTRAX_ROLE";
    public static final String EXTRAX_PROJECT = "EXTRAX_PROJECT";
    public static final String EXTRAX_WORK = "EXTRAX_WORK";
    public static final String EXTRAX_TYPE = "TYPE";
    public static final String EXTRAX_ID = "ID";
    public static final String EXTRAX_POSITION = "EXTRAX_POSITION";
    public static final String EXTRAX_STATUS = "EXTRAX_STATUS";
    public static final String EXTRAX_DATE_DELIVERY = "EXTRAX_DATE_DELIVERY";
    public static final String EXTRAX_LATITUDE = "EXTRAX_LATITUDE";
    public static final String EXTRAX_LONGITUDE = "EXTRAX_LONGITUDE";
    public static final String EXTRAX_ADDRESS = "EXTRAX_ADDRESS";
    public static final String EXTRAX_IS_FINISHED = "EXTRAX_IS_FINISHED";
    public static final String EXTRAX_IS_DELETED = "EXTRAX_IS_DELETED";
    public static final String EXTRAX_IMAGES = "EXTRAX_IMAGES";
    public static final String EXTRAX_FROM_HISTORY = "EXTRAX_FROM_HISTORY";
    public static final String EXTRAX_HASHMAP_TIME = "EXTRAX_HASHMAP_TIME";
    public static final String EXTRAX_ABSENT = "absent";
    public static final String EXTRAX_ABSENT_CHANGE_STATUS_OR_DELETE = "absent_send_status_or_delete";
    public static final String EXTRAX_USER = "user";
    public static String PREF_RECENT_ABSENT = "PREF_RECENT_ABSENT";

    public static final String BROADCAST_CHANGE_STATUS = "BROADCAST_CHANGE_STATUS";
    public static final String BROADCAST_CHANGE_STATUS_TASK = "BROADCAST_CHANGE_STATUS_TASK";
    public static final String BROADCAST_CHANGE_CURRENT_LOCATION = "BROADCAST_CHANGE_CURRENT_LOCATION";

    //Code
    public static int CODE_SUCCESS = 0;
    public static int LIMIT_ITEMS = 10;

    public enum STATUS_WORK {
        WillAccept, Accepting, Checking, Problem, Done, Cancel
    }
    public static final String APPROVE = "APPROVE";
    public static final String UNAPPROVE = "UNAPPROVE";
}
