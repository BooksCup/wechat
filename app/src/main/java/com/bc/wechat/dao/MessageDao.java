package com.bc.wechat.dao;

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

    public void deleteMessageByGroupId(String groupId) {
        String sql = "delete from message where group_id = ?";
        Message.executeQuery(sql, groupId);
    }

    public long getMessageCountByGroupId(String groupId) {
        long count = Message.count(Message.class, "group_id = ?", new String[]{groupId});
        return count;
    }
}
