package com.ofpay.rex.captcha;

import java.io.Serializable;

public class CaptchaConfig implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4751649544138950507L;

    /** 字母顺时针旋转 */
    private int maxRotation = 30;

    /**
     * 抗锯齿
     */
    private boolean bAntialias = true;

    /**
     * 是否画干扰线
     */
    private boolean bDrawLine = true;

    /**
     * 是否wave image
     */
    private boolean bWaveImage = true;

    /**
     * 填充类型
     */
    private FillType fillType = FillType.SOLID;

    /**
     * 链接百分比
     */
    private int connectPercent = 10;

    public int getMaxRotation() {
        return maxRotation;
    }

    public void setMaxRotation(int maxRotation) {
        if (maxRotation > 0 && maxRotation < 90) {
            this.maxRotation = maxRotation;
        }
    }

    public boolean isbAntialias() {
        return bAntialias;
    }

    public void setbAntialias(boolean bAntialias) {
        this.bAntialias = bAntialias;
    }

    public boolean isbDrawLine() {
        return bDrawLine;
    }

    public void setbDrawLine(boolean bDrawLine) {
        this.bDrawLine = bDrawLine;
    }

    public boolean isbWaveImage() {
        return bWaveImage;
    }

    public void setbWaveImage(boolean bWaveImage) {
        this.bWaveImage = bWaveImage;
    }

    public int getConnectPercent() {
        return connectPercent;
    }

    public void setConnectPercent(int connectPercent) {
        if (connectPercent > 0 && connectPercent < 60) {
            this.connectPercent = connectPercent;
        }
    }

    public FillType getFillType() {
        return fillType;
    }

    public void setFillType(FillType fillType) {
        this.fillType = fillType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CaptchaConfig [maxRotation=");
        builder.append(maxRotation);
        builder.append(", bAntialias=");
        builder.append(bAntialias);
        builder.append(", bDrawLine=");
        builder.append(bDrawLine);
        builder.append(", bWaveImage=");
        builder.append(bWaveImage);
        builder.append(", fillType=");
        builder.append(fillType);
        builder.append(", connectPercent=");
        builder.append(connectPercent);
        builder.append("]");
        return builder.toString();
    }
}
