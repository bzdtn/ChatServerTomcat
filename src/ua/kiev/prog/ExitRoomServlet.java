package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Viktor Bezditnyi
 */
public class ExitRoomServlet extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String sessionUser = (String)session.getAttribute("user");
        String cookieUser = null;
        Cookie[] cookies = req.getCookies();
        if(cookies != null){
            for(Cookie cookie: cookies){
                if("user".equals(cookie.getName())){
                    cookieUser = cookie.getValue();
                    break;
                }
            }
        }
        if (sessionUser == null || !sessionUser.equals(cookieUser)) { // invalid user, sessionId or smthng else
            System.out.println("Session User mismatch Cookie User");
            resp.setStatus(401);
            resp.setHeader("errorInfo", "Invalid sender");
            session.invalidate();
            return;
        }

        String room = req.getHeader("room");
        if (room == null) {
            resp.setStatus(400);
            resp.setHeader("errorInfo", "Invalid request: no room");
            return;
        }

        String attrRooms = (String)session.getAttribute("rooms");
        List<String> rooms = (attrRooms == null) ? new ArrayList<>() : Arrays.asList(attrRooms.split(","));
        if (!rooms.contains(room)) {
            resp.setStatus(400);
            resp.setHeader("errorInfo", "Invalid request: sender does not join the room " + room);
            return;
        } else {
            rooms.remove(room);
            session.removeAttribute("rooms");
            if (!rooms.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                rooms.forEach(s -> sb.append(s).append(","));
                session.setAttribute("rooms", sb.toString());
            }
            resp.setStatus(200);
        }
    }
}
