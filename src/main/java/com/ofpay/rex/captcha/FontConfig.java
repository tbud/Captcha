package com.ofpay.rex.captcha;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontConfig {

    /**
     * 字体资源文件路径
     * 
     */
    protected static final String FONT_RESOURCE_PATH = "/fonts/";

    private Logger logger = LoggerFactory.getLogger(FontConfig.class);

    private String name;
    private int minSize;
    private int maxSize;
    private String fontFile;
    private Font font;
    private Font[] fonts = null;

    public FontConfig(String name, int minSize, int maxSize, String fontFile) {
        this.name = name;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.fontFile = fontFile;

        // 加载字体
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(FONT_RESOURCE_PATH + fontFile));
        } catch (FontFormatException e) {
            logger.error("FontFormatException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        }

        fonts = new Font[maxSize - minSize + 1];
        for (int i = 0; i < fonts.length; i++) {
            fonts[i] = font.deriveFont(Font.PLAIN, minSize + i);
        }
    }

    public Font getRandomSize() {
        return fonts[RandomUtils.nextInt(fonts.length)];
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String getFontFile() {
        return fontFile;
    }

    public String getName() {
        return name;
    }

    public Font getFont() {
        return font;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CfgFont [name=");
        builder.append(name);
        builder.append(", minSize=");
        builder.append(minSize);
        builder.append(", maxSize=");
        builder.append(maxSize);
        builder.append(", fontFile=");
        builder.append(fontFile);
        builder.append("]");
        return builder.toString();
    }
}
