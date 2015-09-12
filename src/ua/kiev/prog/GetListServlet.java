package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;

public class GetListServlet extends HttpServlet {

    private MessageList msgList = MessageList.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        HttpSession session = req.getSession();
        String userSession = (String)session.getAttribute("user");

        String userCookie = null;
        Cookie[] cookies = req.getCookies();
        if(cookies != null){
            for(Cookie cookie: cookies){
                if("user".equals(cookie.getName())){
                    userCookie = cookie.getValue();
                    break;
                }
            }
        }
        if (userSession == null || !userSession.equals(userCookie)) { // invalid user, sessionId or smthng else
            System.out.println("Session User mismatch Cookie User");
            resp.setStatus(401);
            session.invalidate();
            return;
        }

        resp.setStatus(200);
        String fromStr = req.getParameter("from");
        int from = Integer.parseInt(fromStr);

        int[] msgNum = {0}; // number of messages in the list
        String json = msgList.toJSON(from, userSession, msgNum); // select messages 'to' or/and 'from' this user
        resp.addIntHeader("message_number", msgNum[0]);
        if (json != null) {
            OutputStream os = resp.getOutputStream();
            os.write(json.getBytes());
        }
    }
}
