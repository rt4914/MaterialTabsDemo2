package com.advanced.education.admin.wisel.utilities;

public class Constants {

    //PRODUCTION
//    public static final String BASE_SERVER_URL = "http://139.59.85.105:5901/api/";
    //TESTING
    public static final String BASE_SERVER_URL = "http://139.59.85.105:6999/v1/";
//    public static final String BASE_SERVER_URL = "http://192.168.0.7:2999/v1/";

    public static final String CONTENT_HEADER = "application/x-www-form-urlencoded";
    //public static final String CONTENT_HEADER = "application/json";

    public static final String DATE_FORMAT = "dd MMM yyyy";
    public static final String DATE_FORMAT_DAY = "dd";
    public static final String DATE_FORMAT_WITHOUT_YEAR = "dd MMM";
    public static final String TIME_FORMAT = "hh: mm a";

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

    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_RETURNED = "RETURNED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";
    public static final String ORDER_STATUS_PLACED = "PLACED";
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDER_STATUS_READY_TO_DISPATCH = "READY TO DISPATCH";
    public static final String ORDER_STATUS_ON_DELIVERY = "ON DELIVERY";
    public static final String ORDER_STATUS_CUSTOMER_ABSENT = "CUSTOMER ABSENT";
    public static final String ORDER_STATUS_CLOSED = "CLOSED";

    public static final String SP_KEY_APP_LATEST_VERSION = "APP_UPDATE";
    public static final String SP_KEY_LATEST_DATA_TIMESTAMP = "LATEST_TIMESTAMP";
    public static final String SP_KEY_FIREBASE_TOKEN = "FIREBASE_TOKEN";
    public static final String SP_KEY_LOGGED_IN = "LOGGED_IN";
    public static final String SP_KEY_FIRST_TIME = "FIRST_TIME";
    public static final String SP_KEY_PHONE_NUMBER = "PHONE_NUMBER";
    public static final String SP_KEY_PASSWORD = "PASSWORD";
    public static final String SP_KEY_AUTH_TOKEN = "AUTH_TOKEN";
    public static final String SP_PREFERENCES_NAME = "100_CENT_PREFERENCES";
    public static final String SP_KEY_SIGN_IN = "SIGN_IN";
    public static final String SP_KEY_TEACHER_ID = "TEACHER_ID";
    public static final String SP_KEY_TEACHER_NAME = "TEACHER_NAME";
    public static final String SP_KEY_TEACHER_EMAIL = "TEACHER_EMAIL";
    public static final String SP_KEY_TEACHER_IMAGE = "TEACHER_IMAGE";
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
    public static final String API_CONTACT_US = "API_CONTACT_US";
    public static final String API_UPDATE_CUSTOMER = "API_UPDATE_CUSTOMER";
    public static final String API_ADD_CUSTOMER_ADDRESS = "API_ADD_CUSTOMER_ADDRESS";
    public static final String API_UPDATE_CUSTOMER_ADDRESS = "API_UPDATE_CUSTOMER_ADDRESS";
    public static final String API_GET_CUSTOMER_ADDRESS = "API_GET_CUSTOMER_ADDRESS";
    public static final String API_DELETE_CUSTOMER_ADDRESS = "API_DELETE_CUSTOMER_ADDRESS";
    public static final String API_GET_BANNER_SLIDER = "API_GET_BANNER_SLIDER";
    public static final String API_GET_PROPERTIES = "API_GET_PROPERTIES";
    public static final String API_GET_CATEGORIES = "API_GET_CATEGORIES";
    public static final String API_GET_FILTER_PRODUCTS = "API_GET_FILTER_PRODUCTS";
    public static final String API_ADD_WISHLIST = "API_ADD_WISHLIST";
    public static final String API_DELETE_WISHLIST = "API_DELETE_WISHLIST";
    public static final String API_GET_WISHLIST = "API_GET_WISHLIST";
    public static final String API_ADD_CART = "API_ADD_CART";
    public static final String API_ADD_VIEWS = "API_ADD_VIEWS";
    public static final String API_DELETE_CART = "API_DELETE_CART";
    public static final String API_GET_CART = "API_GET_CART";
    public static final String API_UPDATE_CART = "API_UPDATE_CART";
    public static final String API_ADD_RATING_REVIEW = "API_ADD_RATING_REVIEW";
    public static final String API_GET_RATING_REVIEW = "API_GET_RATING_REVIEW";
    public static final String API_GENERATE_INVOICE = "API_GENERATE_INVOICE";
    public static final String API_UPDATE_PRODUCT_PROPERTY = "API_UPDATE_PRODUCT_PROPERTY";

    public static final String API_ADD_ORDER = "API_ADD_ORDER";
    public static final String API_GET_ORDER = "API_GET_ORDER";
    public static final String API_ADD_ORDER_STATUS = "API_ADD_ORDER_STATUS";
    public static final String API_GET_CUSTOMER_INITIAL_DATA = "API_GET_CUSTOMER_INITIAL_DATA";
    public static final String API_GET_GLOBAL_INITIAL_DATA = "API_GET_GLOBAL_INITIAL_DATA";
    public static final String API_GET_TRENDING_LIST = "API_GET_TRENDING_LIST";
    public static final String API_GET_MASS_NOTIFICATION = "API_GET_MASS_NOTIFICATION";
    public static final String API_DELETE_MASS_NOTIFICATION = "API_DELETE_MASS_NOTIFICATION";

    public static final String ACTIVITY_MODE_ADD = "ADD";
    public static final String ACTIVITY_MODE_EDIT = "EDIT";
    public static final String ACTIVITY_MODE_VIEW = "VIEW";

}
