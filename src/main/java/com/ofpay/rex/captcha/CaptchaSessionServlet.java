package com.ofpay.rex.captcha;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CaptchaSessionServlet extends BaseCaptchaServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        HttpSession session = request.getSession();
        if (null == session) {
            return;
        }

        DrawCaptcha dc = DrawCaptcha.getInstance(scale);
        ICaptchaControl cc = CaptchaControlHelp.getInstance(session);
        cc.setExpSeconds(expSeconds);

        response.setContentType("image/" + DrawCaptcha.IMAGE_FORMAT);
        response.setHeader("Pragram", "NO-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        cc.drawImage(dc, response.getOutputStream());

        CaptchaControlHelp.setSession(session, cc);
    }
}
