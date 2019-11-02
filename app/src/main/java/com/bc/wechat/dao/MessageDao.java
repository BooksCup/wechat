package com.bc.wechat.dao;

import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.Message;

import java.util.List;

public class MessageDao {
    public Message getMessageByMessageId(String messageId) {
        List<Message> messageList = Message.find(Message.class, "message_id = ?", messageId);
        if (null != messageList && messageList.size() > 0) {
            return messageList.get(0);
        }
        return null;
    }

    /**
     * 根据群组ID删除消息
     * 用处: 群会话中清空聊天记录
     *
     * @param groupId 群组ID
     */
    public void deleteMessageByGroupId(String groupId) {
        String sql = "delete from message where group_id = ?";
        Message.executeQuery(sql, groupId);
    }

    public void deleteMessageByUserId(String userId) {
        String sql = "delete from message where (from_user_id = ? or to_user_id = ?) and target_type = ?";
        Message.executeQuery(sql, userId, userId, Constant.TARGET_TYPE_SINGLE);
    }

    public long getMessageCountByGroupId(String groupId) {
        long count = Message.count(Message.class, "group_id = ?", new String[]{groupId});
        return count;
    }
}
