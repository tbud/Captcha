package com.ofpay.rex.captcha;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rubyeye.xmemcached.MemcachedClient;

public class MemCacheServletHelp {

    public static void drawImage(MemcachedClient memCache, HttpServletRequest request, HttpServletResponse response,
            float scale, int expSeconds) throws IOException {

        DrawCaptcha dc = DrawCaptcha.getInstance(scale);
        ICaptchaControl cc = CaptchaControlHelp.getInstance(memCache, request.getCookies());
        cc.setExpSeconds(expSeconds);

        response.setContentType("image/" + DrawCaptcha.IMAGE_FORMAT);
        response.setHeader("Pragram", "NO-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        String captchaKey = CaptchaControlHelp.setCookie(response, request.getCookies(), expSeconds);

        cc.drawImage(dc, response.getOutputStream());

        CaptchaControlHelp.setMemCache(memCache, cc, captchaKey);
    }
}
