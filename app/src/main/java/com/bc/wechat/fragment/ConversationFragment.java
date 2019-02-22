package com.bc.wechat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bc.wechat.R;
import com.bc.wechat.adapter.ConversationAdapter;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;

public class ConversationFragment extends Fragment {

    private ListView listView;
    private ConversationAdapter conversationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = getView().findViewById(R.id.list);
        List<Conversation> conversationList = JMessageClient.getConversationList();
        conversationAdapter = new ConversationAdapter(getActivity(), conversationList);
        listView.setAdapter(conversationAdapter);
    }
}
