package com.ofpay.rex.captcha;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author py
 * 
 *         实现文字转图片的类。 默认图片大小为120*40. 可以通过初始化类时的参数传递需要缩放的大小。默认缩放1.0f。
 * 
 */
public class DrawCaptcha {

    private Logger logger = LoggerFactory.getLogger(DrawCaptcha.class);

    private static final ThreadLocal<DrawCaptcha> tdc = new ThreadLocal<DrawCaptcha>();

    /**
     * 图片格式
     */
    public static final String IMAGE_FORMAT = "png";

    /**
     * 字体配置
     * 
     * spacing 间距: 相对像素之间的空间特征 minSize 字号: 最小 maxSize 字号: 最大 font 字体: ttf文件
     */
    protected static final List<FontConfig> FONTS = new ArrayList<FontConfig>() {
        private static final long serialVersionUID = 2413410573604328031L;
        {
            // add(new FontConfig("Antykwa", 26, 30, "AntykwaBold.ttf"));
            add(new FontConfig("Candice", 30, 34, "Candice.ttf"));
            // add(new FontConfig("DingDong", 24, 30, "Ding-DongDaddyO.ttf"));
            add(new FontConfig("Duality", 28, 33, "Duality.ttf"));
            // add(new FontConfig("Heineken", 24, 31, "Heineken.ttf"));
            add(new FontConfig("Jura", 28, 32, "Jura.ttf"));
            // add(new FontConfig("StayPuft", 24, 28, "StayPuft.ttf"));
            add(new FontConfig("Times", 28, 34, "TimesNewRomanBold.ttf"));
            add(new FontConfig("VeraSans", 24, 30, "VeraSansBold.ttf"));
        }
    };

    /** 背景颜色 RGB数组 */
    protected Color backgroundColor = new Color(255, 255, 255);

    protected Color foregroundColor = null;

    /** 在RGB阵列的前景颜色 */
    protected Color[] foregroundColors = { new Color(27, 78, 181), // blue
            new Color(22, 163, 35), // green
            new Color(214, 36, 7) // red
    };

    /**
     * 图片宽度 图片高度
     **/
    protected int picWidth = 120;
    protected int picHeight = 40;

    /** 通过文字的水平线 */
    protected int lineWidth = 1;

    /** 在x、y轴的波形设置 */
    protected int nYperiod = 12;
    protected int nYamplitude = 14;
    protected int nXperiod = 11;
    protected int nXamplitude = 5;

    protected CaptchaConfig captchaConfig = null;

    protected float scale = 1.0f;

    protected BufferedImage bim = null;

    protected Graphics2D g2d = null;

    protected FontRenderContext frc = null;

    protected AffineTransform rotateATF = new AffineTransform();

    protected AffineTransform translateATF = new AffineTransform();

    private DrawCaptcha(CaptchaConfig ccfg, float fScale) {
        logger.debug("Use CaptchaConfig={}, fScale={} create DrawCaptcha.", ccfg, fScale);
        initDrawCaptcha(ccfg, fScale);
    }

    /**
     * @return 获取默认大小的图片对象
     */
    public static DrawCaptcha getInstance() {
        return getDrawCaptcha(null, 1.0f);
    }

    /**
     * @param scale
     *            x，y等比缩放。
     */
    public static DrawCaptcha getInstance(float scale) {
        return getDrawCaptcha(null, scale);
    }

    /**
     * @param ccfg
     *            see CaptchaConfig
     */
    public static DrawCaptcha getInstance(CaptchaConfig ccfg) {
        return getDrawCaptcha(ccfg, 1.0f);
    }

    /**
     * @param ccfg
     *            see CaptchaConfig
     * @param scale
     *            x，y等比缩放。
     */
    public static DrawCaptcha getInstance(CaptchaConfig ccfg, float scale) {
        return getDrawCaptcha(ccfg, scale);
    }

    private static DrawCaptcha getDrawCaptcha(CaptchaConfig ccfg, float fScale) {
        DrawCaptcha dc = tdc.get();
        if (dc == null) {
            dc = new DrawCaptcha(ccfg, fScale);
            tdc.set(dc);
        }

        if (fScale != dc.scale) {
            tdc.remove();
            dc = new DrawCaptcha(ccfg, fScale);
            tdc.set(dc);
        }

        return dc;
    }

    private void initDrawCaptcha(CaptchaConfig ccfg, float fScale) {
        if (null == ccfg) {
            captchaConfig = new CaptchaConfig();
        } else {
            captchaConfig = ccfg;
        }

        if (fScale > 0) {
            scale = fScale;
        }

        if (null == bim && null == g2d) {
            bim = new BufferedImage((int) (picWidth * scale), (int) (picHeight * scale), BufferedImage.TYPE_INT_RGB);

            g2d = bim.createGraphics();

            frc = g2d.getFontRenderContext();
        }
    }

    /**
     * 根据参数创建渲染图片。
     * 
     * @param text
     *            需要渲染的文字
     * @param outputStream
     *            渲染后输出图片到流，如果无需输出，可以传入null。
     * @param cconfig
     *            对DrawCaptcha对象产生一次影响的配置参数，为null则使用对象初始化时的参数继续创建。
     */
    public void createImage(String text, OutputStream outputStream, CaptchaConfig cconfig) {
        if (StringUtils.isEmpty(text)) {
            logger.info("Input param 'text' is empty.");
            return;
        }

        CaptchaConfig ccfg = null;

        if (null != cconfig) {
            ccfg = cconfig;
        } else {
            ccfg = captchaConfig;
        }

        logger.debug("Use {} create Image", ccfg);

        if (null == g2d) {
            logger.error("createGraphics from BufferedImage = null");
            return;
        }

        init(g2d, ccfg);

        try {
            if (ccfg.isbAntialias()) {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }

            Rectangle2D r2d = writeText(g2d, ccfg, text, null);

            if (null == r2d) {
                logger.error("writeText get a null Rectangle2D");
                return;
            }

            if (ccfg.isbDrawLine()) {
                writeLine(g2d, ccfg, r2d);
            }

            // if (cfgc.isbWaveImage()) {
            // waveImage(g2d, cfgc);
            // }

            if (ccfg.isbAntialias()) {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
            }

            if (null != outputStream) {
                // bim = blurImage(bim);

                // sun的jdk中未进行flush处理。此处添加先强制刷新BufferdImage对象。
                bim.flush();

                ImageIO.write(bim, IMAGE_FORMAT, outputStream);
            }
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    logger.error("DC, output stream flush error:", e);
                }

                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("DC, output stream close error:", e);
                }
            }
        }
    }

    /**
     * 初始化图片资源
     */
    protected void init(Graphics2D g2d, CaptchaConfig ccfg) {
        // 背景颜色
        g2d.setBackground(backgroundColor);

        // 前景颜色
        foregroundColor = foregroundColors[RandomUtils.nextInt(foregroundColors.length)];
        g2d.setColor(foregroundColor);

        // 设置画线的尺寸
        g2d.setStroke(new BasicStroke(lineWidth));

        // 设置相对偏移坐标归零
        translateATF.setToTranslation(0, 0);
        g2d.setTransform(translateATF);

        // 设置缩放比例
        g2d.scale(scale, scale);

        // 清空剪切区域
        g2d.setClip(null);

        // 用背景色清空图片
        g2d.clearRect(0, 0, picWidth, picHeight);
    }

    /**
     * 绘制文字到图片
     * 
     * @param g2d
     *            将文字渲染到的图片
     * @param ccfg
     *            渲染所用的配置参数
     * @param text
     *            渲染所用的文字
     * @param fcfg
     *            渲染所用的字体
     * @return 返回渲染后，文字所占用的大致筐体范围
     */
    protected Rectangle2D writeText(Graphics2D g2d, CaptchaConfig ccfg, String text, FontConfig fcfg) {
        if (fcfg == null) {
            fcfg = FONTS.get(RandomUtils.nextInt(FONTS.size()));
        }

        // System.out.println(fcfg.getName());
        // System.out.println(text);

        int maxX = 20;
        int maxY = Math.round(picHeight * 27.0f / 40.0f);

        GeneralPath gp = new GeneralPath();

        for (int i = 0; i < StringUtils.length(text); i++) {
            String letter = String.valueOf(text.charAt(i));

            // 获取随机大小字体
            Font myFont = fcfg.getRandomSize();

            // 获取字体轮廓
            TextLayout tl = new TextLayout(letter, myFont, frc);
            Rectangle2D r2d = tl.getBounds();

            Shape s = null;

            // 根据字体边框求的中心点，设置旋转
            if (ccfg.getMaxRotation() != 0) {
                // 获取旋转角度
                int degree = RandomUtil.rand(ccfg.getMaxRotation() * -1, ccfg.getMaxRotation());
                // 按字符中心点进行旋转
                rotateATF.setToRotation(Math.toRadians(degree), r2d.getCenterX(), r2d.getCenterY());

                // 根据旋转后的bounds计算位移量
                s = tl.getOutline(rotateATF);
                translateATF.setToTranslation(
                        maxX - s.getBounds2D().getX() - r2d.getWidth()
                                * RandomUtil.rand(ccfg.getConnectPercent() / 2, ccfg.getConnectPercent()) / 100.0f,
                        maxY);

                // 将位移与旋转的效果叠加起来
                translateATF.concatenate(rotateATF);

                // 生产字符
                s = tl.getOutline(translateATF);
            } else {
                translateATF.setToTranslation(
                        maxX - r2d.getWidth() * RandomUtil.rand(ccfg.getConnectPercent() / 2, ccfg.getConnectPercent())
                                / 100.0f, maxY);
                s = tl.getOutline(translateATF);
            }

            gp.append(s, false);

            maxX = (int) (s.getBounds2D().getX() + s.getBounds2D().getWidth()
                    * (1 - RandomUtils.nextInt(ccfg.getConnectPercent()) / 100.0f));
        }

        if (ccfg.isbWaveImage()) {
            gp = waveImage(gp);
        }

        return fillGraphics(g2d, gp, ccfg.getFillType());
    }

    private Rectangle2D fillGraphics(Graphics2D g2d, GeneralPath gp, FillType fillType) {

        Rectangle2D r2d = gp.getBounds2D();

        // 进行坐标变换，让图片随机出现在图片的不同位置
        int transX = RandomUtil.rand(-(int) r2d.getX(), (int) (picWidth - r2d.getX() - r2d.getWidth()));
        int transY = RandomUtil.rand(-(int) r2d.getY(), (int) (picHeight - r2d.getY() - r2d.getHeight()));
        translateATF.setToTranslation(transX, transY);
        g2d.transform(translateATF);

        // 根据填充类型进行填充
        switch (fillType) {
        case SOLID:
            g2d.fill(gp);
            break;
        case HOLLOW:
            g2d.draw(gp);
            break;

        // case DOTTED:
        // g2d.setClip(gp);
        //
        // for (int i = 0; i < r2d.getHeight(); i++) {
        // if (i % 3 != 0) {
        // g2d.draw(new Line2D.Double(r2d.getX(), r2d.getY() + i, r2d.getX() +
        // r2d.getWidth(), r2d.getY() + i));
        // }
        // }
        // break;

        default:
            logger.info("Error fill type {}", fillType);
            break;
        }

        return r2d;
    }

    /**
     * 通过GeneralPath的getPathIterator获取坐标点，并产生wave效果。 这种方式虽然可以避免模糊，但会导致在文字不够圆滑。
     * 
     * @param vgp
     *            需要产生wave效果的GeneralPath
     * @return 经过处理的GeneralPath
     */
    protected GeneralPath waveImage(GeneralPath vgp) {

        float xp = nXperiod * RandomUtil.rand(1, 3);
        float yp = nYperiod * RandomUtil.rand(1, 2);
        float kx = RandomUtil.rand(0, 100);
        float ky = RandomUtil.rand(0, 100);

        GeneralPath gp = new GeneralPath();
        PathIterator pi = vgp.getPathIterator(null);

        float[] coords = new float[6];

        while (!pi.isDone()) {
            int pathType = pi.currentSegment(coords);

            switch (pathType) {
            case PathIterator.SEG_MOVETO:
                gp.moveTo(coords[0] + Math.sin(ky + coords[1] / yp) * nYamplitude,
                        coords[1] + Math.sin(kx + coords[0] / xp) * nXamplitude);
                break;
            case PathIterator.SEG_LINETO:
                gp.lineTo(coords[0] + Math.sin(ky + coords[1] / yp) * nYamplitude,
                        coords[1] + Math.sin(kx + coords[0] / xp) * nXamplitude);
                break;
            case PathIterator.SEG_QUADTO:
                double quadX = Math.sin(ky + coords[3] / yp) * nYamplitude;
                double quadY = Math.sin(kx + coords[2] / xp) * nXamplitude;
                gp.quadTo(coords[0] + quadX, coords[1] + quadY, coords[2] + quadX, coords[3] + quadY);
                break;
            case PathIterator.SEG_CUBICTO:
                double cubicX = Math.sin(ky + coords[5] / yp) * nYamplitude;
                double cubicY = Math.sin(kx + coords[4] / xp) * nXamplitude;
                gp.curveTo(coords[0] + cubicX, coords[1] + cubicY, coords[2] + cubicX, coords[3] + cubicY, coords[4]
                        + cubicX, coords[5] + cubicY);
                break;
            case PathIterator.SEG_CLOSE:
                gp.closePath();
                break;
            default:
                logger.info("Unsupported kind of path type {}.", pathType);
            }

            pi.next();
        }

        return gp;
    }

    /**
     * 绘制干扰线
     * 
     * @param g2d
     *            需要绘制干扰线的图片
     * @param ccfg
     *            绘制干扰线的配置参数
     * @param r2d
     *            计划用于在该范围进行干扰线的绘制（目前暂未实现，仅使用了部分参数用于判断文字结束位置）
     */
    protected void writeLine(Graphics2D g2d, CaptchaConfig ccfg, Rectangle2D r2d) {
        int x1 = (int) r2d.getX();
        int x2 = (int) (r2d.getX() + r2d.getWidth());
        int y1 = RandomUtil.rand((int) (r2d.getY()), (int) (r2d.getY() + r2d.getHeight()));
        int y2 = RandomUtil.rand((int) (r2d.getY()), (int) (r2d.getY() + r2d.getHeight()));

        g2d.draw(new Line2D.Float(x1, y1, x2, y2));
    }

    public CaptchaConfig getCaptchaConfig() {
        if (null == captchaConfig) {
            captchaConfig = new CaptchaConfig();
        }
        return captchaConfig;
    }

    // /**
    // * @param g2d
    // * 对图片进行wave操作
    // * @param cfgc
    // * 操作用到的配置参数。
    // */
    // protected void waveImage(Graphics2D g2d, CfgCaptcha cfgc) {
    // // X-axis wave generation
    // float xp = cfgc.getfScale() * nXperiod * RandomUtil.rand(1, 3);
    // float k = RandomUtil.rand(0, 100);
    // for (int i = 0; i < picWidth * cfgc.getfScale(); i++) {
    // g2d.copyArea(i, 0, 1, (int) (picHeight * cfgc.getfScale()), 0,
    // (int) (Math.sin(k + i * 1.0f / xp) * cfgc.getfScale() * nXamplitude));
    // }
    //
    // // Y-axis wave generation
    // k = RandomUtil.rand(0, 100);
    // float yp = cfgc.getfScale() * nYperiod * RandomUtil.rand(1, 2);
    // for (int i = 0; i < picHeight * cfgc.getfScale(); i++) {
    // g2d.copyArea(0, i, (int) (picWidth * cfgc.getfScale()), 1,
    // (int) (Math.sin(k + i * 1.0f / yp) * cfgc.getfScale() * nYamplitude), 0);
    // }
    // }

    // protected static BufferedImage blurImage(BufferedImage image) {
    // float ninth = 1.0f / 9.0f;
    // float[] blurKernel = { ninth, ninth, ninth, ninth, ninth, ninth, ninth,
    // ninth, ninth };
    //
    // Map map = new HashMap();
    //
    // map.put(RenderingHints.KEY_INTERPOLATION,
    // RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    //
    // map.put(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    //
    // map.put(RenderingHints.KEY_ANTIALIASING,
    // RenderingHints.VALUE_ANTIALIAS_ON);
    //
    // RenderingHints hints = new RenderingHints(map);
    // BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel),
    // ConvolveOp.EDGE_NO_OP, hints);
    // return op.filter(image, null);
    // }
    //
    // protected static BufferedImage resize(BufferedImage image, int width, int
    // height) {
    // int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB :
    // image.getType();
    // BufferedImage resizedImage = new BufferedImage(width, height, type);
    // Graphics2D g = resizedImage.createGraphics();
    // g.setComposite(AlphaComposite.Src);
    //
    // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
    // RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    //
    // g.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    //
    // g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    // RenderingHints.VALUE_ANTIALIAS_ON);
    //
    // g.drawImage(image, 0, 0, width, height, null);
    // g.dispose();
    // return resizedImage;
    // }
}
