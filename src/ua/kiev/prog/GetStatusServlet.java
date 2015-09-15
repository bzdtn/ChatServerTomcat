package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.Date;

/**
 * @author Viktor Bezditnyi
 */
public class GetStatusServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

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

        String user = req.getParameter("user");
        if (!Sessions.isUserOnline(user)) {// possible problems in multithreading
            resp.setStatus(400);
            resp.setHeader("errorInfo", "Invalid request: user not found");
            return;
        }

        resp.setStatus(200);
        String status = Sessions.getUserStatus(user);
        Message msg = new Message();
        msg.setFrom("Server");
        msg.setTo(sessionUser);
        if (status != null){
            msg.setText("User: " + user + ", status: " + status);
        } else {
            msg.setText("User: " + user + ", no status set");
        }
        msg.setDate(new Date());
        MessageList.getInstance().add(msg);
    }
}
