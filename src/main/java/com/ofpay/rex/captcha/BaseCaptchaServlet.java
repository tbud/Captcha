package com.ofpay.rex.captcha;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class BaseCaptchaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected float scale = 1.0f;

    protected int expSeconds = 60;

    public void init() throws ServletException {
        String strScale = getInitParameter("scale");

        if (!StringUtils.isEmpty(strScale)) {
            float tmpScale = Float.valueOf(strScale);
            if (tmpScale > 0) {
                scale = tmpScale;
            }
        }

        String strExpSeconds = getInitParameter("expTime");

        if (!StringUtils.isEmpty(strExpSeconds)) {
            int tmpExpSeconds = Integer.valueOf(strExpSeconds);
            if (tmpExpSeconds > 0) {
                expSeconds = tmpExpSeconds;
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
