package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddServlet extends HttpServlet {

    private MessageList msgList = MessageList.getInstance();

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {
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
        if (sessionUser == null || !sessionUser.equals(cookieUser)) { // invalid user, sessionId or something else
            System.out.println("Session User mismatch Cookie User");
            resp.setStatus(401);
            resp.setHeader("errorInfo", "Invalid sender");
            session.invalidate();
            return;
        }

        String attrRooms = (String)session.getAttribute("rooms");
        List<String> rooms = (attrRooms == null) ? new ArrayList<>(): Arrays.asList(attrRooms.split(","));

        InputStream is = req.getInputStream();
        byte[] buf = new byte[req.getContentLength()]; // is.available() doesn't work correctly
        is.read(buf);
        Message msg = Message.fromJSON(new String(buf));
        if (msg != null) {
            String to = msg.getTo();
            if (to == null || Sessions.isUserOnline(to) || rooms.contains(to)) {// validate the recipient of the message: 'to' is existing user or room from session_room_list
                msgList.add(msg);
            } else { // otherwise ERROR 400 bad request
                resp.setStatus(400);
                resp.setHeader("errorInfo", "Invalid recipient: user offline or room does not exist");
            }
        }
        else {
            resp.setStatus(400); // Bad request
            resp.setHeader("errorInfo", "Invalid message");
        }
    }
}
