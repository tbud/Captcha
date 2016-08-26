package com.bud.captcha;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestFloatEqual extends TestCase {

    public void testFloatEqual() {
        float a = 3.21f;
        float b = 1.11f;
        float c = 2.1f;

        Assert.assertTrue((a - b) == c);
    }

}
