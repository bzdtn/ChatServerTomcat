package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GetListServlet extends HttpServlet {

    private MessageList msgList = MessageList.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
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

        resp.setStatus(200);
        String fromStr = req.getParameter("from");
        int from = Integer.parseInt(fromStr);
        int[] getMsgNum = {0}; // to get number of messages in the list
        String attrRooms = (String)session.getAttribute("rooms");
        List<String> rooms = (attrRooms == null) ? new ArrayList<>(): (Arrays.asList(attrRooms.split(",")));
        String json = msgList.toJSON(from, sessionUser, rooms, getMsgNum); // select messages 'to' or/and 'from' this user
        resp.addIntHeader("messageNumber", getMsgNum[0]);
        if (json != null) {
            OutputStream os = resp.getOutputStream();
            os.write(json.getBytes());
        }
    }
}
