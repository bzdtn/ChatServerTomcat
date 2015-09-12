package ua.kiev.prog;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class AddServlet extends HttpServlet {

    private MessageList msgList = MessageList.getInstance();

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {

        InputStream is = req.getInputStream();
        byte[] buf = new byte[req.getContentLength()]; // is.available() doesn't work correctly
        is.read(buf);
        Message msg = Message.fromJSON(new String(buf));
        if (msg != null)
            msgList.add(msg);
        else
            resp.setStatus(400); // Bad request
    }
}
