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
import com.bc.wechat.entity.Conversation;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment {

    private List<Conversation> normalList = new ArrayList<>();
    private List<Conversation> topList = new ArrayList<>();
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
        Conversation conversation = new Conversation();
        Conversation conversation1 = new Conversation();
        Conversation conversation2 = new Conversation();
        Conversation conversation3 = new Conversation();

        normalList.add(conversation);
        normalList.add(conversation1);
        normalList.add(conversation2);
        normalList.add(conversation3);
        topList.add(conversation);
        topList.add(conversation1);
        conversationAdapter = new ConversationAdapter(getActivity(), normalList, topList);
        listView.setAdapter(conversationAdapter);
    }
}
