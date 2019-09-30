package com.kloudsync.techexcel.search.view;

import com.kloudsync.techexcel.info.Customer;

import java.util.List;

public interface VContactSearch {
    void showLoading();

    void showEmpty(String message);

    void showContacts(List<Customer> conversations, String keyword);
}
