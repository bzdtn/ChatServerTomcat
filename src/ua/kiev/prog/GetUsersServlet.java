package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.Date;

/**
 * @author Viktor Bezditnyi
 */
public class GetUsersServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
        String res = Sessions.currentUsers();
        if (res.length() > 0) {
            resp.addHeader("usersList", res);
        }

        Message msg = new Message();
        msg.setFrom("Server");
        msg.setTo(userSession);
        msg.setText(Sessions.currentUsers());
        msg.setDate(new Date());
        MessageList.getInstance().add(msg);
    }
}
