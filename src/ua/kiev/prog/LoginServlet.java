package ua.kiev.prog;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

    static final Map<String, String> credentials = new HashMap<>();

    static { // hardcode login credentials
        credentials.put("user", "qwerty");
        credentials.put("admin", "qazwsx");
        credentials.put("ivan", "1234");
        credentials.put("john", "doe");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String login = req.getHeader("login");
        String password = req.getHeader("password");
        if(Sessions.isLoggedIn(login)){
            resp.setStatus(409); // CONFLICT the user already logged in
            return;
        }
        String temp = credentials.get(login);
        if (temp != null && temp.equals(password)) {
            resp.setStatus(200);
            Cookie loginCookie = new Cookie("user", login);
            loginCookie.setMaxAge(30 * 60);
            resp.addCookie(loginCookie);
            HttpSession session = req.getSession();

            session.setAttribute("user", login);
            session.setMaxInactiveInterval(30 * 60);
        } else {
            resp.setStatus(401); //401 Unauthorized wrong password
        }
    }
}

