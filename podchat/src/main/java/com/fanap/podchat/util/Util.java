package com.fanap.podchat.util;

import android.support.annotation.NonNull;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.model.ResultAddContact;

import java.util.ArrayList;
import java.util.List;

public class Util {

    @NonNull
    public static ChatResponse<ResultAddContact> getReformatOutPutAddContact(Contacts contacts, String uniqueId) {
        ChatResponse<ResultAddContact> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(uniqueId);

        ResultAddContact resultAddContact = new ResultAddContact();
        resultAddContact.setContentCount(1);
        Contact contact = new Contact();

        contact.setCellphoneNumber(contacts.getResult().get(0).getCellphoneNumber());
        contact.setEmail(contacts.getResult().get(0).getEmail());
        contact.setFirstName(contacts.getResult().get(0).getFirstName());
        contact.setId(contacts.getResult().get(0).getId());
        contact.setLastName(contacts.getResult().get(0).getLastName());
        contact.setUniqueId(contacts.getResult().get(0).getUniqueId());
        resultAddContact.setContact(contact);
        chatResponse.setResult(resultAddContact);
        return chatResponse;
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static <T extends Number> boolean isNullOrEmpty(ArrayList<T> list) {
        return list == null || list.size() == 0;
    }

    public static <T extends Number> boolean isNullOrEmpty(T number) {
        String num = String.valueOf(number);
        return number == null || num.equals("0");
    }

    public static <T extends Object> boolean isNullOrEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNullOrEmptyMessageVO(List<MessageVO> list) {
        return list == null || list.size() == 0;
    }

    public static <T extends Number> boolean isNullOrEmptyNumber(List<T> list) {
        return list == null || list.size() == 0;
    }

}
