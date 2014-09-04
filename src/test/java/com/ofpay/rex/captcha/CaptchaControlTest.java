package com.ofpay.rex.captcha;

import junit.framework.TestCase;

public class CaptchaControlTest extends TestCase {

    public void testIsOvertimeFalse() {
        CaptchaControl cc = new CaptchaControl();
        cc.getText();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cc.setExpSeconds(4);
        assertFalse(cc.isTimeout());
    }

    public void testIsOvertimeTrue() {
        CaptchaControl cc = new CaptchaControl();
        cc.getText();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cc.setExpSeconds(2);
        assertTrue(cc.isTimeout());
    }

}
