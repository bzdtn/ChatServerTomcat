package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;

/**
 * @author Viktor Bezditnyi
 */
public class LogoutServlet extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            resp.setHeader("errorInfo", "Invalid sender");
            session.invalidate();
            return;
        }

        resp.setStatus(200);
        session.invalidate();
    }
}
