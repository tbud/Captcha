package com.ofpay.rex.captcha;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CaptchaControlHelp {

    protected static final String CCH_KEY = "OFCaptchaControlHelp";

    protected static final int SSO_TIME_OUT = 5; // ssokey的超时时间，默认为10s。

    private static Logger logger = LoggerFactory.getLogger(CaptchaControlHelp.class);

    /**
     * 测试验证码是否正确
     * 
     * @param captchaText
     *            验证码
     * @param session
     *            session
     * @return
     */
    public static boolean checkCaptcha(String captchaText, HttpSession session) {
        if (session == null) {
            return false;
        }

        ICaptchaControl ic = getInstance(session);

        if (ic.isTimeout()) {
            return false;
        }

        boolean ret = ic.checkCaptcha(captchaText);
        if (ret) {
            session.removeAttribute(ICaptchaControl.KEY_STRING);
        }

        return ret;
    }

    /**
     * 测试验证码是否正确
     * 
     * @param captchaText
     *            验证码
     * @param memCache
     *            memCache
     * @param cookies
     * @return
     */
    public static boolean checkCaptcha(String captchaText, MemcachedClient memCache, Cookie[] cookies) {
        return checkCaptcha(captchaText, memCache, getCaptchaCookieValue(cookies));
    }

    /**
     * 测试验证码是否正确
     * 
     * @param captchaText
     *            验证码
     * @param memCache
     *            memCache
     * @param captchaCookieValue
     *            captcha在cookie中的value，可以通过getCaptchaCookieValue从cookie中获取。
     * @return
     */
    public static boolean checkCaptcha(String captchaText, MemcachedClient memCache, String captchaCookieValue) {
        if (memCache == null || StringUtils.isEmpty(captchaCookieValue)) {
            return false;
        }

        ICaptchaControl ic = getInstance(memCache, captchaCookieValue);

        if (ic.isTimeout()) {
            return false;
        }

        boolean ret = ic.checkCaptcha(captchaText);

        if (ret) {
            try {
                memCache.delete(captchaCookieValue);
            } catch (TimeoutException e) {
                logger.debug("TimeoutException:", e);
            } catch (InterruptedException e) {
                logger.debug("InterruptedException:", e);
            } catch (MemcachedException e) {
                logger.debug("MemcachedException:", e);
            }
        }

        return ret;
    }

    /**
     * 从session中获取ICaptchaControl接口实例
     * 
     * @param session
     * @return
     */
    public static ICaptchaControl getInstance(HttpSession session) {
        if (null == session) {
            return null;
        }

        Object obj = session.getAttribute(ICaptchaControl.KEY_STRING);
        ICaptchaControl cc = null;

        if (null == obj || !(obj instanceof ICaptchaControl)) {
            cc = new CaptchaControl();
            logger.debug("new captcha control in session method.");
        } else {
            cc = (ICaptchaControl) obj;
        }

        return cc;
    }

    /**
     * 通过memCache和cookies获取ICaptchaControl接口实例
     * 
     * @param memCache
     * @param cookies
     * @return
     */
    public static ICaptchaControl getInstance(MemcachedClient memCache, Cookie[] cookies) {
        return getInstance(memCache, getCaptchaCookieValue(cookies));
    }

    /**
     * 通过memCache和captchaCookieValue获取ICaptchaControl接口实例
     * 
     * @param memCache
     *            memCache
     * @param captchaCookieValue
     *            captcha在cookie中的value，可以通过getCaptchaCookieValue从cookie中获取。
     * @return
     */
    public static ICaptchaControl getInstance(MemcachedClient memCache, String captchaCookieValue) {
        if (null == memCache) {
            return null;
        }

        Object obj = null;

        if (!StringUtils.isEmpty(captchaCookieValue)) {
            try {
                obj = memCache.get(captchaCookieValue);
            } catch (TimeoutException e) {
                logger.debug("TimeoutException:", e);
            } catch (InterruptedException e) {
                logger.debug("InterruptedException:", e);
            } catch (MemcachedException e) {
                logger.debug("MemcachedException:", e);
            }
        }

        ICaptchaControl cc = null;
        if (null == obj || !(obj instanceof ICaptchaControl)) {
            cc = new CaptchaControl();
            logger.debug("new captcha control in memcache method.");
        } else {
            cc = (ICaptchaControl) obj;
        }

        return cc;
    }

    /**
     * 从cookie中获取captcha在cookie中的value
     * 
     * @param cookies
     * @return
     */
    public static String getCaptchaCookieValue(Cookie[] cookies) {
        Cookie cookie = getCookiesByKey(cookies, ICaptchaControl.KEY_STRING);

        String captchaKey = "";
        if (null != cookie) {
            captchaKey = cookie.getValue();
        }

        return captchaKey;
    }

    public static void setSession(HttpSession session, ICaptchaControl cc) {
        if (null == session) {
            return;
        }

        session.setAttribute(ICaptchaControl.KEY_STRING, cc);
    }

    public static String setCookie(HttpServletResponse response, Cookie[] cookies, int expSeconds) {
        Cookie cookie = getCookiesByKey(cookies, ICaptchaControl.KEY_STRING);
        String captchaKey = "";
        if (null != cookie) {
            captchaKey = cookie.getValue();
        }

        if (null == cookie || StringUtils.isEmpty(captchaKey)) {
            captchaKey = ICaptchaControl.KEY_STRING + UUID.randomUUID().toString();
            cookie = new Cookie(ICaptchaControl.KEY_STRING, captchaKey);
        }

        cookie.setPath("/");
        cookie.setMaxAge(365 * 24 * 3600);

        response.addCookie(cookie);

        return captchaKey;
    }

    public static void setMemCache(MemcachedClient memCache, ICaptchaControl cc, String captchaKey) {
        try {
            memCache.set(captchaKey, cc.getExpSeconds(), cc);
        } catch (TimeoutException e) {
            logger.debug("TimeoutException:", e);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException:", e);
        } catch (MemcachedException e) {
            logger.debug("MemcachedException:", e);
        }
    }

    /**
     * 获取单点登陆key，该key只能使用一次。 默认5秒超时。
     * 
     * @param saltKey
     *            盐值，保存在memcache中。
     * @return ssokey
     */
    public static String getSSOKey(MemcachedClient memCache, String saltKey) {

        return getSSOKey(memCache, saltKey, SSO_TIME_OUT);
    }

    /**
     * 获取单点登陆key，该key只能使用一次。
     * 
     * @param saltKey
     *            盐值，保存在memcache中。
     * @param expTime
     *            超时时间，单位秒
     * @return ssokey
     */
    public static String getSSOKey(MemcachedClient memCache, String saltKey, int expTime) {
        if (null == memCache || StringUtils.isEmpty(saltKey)) {
            return null;
        }

        if (expTime <= 0) {
            expTime = SSO_TIME_OUT;
        }

        String strRet = CCH_KEY + UUID.randomUUID();
        boolean bException = true;

        try {
            memCache.set(strRet, expTime, saltKey);
            bException = false;
        } catch (TimeoutException e) {
            logger.debug("TimeoutException:", e);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException:", e);
        } catch (MemcachedException e) {
            logger.debug("MemcachedException:", e);
        }

        return bException ? null : strRet;
    }

    /**
     * 检查ssokey
     * 
     * @param ssoKey
     *            ssokey值
     * @param saltKey
     *            盐值，需要检查盐值的正确性。
     * @return
     */
    public static boolean checkSSOKey(MemcachedClient memCache, String ssoKey, String saltKey) {
        if (null == memCache) {
            return false;
        }

        String value;
        boolean bResult = false;
        try {
            value = memCache.get(ssoKey);
            if (!StringUtils.isEmpty(value) && value.equals(saltKey)) {
                memCache.delete(ssoKey);
                bResult = true;
            }
        } catch (TimeoutException e) {
            logger.debug("TimeoutException:", e);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException:", e);
        } catch (MemcachedException e) {
            logger.debug("MemcachedException:", e);
        }

        return bResult;
    }

    protected static Cookie getCookiesByKey(Cookie[] cookies, String keyName) {
        if (null == cookies) {
            return null;
        }

        for (int i = 0; i < cookies.length; i++) {
            Cookie c = cookies[i];

            if (c.getName().equalsIgnoreCase(keyName)) {
                return c;
            }
        }

        return null;
    }
}
