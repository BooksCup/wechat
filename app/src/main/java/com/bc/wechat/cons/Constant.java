package com.bc.wechat.cons;

/**
 * 常量类
 */
public class Constant {
    public static String PICTURE_DIR = "sdcard/wechat_/pictures/";

    //    public static final String BASE_URL = "http://49.4.25.11:8081/";
    public static final String BASE_URL = "http://192.168.0.133:8081/";

    public static final String USER_SEX_MALE = "1";
    public static final String USER_SEX_FEMALE = "2";

    public static final String IS_NOT_FRIEND = "0";
    public static final String IS_FRIEND = "1";

    public static final String FRIEND_APPLY_STATUS_ACCEPT = "1";

    // 用于推送的业务类型
    /**
     * 好友申请
     */
    public static final String PUSH_SERVICE_TYPE_ADD_FRIENDS_APPLY = "ADD_FRIENDS_APPLY";

    /**
     * 好友接收
     */
    public static final String PUSH_SERVICE_TYPE_ADD_FRIENDS_ACCEPT = "ADD_FRIENDS_ACCEPT";

    public static final String MSG_TYPE_TEXT = "text";
    public static final String MSG_TYPE_IMAGE = "image";
    public static final String MSG_TYPE_LOCATION = "location";
    public static final String MSG_TYPE_VOICE = "voice";
    public static final String MSG_TYPE_CUSTOM = "custom";
    public static final String MSG_TYPE_SYSTEM = "eventNotification";

    public static final String TARGET_TYPE_SINGLE = "single";
    public static final String TARGET_TYPE_GROUP = "group";
    public static final String TARGET_TYPE_CHATROOM = "chatroom";

    public static final int DEFAULT_PAGE_SIZE = 10;

    // 创建群聊方式
    public static final String CREATE_GROUP_TYPE_FROM_NULL = "1";

    public static final String CREATE_GROUP_TYPE_FROM_SINGLE = "2";

    public static final String CREATE_GROUP_TYPE_FROM_GROUP = "3";

    // 好友来源
    /**
     * 来自手机号搜索
     */
    public static final String FRIENDS_SOURCE_BY_PHONE = "1";

    /**
     * 来自微信号搜索
     */
    public static final String FRIENDS_SOURCE_BY_WX_ID = "2";

    /**
     * 来自附近的人
     */
    public static final String FRIENDS_SOURCE_BY_PEOPLE_NEARBY = "3";

    /**
     * 来自手机通讯录
     */
    public static final String FRIENDS_SOURCE_BY_CONTACT = "4";

    /**
     * 朋友权限（所有权限：聊天、朋友圈、微信运动等）
     * default
     */
    public static final String RELA_AUTH_ALL = "0";

    /**
     * 朋友权限（仅聊天）
     */
    public static final String RELA_AUTH_ONLY_CHAT = "1";

    /**
     * 朋友圈和视频动态-可以看我
     * default
     */
    public static final String RELA_CAN_SEE_ME = "0";

    /**
     * 朋友圈时视频动态-不让他看我
     */
    public static final String RELA_NOT_SEE_ME = "1";

    /**
     * 朋友圈和视频动态-可以看他
     * default
     */
    public static final String RELA_CAN_SEE_HIM = "0";

    /**
     * 朋友圈时视频动态-不看他
     */
    public static final String RELA_NOT_SEE_HIM = "1";

    /**
     * 非星标好友
     */
    public static final String RELA_IS_NOT_STAR_FRIEND = "0";

    /**
     * 星标好友
     */
    public static final String RELA_IS_STAR_FRIEND = "1";

    /**
     * 星标好友分组title
     */
    public static final String STAR_FRIEND = "星标朋友";

    /**
     * 用户微信号修改标记
     */
    public static final String USER_WX_ID_MODIFY_FLAG_TRUE = "1";

    /**
     * 地区类型-"省"
     */
    public static final String AREA_TYPE_PROVINCE = "1";

    /**
     * 地区类型-"市"
     */
    public static final String AREA_TYPE_CITY = "2";

    /**
     * 地区类型-"县"
     */
    public static final String AREA_TYPE_DISTRICT = "3";

    /**
     * 定位类型-地区信息
     * 获取省市区街道信息
     */
    public static final String LOCATION_TYPE_AREA = "0";

    /**
     * 定位类型-消息
     * 发送定位信息
     */
    public static final String LOCATION_TYPE_MSG = "1";

    public static final String DEFAULT_POST_CODE = "000000";

    // 登录方式
    /**
     * 手机号/密码登录
     */
    public static final String LOGIN_TYPE_PHONE_AND_PASSWORD = "0";

    /**
     * 手机号/验证码登录
     */
    public static final String LOGIN_TYPE_PHONE_AND_VERIFICATION_CODE = "1";

    /**
     * 微信号/QQ/邮箱登录
     */
    public static final String LOGIN_TYPE_OTHER_ACCOUNTS_AND_PASSWORD = "2";

    /**
     * 验证码业务类型-"登录"
     */
    public static final String VERIFICATION_CODE_SERVICE_TYPE_LOGIN = "0";

    /**
     * QQ号验证
     */
    /**
     * QQ号验证
     */
    /**
     * 未绑定
     */
    public static final String QQ_ID_NOT_LINK = "0";

    /**
     * 已绑定
     */
    public static final String QQ_ID_LINKED = "1";

    /**
     * 邮箱验证
     */
    /**
     * 未绑定
     */
    public static final String EMAIL_NOT_LINK = "0";

    /**
     * 未验证
     */
    public static final String EMAIL_NOT_VERIFIED = "1";

    /**
     * 已验证
     */
    public static final String EMAIL_VERIFIED = "2";

    /**
     * 热词阈值
     */
    public static final Integer HOT_SEARCH_THRESHOLD = 8;
}
