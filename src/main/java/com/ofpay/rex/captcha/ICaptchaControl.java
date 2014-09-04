package com.ofpay.rex.captcha;

import java.io.OutputStream;

public interface ICaptchaControl {

    /**
     * session中保存的attribute的名字
     */
    public static final String KEY_STRING = "OFCaptchaControl";

    /**
     * @param captchaText
     *            输入需要检查的字符串
     * @return 返回字符串与图片是否匹配
     */
    public abstract boolean checkCaptcha(String captchaText);

    /**
     * @return
     */
    public abstract boolean isTimeout();

    /**
     * @param expSeconds
     */
    public abstract void setExpSeconds(int expSeconds);

    /**
     * @return
     */
    public abstract int getExpSeconds();

    /**
     * @param os
     *            输出图片到流
     */
    public abstract void drawImage(DrawCaptcha dc, OutputStream os);

}