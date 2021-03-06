package ua.kiev.prog;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.*;

/**
 * @author Viktor Bezditnyi
 */

//HttpSessionCollector
public class Sessions implements HttpSessionListener {
    private static final Map<String, HttpSession> sessions = new HashMap<>();
    private static final List<String> usersOnline = new ArrayList<>();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessions.put(session.getId(), session);
        System.out.println("New session " + session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        String user = (String)session.getAttribute("user");
        if (user != null) {
            usersOnline.remove(user);
        }
        System.out.println("Session destroyed " + session.getId() + " " + user);
        sessions.remove(session.getId());
    }

    public static HttpSession find(String sessionId) {
        return sessions.get(sessionId);
    }

    public static String currentUsers() {
        StringBuilder res = new StringBuilder();
        sessions.forEach((id, s) -> res.append(s.getAttribute("user")).append(","));
        return res.toString();
    }

    public static boolean isLoggedIn(String user) {
        for(HttpSession s: sessions.values()) {
            if(user.equals(s.getAttribute("user"))) {
                return true;
            }
        }
        return false;
    }

    public static String getUserStatus(String user) {
        for(HttpSession s: sessions.values()) {
            if (user.equals(s.getAttribute("user"))) {
                return (String)s.getAttribute("userStatus");
            }
        }
        return null;
    }

    public static void addUser(String user){
        usersOnline.add(user);
    }

    public static boolean isUserOnline(String user) {
        return usersOnline.contains(user);
    }
}