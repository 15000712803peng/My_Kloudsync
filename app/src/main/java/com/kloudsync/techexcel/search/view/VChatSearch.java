package com.kloudsync.techexcel.search.view;

import java.util.List;

import io.rong.imlib.model.Conversation;

public interface VChatSearch {
    void showLoading();

    void showEmpty(String message);

    void showChats(List<Conversation> conversations, String keyword);
}
