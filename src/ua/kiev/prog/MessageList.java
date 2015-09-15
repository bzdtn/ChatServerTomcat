package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class MessageList {

    private static final MessageList msgList = new MessageList();

    private final List<Message> list = new ArrayList<>();

    public static MessageList getInstance() {
        return msgList;
    }

    public synchronized void add(Message m) {
        list.add(m);
    }

    public synchronized String toJSON(int n) {
        List<Message> res = new ArrayList<>();
        for (int i = n; i < list.size(); i++)
            res.add(list.get(i));

        if (res.size() > 0) {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(res.toArray());
        } else
            return null;
    }

    public synchronized String toJSON(int n, String user, List<String> rooms, int[] msgNum) {
        List<Message> res = new ArrayList<>();
        Message msg = null;
        msgNum[0] = list.size();
        for (int i = n; i < list.size(); i++) {
            msg = list.get(i);
            // for ALL (to=null), for User, from User
            String to = msg.getTo();
            if (to == null || msg.getFrom().equals(user) || to.equals(user) || rooms.contains(to)) {
                res.add(msg);
            }
        }
        if (res.size() > 0) {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(res.toArray());
        } else
            return null;
    }

    public synchronized int size() {
        return list.size();
    }
}
