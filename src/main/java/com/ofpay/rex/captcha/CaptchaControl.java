package com.ofpay.rex.captcha;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptchaControl implements ICaptchaControl, Serializable {
    private static Logger logger = LoggerFactory.getLogger(CaptchaControl.class);

    private static final long serialVersionUID = -8256676832538234674L;

    protected int errorNumber = 0;

    protected int refreshTextNumber = 0;

    protected String currentText = null;

    private long createTime = 0;

    private int expSeconds = 60; // 默认超时时间60秒

    protected String getText() {
        if (refreshTextNumber > 50) {
            currentText = RandomUtil.getRandomCaptchaText(0);
        } else {
            currentText = RandomUtil.getRandomNumberText(0);
        }

        refreshTextNumber++;

        createTime = new Date().getTime();

        return currentText;
    }

    /**
     * 根据错误次数和尝试次数修改识别码产生的行为。
     * 
     * @param ccfg
     *            传入配置bean
     * @return
     */
    protected CaptchaConfig getCaptchaConfig(CaptchaConfig ccfg) {
        // 在3种填充方式中进行随机
        ccfg.setFillType(FillType.values()[RandomUtils.nextInt(FillType.values().length)]);

        // 当错误3次后，设置产生的图片有波浪效果。
        ccfg.setbWaveImage(errorNumber > 2);

        // 根据尝试次数，设置字符的连接百分比随机数。最少设置10.
        ccfg.setConnectPercent(Math.max(10, refreshTextNumber / 3));

        // 根据尝试次数，设置旋转角度。最小设置15.
        ccfg.setMaxRotation(Math.max(15, refreshTextNumber / 2));

        return ccfg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ofpay.rex.captcha.ICaptchaControl#checkCaptcha(java.lang.String)
     */
    @Override
    public boolean checkCaptcha(String captchaText) {
        logger.debug("CurrentText={}, inputCaptchaText={}", currentText, captchaText);
        // 刚刚初始化，还没有生产任何图片时，currentText会为null。
        if (StringUtils.isEmpty(currentText)) {
            return false;
        }

        boolean bRet = currentText.equalsIgnoreCase(captchaText);

        if (bRet) {
            // 登陆成功，清空状态到初始值
            errorNumber = 0;
            refreshTextNumber = 0;
        } else {
            // 验证错误则错误数增一
            errorNumber++;
        }

        return bRet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ofpay.rex.captcha.ICaptchaControl#drawImage(java.io.OutputStream)
     */
    @Override
    public void drawImage(DrawCaptcha dc, OutputStream os) {
        if (null == dc || null == os) {
            return;
        }

        dc.createImage(getText(), os, getCaptchaConfig(dc.getCaptchaConfig()));
    }

    @Override
    public boolean isTimeout() {
        Date currentDate = new Date();
        int interval = (int) ((currentDate.getTime() - createTime) / 1000);

        return interval > expSeconds;
    }

    @Override
    public void setExpSeconds(int expSeconds) {
        if (expSeconds > 0) {
            this.expSeconds = expSeconds;
        }
    }

    @Override
    public int getExpSeconds() {
        return expSeconds;
    }
}
