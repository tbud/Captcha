package com.ofpay.rex.captcha;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

public class RandomUtil {
    /**
     * 最小的字长（非字典随机文本生成） 最大字数 (非字典随机文本生成)
     * 
     * 用于字典的话，表示字长 修改字体大小的目的
     */
    private static int minWordLength = 4;
    private static int maxWordLength = 6;

    public static int rand(int min, int max) {
        return min + RandomUtils.nextInt(max - min + 1);
    }

    /**
     * 文本生成
     * 
     * @return
     */
    public static String getCaptchaText() {
        String text = getRandomNumberText(0); // getDictionaryCaptchaText(false);
        if (null == text) {
            text = getRandomCaptchaText(0);
        }

        return text;
    }

    public static String getRandomNumberText(int length) {
        if (length == 0) {
            length = RandomUtil.rand(minWordLength, maxWordLength);
        }

        String words = "0123456789";

        String text = "";

        for (int i = 0; i < length; i++) {
            text += RandomStringUtils.random(1, words);
        }

        return text;
    }

    /**
     * 随机文本生成
     * 
     * @param length
     * @return
     */
    public static String getRandomCaptchaText(int length) {
        if (length == 0) {
            length = RandomUtil.rand(minWordLength, maxWordLength);
        }

        String words = "abcdefghijlmnopqrstvwyz";
        String vocals = "aeiou";

        String text = "";
        boolean bVocal = RandomUtils.nextBoolean();

        for (int i = 0; i < length; i++) {
            if (bVocal) {
                text += RandomStringUtils.random(1, vocals);
            } else {
                text += RandomStringUtils.random(1, words);
            }

            bVocal = !bVocal;
        }

        return text;
    }
}
