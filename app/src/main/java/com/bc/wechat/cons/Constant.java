package com.bc.wechat.cons;

/**
 * 常量类
 */
public class Constant {
    public static String PICTURE_DIR = "sdcard/wechat_/pictures/";

    public static final String BASE_URL = "http://49.4.25.11:8081/";

    public static final String FILE_UPLOAD_URL = BASE_URL + "files";
    public static final String FILE_BASE_URL = "http://49.4.25.11:9999/wechat_file/";

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
}
