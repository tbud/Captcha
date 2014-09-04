package com.ofpay.rex.captcha;

import junit.framework.TestCase;

public class CaptchaControlHelpTest extends TestCase {

    public void testSSOCheckOK() {
        // String key = CaptchaControlHelp.getSSOKey("pengyi");
        //
        // Assert.assertTrue(CaptchaControlHelp.checkSSOKey(key, "pengyi"));
    }

    public void testSSOCheckAgainFail() {
        // String key = CaptchaControlHelp.getSSOKey("pengyi");
        //
        // Assert.assertTrue(CaptchaControlHelp.checkSSOKey(key, "pengyi"));
        //
        // Assert.assertFalse(CaptchaControlHelp.checkSSOKey(key, "pengyi"));
    }

    public void testSSOCheckErrorSalt() {
        // String key = CaptchaControlHelp.getSSOKey("pengyi");
        //
        // Assert.assertFalse(CaptchaControlHelp.checkSSOKey(key, "123"));
        // Assert.assertTrue(CaptchaControlHelp.checkSSOKey(key, "pengyi"));
    }

    public void testSSOKeyTimtOut() {
        // String key = CaptchaControlHelp.getSSOKey("pengyi");
        //
        // try {
        // Thread.sleep((CaptchaControlHelp.SSO_TIME_OUT + 1) * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        //
        // Assert.assertFalse(CaptchaControlHelp.checkSSOKey(key, "pengyi"));
    }

    public void testSSOKeyTimtOut2() {
        // int timeout = 10;
        // String key = CaptchaControlHelp.getSSOKey("pengyi", timeout);
        //
        // try {
        // Thread.sleep((CaptchaControlHelp.SSO_TIME_OUT + 1) * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        //
        // Assert.assertTrue(CaptchaControlHelp.checkSSOKey(key, "pengyi"));
        //
        // key = CaptchaControlHelp.getSSOKey("pengyi", timeout);
        //
        // try {
        // Thread.sleep((timeout + 1) * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        //
        // Assert.assertFalse(CaptchaControlHelp.checkSSOKey(key, "pengyi"));
    }
}
