package com.ofpay.rex.captcha;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ofcard.component.cache.MemcachedUtil;

public class CaptchaMemCacheServlet extends BaseCaptchaServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        MemCacheServletHelp.drawImage(MemcachedUtil.getMemcachedClient(), request, response, scale, expSeconds);
    }
}
