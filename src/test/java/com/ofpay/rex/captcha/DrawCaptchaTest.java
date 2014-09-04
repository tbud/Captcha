package com.ofpay.rex.captcha;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DrawCaptchaTest extends TestCase {

    // 确保字体输出的最大高度小于等于30.
    public void testFontSize() {
        BufferedImage bim = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bim.createGraphics();

        FontRenderContext frc = g2d.getFontRenderContext();

        for (FontConfig fc : DrawCaptcha.FONTS) {
            System.out.println(fc.getName());

            for (int size = fc.getMinSize(); size < fc.getMaxSize() + 1; size++) {
                TextLayout tl = new TextLayout("0123456789abcdefghijklmnopqrstuvwxyz", fc.getFont().deriveFont(
                        Font.PLAIN, size), frc);

                Assert.assertTrue(tl.getBounds().getHeight() <= 30);
            }
        }
    }
}
