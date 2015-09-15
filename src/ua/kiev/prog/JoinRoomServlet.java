package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * @author Viktor Bezditnyi
 */
public class JoinRoomServlet extends HttpServlet {

    private static final Map<String, String> rooms = new HashMap<>();

    static { // hardcode rooms rooms
        rooms.put("room0", "0");
        rooms.put("room1", "1");
        rooms.put("besedka", "1234");
        rooms.put("izba", "1234");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String sessionUser = (String)session.getAttribute("user");
        String cookieUser = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user".equals(cookie.getName())) {
                    cookieUser = cookie.getValue();
                    break;
                }
            }
        }
        if (sessionUser == null || !sessionUser.equals(cookieUser)) { // invalid user, sessionId or something else
            System.out.println("Session User mismatch Cookie User");
            resp.setStatus(401);
            resp.setHeader("errorInfo", "Invalid sender");
            session.invalidate();
            return;
        }

        String room = req.getHeader("room");
        String password = req.getHeader("password");
        if (room == null || password == null || LoginServlet.isRegistered(room)) {
            resp.setStatus(400);// bad request: no room name or no password or room=some user name
            resp.setHeader("errorInfo", "Invalid request: room or password not set");
            return;
        }


        String attrRooms = (String)session.getAttribute("rooms");
        List<String> sessionRooms = (attrRooms == null) ? new ArrayList<>() : Arrays.asList(attrRooms.split(","));
        if (sessionRooms.contains(room)) {
            resp.setStatus(400);
            resp.setHeader("errorInfo", "Invalid request: sender already joined the room");
            return;
        } else {
            if (!rooms.containsKey(room)) { // no room yet
                rooms.put(room, password);
            } else { // room exists, verify password
                if (!rooms.get(room).equals(password)) {
                    resp.setStatus(400); // wrong password
                    resp.setHeader("errorInfo", "Invalid password");
                    return;
                }
            }
            session.removeAttribute("rooms");
            session.setAttribute("rooms", sessionRooms + room + ",");
            resp.setStatus(200);
        }
    }

    public static boolean isRoomRegistered(String room) {
        return rooms.containsKey(room);
    }
}
