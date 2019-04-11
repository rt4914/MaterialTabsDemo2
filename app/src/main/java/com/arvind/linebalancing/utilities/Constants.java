package com.arvind.linebalancing.utilities;

public class Constants {

    //PRODUCTION

    //TESTING
    public static final String BASE_SERVER_URL = "http://139.59.85.105:2299/api/";

    public static final String CONTENT_HEADER = "application/x-www-form-urlencoded";
    //public static final String CONTENT_HEADER = "application/json";

    public static final String DATE_FORMAT = "dd MMM yyyy";
    public static final String DATE_FORMAT_DAY = "dd";
    public static final String DATE_FORMAT_WITHOUT_YEAR = "dd MMM";
    public static final String TIME_FORMAT = "hh: mm a";
    public static final String DATE_TIME_FORMAT_MYSQL = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final int REQUEST_CALL_ACCESS = 104;
    public static final int REQUEST_CAMERA_ACCESS = 101;
    public static final int REQUEST_LOCATION_ACCESS = 105;
    public static final int REQUEST_READ_STORAGE_ACCESS = 102;
    public static final int REQUEST_WRITE_STORAGE_ACCESS = 103;

    public static final int IMAGE_WIDTH_RATIO = 3;
    public static final int IMAGE_HEIGHT_RATIO = 2;

    public static final int DELIVERY_CHARGES = 35;
    public static final double SGST_TAX_PERCENTAGE = 2.5;
    public static final double CGST_TAX_PERCENTAGE = 2.5;

    public static final float PROFILE_MAX_WIDTH = 360;
    public static final float PROFILE_MAX_HEIGHT = 360;

    public static final String URL_PPB_TERMS_CONDITIONS = "http://www.wisel.in";
    public static final String URL_PPB_PRIVACY_POLICY = "http://www.wisel.in";
    public static final String URL_PPB_PRIVACY_PRICING = "http://www.wisel.in";
    public static final String URL_PPB_PRIVACY_CONTACT_US = "http://www.wisel.in";
    public static final String URL_PPB_PRIVACY_CANCELLATION_AND_REFUND = "http://www.wisel.in";

    public static final int SELECT_CAMERA_OPTION = 11;
    public static final int SELECT_DOCUMENT_ACTIVITY_REQUEST = 12;
    public static final int SELECT_GALLERY_OPTION = 13;
    public static final int SELECT_STUDENT_REQUEST = 14;
    public static final int SELECT_TEACHER_REQUEST = 15;
    public static final int SELECT_COURSE_REQUEST = 16;
    public static final int SELECT_STANDARD_REQUEST = 17;
    public static final int SELECT_RECIPIENT_REQUEST = 18;
    public static final int SELECT_DESIGNATION_REQUEST = 19;
    public static final int SELECT_EMPLOYEE_REQUEST = 20;
    public static final int SELECT_OPERATION_REQUEST = 21;
    public static final int SELECT_MACHINE_REQUEST = 22;
    public static final int SELECT_LINE_REQUEST = 23;

    public static final int RESPONSE_CODE_SUCCESS = 100;
    public static final int RESPONSE_CODE_USER_NOT_FOUND = 201;
    public static final int RESPONSE_CODE_PASSWORD_NOT_SET = 210;
    public static final int RESPONSE_CODE_ID_EXISTS = 201;
    public static final int RESPONSE_CODE_INCORRECT_PASSWORD = 203;
    public static final int RESPONSE_CODE_MULTIPLE_PHONE = 202;
    public static final int RESPONSE_CODE_REFERRAL_USED = 203;
    public static final int RESPONSE_CODE_MISSING = 302;
    public static final int RESPONSE_CODE_NO_TOKEN = 402;
    public static final int RESPONSE_CODE_INVALID_TOKEN = 403;

    public static final String SELECT_FILE_TEXT = "Select File";
    public static final String SELECT_IMAGE_TEXT = "Select Image";
    public static final String SELECT_VIDEO = "Select Video";
    public static final String TAKE_PHOTO_TEXT = "Take Photo";

//    public static final long MAX_IMAGE_FILE_SIZE = 4000000; // 4MB
//    public static final int MAX_IMAGE_FILE_WIDTH = 2560;
//    public static final int MAX_IMAGE_FILE_HEIGHT = 1080;

    public static final String FEATURE_GET_ALL_FEATURES = "GET_ALL_FEATURES";
    public static final String FEATURE_LATEST_VERSION = "LATEST_VERSION";
    public static final String FEATURE_EMAIL_VERIFICATION = "EMAIL_VERIFICATION";
    public static final String FEATURE_PASSWORD_VERIFICATION = "PASSWORD_VERIFICATION";
    public static final String FEATURE_NEW_PASSWORD = "ADD_PASSWORD";
    public static final String FEATURE_FORGOT_PASSWORD = "FORGOT_PASSWORD";
    public static final String FEATURE_GET_LATEST_DATA = "LATEST_DATA";

    //These two below mentioned constants will always be uses in combination with above feature based errors.
    //Example: JSON_ERROR + " " + FEATURE_EMAIL_VERIFICATION
    public static final String JSON_ERROR = "JSON_ERROR";
    public static final String VOLLEY_ERROR = "VOLLEY_ERROR";
    public static final int JSON_ERROR_CODE = 30;
    public static final int VOLLEY_ERROR_CODE = 50;

    public static final String SP_KEY_APP_LATEST_VERSION = "APP_UPDATE";
    public static final String SP_KEY_LATEST_DATA_TIMESTAMP = "LATEST_TIMESTAMP";
    public static final String SP_KEY_FIREBASE_TOKEN = "FIREBASE_TOKEN";
    public static final String SP_KEY_LOGGED_IN = "LOGGED_IN";
    public static final String SP_KEY_FIRST_TIME = "FIRST_TIME";
    public static final String SP_KEY_PHONE_NUMBER = "PHONE_NUMBER";
    public static final String SP_KEY_EMAIL = "EMAIL";
    public static final String SP_KEY_PASSWORD = "PASSWORD";
    public static final String SP_KEY_AUTH_TOKEN = "AUTH_TOKEN";
    public static final String SP_PREFERENCES_NAME = "100_CENT_PREFERENCES";
    public static final String SP_KEY_SIGN_IN = "SIGN_IN";
    public static final String SP_KEY_TEACHER_ID = "TEACHER_ID";
    public static final String SP_KEY_ADMIN_NAME = "ADMIN_NAME";
    public static final String SP_KEY_ADMIN_EMAIL = "ADMIN_EMAIL";
    public static final String SP_KEY_ADMIN_IMAGE = "ADMIN_IMAGE";
    public static final String SP_KEY_INSTITUTE_ID = "INSTITUTE_ID";
    public static final String SP_KEY_LATEST_GALLERY_TIMESTAMP = "GALLERY_TIMESTAMP";
    public static final String SP_KEY_LATEST_MESSAGE_TIMESTAMP = "MESSAGE_TIMESTAMP";
    public static final String SP_KEY_LATEST_MEAL_TIMESTAMP = "MEAL_TIMESTAMP";
    public static final String SP_KEY_LATEST_PARENT_REMARK_TIMESTAMP = "PARENT_REMARK_TIMESTAMP";
    public static final String SP_KEY_LATEST_TEST_RESULTS_TIMESTAMP = "TEST_RESULTS_TIMESTAMP";

    public static final String FONT_TYPE_BOLD = "fonts/Dosis-Bold.ttf";
    public static final String FONT_TYPE_EXTRA_BOLD = "fonts/Dosis-ExtraBold.ttf";
    public static final String FONT_TYPE_EXTRA_LIGHT = "fonts/Dosis-ExtraLight.ttf";
    public static final String FONT_TYPE_LIGHT = "fonts/Dosis-Light.ttf";
    public static final String FONT_TYPE_MEDIUM = "fonts/Dosis-Medium.ttf";
    public static final String FONT_TYPE_REGULAR = "fonts/Dosis-Regular.ttf";
    public static final String FONT_TYPE_SEMI_BOLD = "fonts/Dosis-SemiBold.ttf";

    public static final int VIEW_DOCUMENT_ACTIVITY = 100;

    public static final String API_FILE_UPLOAD = "API_FILE_UPLOAD";

    public static final String API_LOGIN_TEACHER = "API_LOGIN_TEACHER";
    public static final String API_GET_COURSE_LIST = "API_GET_COURSE_LIST";
    public static final String API_ADD_COURSE = "API_ADD_COURSE";
    public static final String API_DELETE_COURSE = "API_DELETE_COURSE";
    public static final String API_SEND_OTP_PHONE = "API_SEND_OTP_PHONE";
    public static final String API_OTP_VERIFICATION = "API_OTP_VERIFICATION";
    public static final String API_ADD_STITCH = "API_ADD_STITCH";
    public static final String API_GET_STITCH = "API_GET_STITCH";
    public static final String API_UPDATE_STITCH = "API_UPDATE_STITCH";
    public static final String API_DELETE_STITCH = "API_DELETE_STITCH";
    public static final String API_ADD_OPERATION = "API_ADD_OPERATION";
    public static final String API_GET_OPERATION = "API_GET_OPERATION";
    public static final String API_UPDATE_OPERATION = "API_UPDATE_OPERATION";
    public static final String API_DELETE_OPERATION = "API_DELETE_OPERATION";
    public static final String API_ADD_MACHINE = "API_ADD_MACHINE";
    public static final String API_GET_MACHINE = "API_GET_MACHINE";
    public static final String API_UPDATE_MACHINE = "API_UPDATE_MACHINE";
    public static final String API_DELETE_MACHINE = "API_DELETE_MACHINE";
    public static final String API_ADD_LINE = "API_ADD_LINE";
    public static final String API_GET_LINE = "API_GET_LINE";
    public static final String API_UPDATE_LINE = "API_UPDATE_LINE";
    public static final String API_DELETE_LINE = "API_DELETE_LINE";
    public static final String API_ADD_DESIGNATION = "API_ADD_DESIGNATION";
    public static final String API_GET_DESIGNATION = "API_GET_DESIGNATION";
    public static final String API_UPDATE_DESIGNATION = "API_UPDATE_DESIGNATION";
    public static final String API_DELETE_DESIGNATION = "API_DELETE_DESIGNATION";
    public static final String API_ADD_EMPLOYEE = "API_ADD_EMPLOYEE";
    public static final String API_UPDATE_EMPLOYEE = "API_UPDATE_EMPLOYEE";
    public static final String API_GET_EMPLOYEE = "API_GET_EMPLOYEE";
    public static final String API_DELETE_EMPLOYEE = "API_DELETE_EMPLOYEE";
    public static final String API_ADD_EMPLOYEE_RATING = "API_ADD_EMPLOYEE_RATING";
    public static final String API_GET_EMPLOYEE_RATING = "API_GET_EMPLOYEE_RATING";
    public static final String API_UPDATE_EMPLOYEE_RATING = "API_UPDATE_EMPLOYEE_RATING";
    public static final String API_DELETE_EMPLOYEE_RATING = "API_DELETE_EMPLOYEE_RATING";
    public static final String API_ADD_EMPLOYEE_MACHINE_CONNECTION = "API_ADD_EMPLOYEE_MACHINE_CONNECTION";
    public static final String API_GET_EMPLOYEE_MACHINE_CONNECTION = "API_GET_EMPLOYEE_MACHINE_CONNECTION";
    public static final String API_UPDATE_EMPLOYEE_MACHINE_CONNECTION = "API_UPDATE_EMPLOYEE_MACHINE_CONNECTION";
    public static final String API_DELETE_EMPLOYEE_MACHINE_CONNECTION = "API_DELETE_EMPLOYEE_MACHINE_CONNECTION";
    public static final String API_ADD_MACHINE_OPERATION_CONNECTION = "API_ADD_MACHINE_OPERATION_CONNECTION";
    public static final String API_GET_MACHINE_OPERATION_CONNECTION = "API_GET_MACHINE_OPERATION_CONNECTION";
    public static final String API_UPDATE_MACHINE_OPERATION_CONNECTION = "API_UPDATE_MACHINE_OPERATION_CONNECTION";
    public static final String API_DELETE_MACHINE_OPERATION_CONNECTION = "API_DELETE_MACHINE_OPERATION_CONNECTION";
    public static final String API_ADD_EMPLOYEE_OPERATION_CONNECTION = "API_ADD_EMPLOYEE_OPERATION_CONNECTION";
    public static final String API_GET_EMPLOYEE_OPERATION_CONNECTION = "API_GET_EMPLOYEE_OPERATION_CONNECTION";
    public static final String API_UPDATE_EMPLOYEE_OPERATION_CONNECTION = "API_UPDATE_EMPLOYEE_OPERATION_CONNECTION";
    public static final String API_DELETE_EMPLOYEE_OPERATION_CONNECTION = "API_DELETE_EMPLOYEE_OPERATION_CONNECTION";
    public static final String API_DELETE_LINE_EMPLOYEE_CONNECTION = "API_DELETE_LINE_EMPLOYEE_CONNECTION";

    public static final String API_ADMIN_LOGIN = "API_ADMIN_LOGIN";

    public static final String ACTIVITY_MODE_ADD = "ADD";
    public static final String ACTIVITY_MODE_EDIT = "EDIT";
    public static final String ACTIVITY_MODE_VIEW = "VIEW";

    public static final String API_ADD_LINE_EMPLOYEE_CONNECTION = "API_ADD_LINE_EMPLOYEE_CONNECTION";
    public static final String API_UPDATE_LINE_EMPLOYEE_CONNECTION = "API_UPDATE_LINE_EMPLOYEE_CONNECTION";
    public static final String API_GET_LINE_EXECUTIVE_CONNECTION = "API_GET_LINE_EXECUTIVE_CONNECTION";
    public static final String API_GET_LINE_SUPERVISOR_CONNECTION = "API_GET_LINE_SUPERVISOR_CONNECTION";
    public static final String API_GET_LINE_EMPLOYEE_CONNECTION = "API_GET_LINE_SUPERVISOR_CONNECTION";
    public static final String API_UPDATE_LINE_EFFICIENCY = "API_UPDATE_LINE_EFFICIENCY";
    public static final String API_ADD_LINE_EFFICIENCY = "API_ADD_LINE_EFFICIENCY";
    public static final String API_GET_LINE_EFFICIENCY = "API_GET_LINE_EFFICIENCY";
    public static final String API_ADD_CONNECTION = "API_ADD_CONNECTION";
    public static final String API_GET_CONNECTION = "API_GET_CONNECTION";
    public static final String API_DELETE_CONNECTION = "API_DELETE_CONNECTION";
    public static final String API_UPDATE_CONNECTION = "API_UPDATE_CONNECTION";
    public static final String API_GET_SUGGESTION = "API_GET_SUGGESTION";
    public static final String API_SAVE_SUGGESTION = "API_SAVE_SUGGESTION";
}
