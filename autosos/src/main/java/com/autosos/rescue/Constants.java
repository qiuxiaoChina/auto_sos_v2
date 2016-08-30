package com.autosos.rescue;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Constants {
    public static final boolean DEBUG = false; // 开发模式是 true 的时候可以动态修改服务器地址
    public static final String APP_VERSION = "2.0.1.3";
    public static final int VERSION_CODE = 109;
    //public static String HOST = "http://api.auto-sos.com.cn/";
    //public static String HOST = "http://autosos.wicp.net:46622/";//测试服务器
    //public static String HOST = "http://api.test.auto-sos.cn/";//测试服务器
    public static String HOST = "http://api.auto-sos.net/";//测试服务器
   // public static String HOST = "http://api.auto-sos.cn/";//正式服务器

    public static final String ACTIONNAME_STRING = "com.igexin.sdk.action.3gcCKQHtdt9JMY0IQ4zQn";

    public static final String DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    public static final String PREF_FILE = "pref";

    public static final int DEFAULT_IMAGE_SIZE = 960;
    public static final int PLACEHOLDER = R.drawable.icon_image_l;
    public static final int PLACEHOLDER_NORMAL = R.drawable.icon_default;
    public static final int PLACEHOLDER_AVATAR = R.drawable.icon_avatar;
    public static final int PLACEHOLDER_AVATAR2 = R.drawable.bg_white2;
    public static final Executor LISTTHEADPOOL = Executors.newFixedThreadPool(5);
    public static final Executor THEADPOOL = Executors.newFixedThreadPool(10);
    public static final Executor INFOTHEADPOOL = Executors.newFixedThreadPool(3);

    public static String FEED_BACK_URL = HOST+"v1/orders/feedback";
    public static String PAY_CASH_URL = HOST +"v1/orders/%s/pay-for-another";
    public static String USER_INFO = HOST +"v1/user/user-detail";              //登出状态
    public static String CHECK_YOU_ARE_THE_ONLY_ONE = HOST+"v1/orders/get-device";//检查用户只被一个设备使用
    public static String NO_NEED_PAY = HOST+"v1/orders/%s/get-support-free";//提交车主评价
    public static String TRAILER_INFO_URL = HOST+"v1/user/trail-detail";//获取车辆信息
    public static String SUBMIT_TRAILER_INFO_URL = HOST+"v1/user/trail";//提交车辆信息
    public static String CLOSE_ORDER_URL = HOST+"v1/orders/%s/close-order";//取消订单
    public static String CHECK_ORDER_STATUS_URL = HOST+"v1/orders/%s/get-order-status";//获取订单状态。
    public static String CHECK_IF_ONLINE_URL = HOST+"v1/orders/get-status";//检查用户是否上线
    public static String ACCESS_TOKEN_URL = HOST + "v1/auth/get-access-token";  //token获取
    public static  String GET_AUTH_NUMBER = HOST + "v1/auth/device-verify-code"; //验证手机号
    public static String PERSON_URL = HOST + "v1/user/info";    //个人信息
    public static String ORDERS_URL = HOST + "v1/orders/refresh-list";  //所以订单信息
    public static String ACCEPT_ORDER_URL = HOST + "v1/orders/%s/acceptance";   //接单操作
    public static String USER_LOCATION_URL = HOST + "v1/user/update-status";    //心跳更新经纬度接口
    public static String USER_START_WORK_URL = HOST + "v1/user/start-work";     //开始接单状态
    public static String USER_STOP_WORK_URL = HOST +"v1/user/stop-work";        //停止接单状态
    public static String USER_LOGOUT_URL = HOST +"v1/user/logout";              //登出状态
    public static String UPLOAD_PICS_URL = HOST + "v1/orders/%s/upload-pic";    //id号的图片上传接口
    public static String HISTORY_ORDERS_URL = HOST + "v1/orders/history-list?page=%s&per-page=%s";  //历史订单的某页及某分页的订单数量
    public static String ORDER_INFO_URL = HOST + "v1/orders/%s/detail";   //id号的订单详情
    public static String PHOTOS_URL = HOST + "v1/orders/%s/pics";   //id号的订单图片
    public static String SUBMIT_TUOCHE_URL = HOST + "v1/orders/%s/tuoche-distance-submit"; //id号订单的拖车距离提交接口
    public static String PAY_URL = HOST + "v1/orders/%s/get-pay-charge";    //id号订单的确认收费
    public static String ARRIVE_SUBMIT_URL = HOST + "v1/orders/%s/arrive-submit";   //id号订单 到达现场按钮
    public static String ORDER_ORDER_START_URL = HOST + "v1/orders/%s/start-reserved-order";//id号预约订单，开始处理按钮


    public static String QINIU_TOKEN_URL = HOST + "qiniu/get-token";        //获取七牛token
    public static String NOTICE_URL = HOST +"v1/bulletins";                 //通知接口
    public static String NOTICE_CONTENT_URL = HOST + "v1/bulletins/%s";     //id号通知接口
    public static String NOTICE_I_GOT = HOST + "v1/bulletins/%s/i-got";     //已读id号通知
    public static String NOTICE_LAST_NOTICE = HOST +"v1/bulletins/get-last-one";//获取最新一条通知
    public static String HISTORY_ORDERS_PUSH = HOST +"v1/orders/history-pushed-list?page=%s&per-page=%s";//历史推送记录的某页及某分页的订单数量
    public static String DRAG_START = HOST +"v1/orders/%s/start-tuoche";    //id号订单开始拖车
    public static String ORDERS_DIAL = HOST +"v1/orders/%s/dial";           //id号订单拨打电话
    public static String BUG_REPORT = HOST + "v1/bug/report";               //异常捕捉上传接口
    public static  String CHANGE_PASSWORD = HOST + "v1/user/changepwd";
    public static  String GET_BALANCE = HOST + "v1/balance";                //获取余额各种信息
    public static  String GET_BALANCE_ONLY = HOST + "v1/balance/total";     //仅获取余额
    public static  String GET_BANK_INFO = HOST + "v1/balance/bankcard-info";     //获取银行卡信息
    public static  String TAKECASH = HOST + "v1/balance/takecash-apply";     //提现
    public static  String MONTH_BILL = HOST + "v1/balance/bill";     //月收入支出
    public static  String GET_MONTH_BILL = HOST + "v1/balance/bill/%s/%s";     //获取月收入支出
    public static  String GET_TOTAL_AMOUNT = HOST + "v1/settle-list/total-amount";     //获取月收入支出
    public static  String GET_SETTLE_LIST= HOST + "v1/settle-list/";     //结算报表详细
    public static  String GET_MONTH_LIST= HOST + "v1/settle-list/view-orders/%s";     //结算报表详细
    public static  String GET_ORDER_FEE= HOST + "v1/settle-list/order-fee-detail/%s";     //结算报表详细
    public static  String GET_SEND_CODE = HOST + "v1/user/change-mobile-verify-code";    //发送修改手机号码验证码
    public static  String GET_CHANGE_NUMBER = HOST + "v1/user/change-mobile";               //修改手机号码


    public static String CHECK_IS_PAID = HOST+ "v1/orders/%s/get-pay-stat";  //获取支付状态
    public static String UPLOAD_COMMENT_SIGN = HOST+"v1/orders/%s/upload-pay-pic";//提交车主签名
    public static String GET_COMPANY_INFO = HOST+"v1/orders/%s/service-company";//在评价界面 获取服务商信息
    public static String GET_ORDER_END_INFO = HOST+ "v1/orders/%s/service-detail"; //在订单完成界面显示的信息
    public static String SUBMIT_COMMENT = HOST+"v1/orders/%s/comment-submit";//提交车主评价

    public static String CONFIRM_THE_SERVICE_COMPLETED = HOST+"v1/orders/%s/get-completed";//获取微信端用户确认到达救援现场
    public static String CONFIRM_USER_PAID = HOST+"v1/orders/%s/get-is-cash";//判断微信端用户是否现金 或者 微信支付

    public static final String USER_FILE = "user.json";                     //存放用户信息（账号密码以及token）
    public static final String LOCATION_DARG = "drag.txt";                  //记录拖车轨迹
    public static final String LOCATION_ARRIVE = "arrive.txt";                //记录行车轨迹
    public static final String LOCATION_FILE = "location.json";
   // public static final String PHOTO_URL = "?imageMogr2/gravity/Center/crop/%sx%s/format/wedp";
    public static final String PHOTO_URL = "?imageView2/1/w/%s/h/%s/format/jpg"; //图片详情
   // public static final String PHOTO_URL2 = "?imageMogr2/thumbnail/!%sx%sr/format/wedp";
    public static final String PHOTO_URL2 = "?imageView2/2/w/%s/format/jpg";


    public static class RequestCode {
        public static final int USER_LOGOUT = 1;
        public static final int  PHOTO_FROM_GALLERY = 2;
        public static final int PHOTO_FROM_CAMERA = 3;
    }

    public static void setHOST(String HOST) {
        com.autosos.rescue.Constants.HOST = HOST;
        ACCESS_TOKEN_URL = HOST + "v1/auth/get-access-token";
        PERSON_URL = HOST + "v1/user/info";
        ORDERS_URL = HOST + "v1/orders/refresh-list";
        ACCEPT_ORDER_URL = HOST + "v1/orders/%s/acceptance";
        USER_LOCATION_URL = HOST + "v1/user/update-status";
        USER_START_WORK_URL = HOST + "v1/user/start-work";
        USER_STOP_WORK_URL = HOST +"v1/user/stop-work";
        USER_LOGOUT_URL = HOST +"v1/user/logout";
        UPLOAD_PICS_URL = HOST + "v1/orders/%s/upload-pic";
        HISTORY_ORDERS_URL = HOST + "v1/orders/history-list?page=%s&per-page=%s";
        ORDER_INFO_URL = HOST + "v1/orders/%s/detail";
        PHOTOS_URL = HOST + "v1/orders/%s/pics";
        SUBMIT_TUOCHE_URL = HOST + "v1/orders/%s/tuoche-distance-submit";
        PAY_URL = HOST + "v1/orders/%s/get-pay-charge";
        ARRIVE_SUBMIT_URL = HOST + "v1/orders/%s/arrive-submit";
        QINIU_TOKEN_URL = HOST + "qiniu/get-token";
        NOTICE_URL = HOST +"v1/bulletins";
        NOTICE_CONTENT_URL = HOST + "v1/bulletins/%s";
        NOTICE_I_GOT = HOST + "v1/bulletins/%s/i-got";
        NOTICE_LAST_NOTICE = HOST +"v1/bulletins/get-last-one";
        HISTORY_ORDERS_PUSH = HOST +"v1/orders/history-pushed-list?page=%s&per-page=%s";
        DRAG_START = HOST +"v1/orders/%s/start-tuoche";
        ORDERS_DIAL = HOST + "v1/orders/%s/dial";
        BUG_REPORT = HOST + "v1/bug/report";
        ORDER_ORDER_START_URL = HOST + "v1/orders/%s/start-reserved-order";
        CHANGE_PASSWORD = HOST + "v1/user/changepwd";
    }

}
