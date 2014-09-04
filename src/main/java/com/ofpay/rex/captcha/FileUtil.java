package com.ofpay.rex.captcha;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author of546
 * 
 */
public class FileUtil {

    public static boolean file_exists(String filename) {
        try {
            return (new File(filename)).exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String fgets(RandomAccessFile raf) {
        try {
            if (raf.getFilePointer() == 0) {
                return raf.readLine();
            }
            char chr = (char) raf.read();
            System.out.println(chr);
            if (chr != '\r' && chr != '\n') {
                raf.seek(raf.getFilePointer() - 2);
                return fgets(raf);
            }
            return raf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long filesize(File file) {
        return file.length();
    }

    public static RandomAccessFile fopen(File file, String model) {
        if (model == null) {
            model = "r";
        }
        try {
            return new RandomAccessFile(file, model);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean fseek(RandomAccessFile raf, long sk) {
        try {
            raf.seek(sk);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void fclose(RandomAccessFile raf) {
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public static Map<String, CfgFont> fonts = new HashMap<String, CfgFont>()
    // {
    // private static final long serialVersionUID = 2413410573604328031L;
    // {
    // put("Antykwa", new CfgFont(-3, 27, 30, "AntykwaBold.ttf"));
    // put("Candice", new CfgFont(-1.5f, 28, 31, "Candice.ttf"));
    // put("DingDong", new CfgFont(-2, 24, 30, "Ding-DongDaddyO.ttf"));
    // put("Duality", new CfgFont(-2, 30, 38, "Duality.ttf"));
    // put("Heineken", new CfgFont(-2, 24, 34, "Heineken.ttf"));
    // put("Jura", new CfgFont(-2, 28, 32, "Jura.ttf"));
    // put("StayPuft", new CfgFont(-1.5f, 28, 32, "StayPuft.ttf"));
    // put("Times", new CfgFont(-2, 28, 34, "TimesNewRomanBold.ttf"));
    // put("VeraSans", new CfgFont(-1, 20, 28, "VeraSansBold.ttf"));
    // }};

    public static void main(String[] args) throws IOException {
        File f = new File("src/main/resources/en.php");
        RandomAccessFile raf = fopen(f, "r");
        int length = StringUtils.length(fgets(raf));
        System.out.println("length=" + length);
        int size = (int) filesize(f);
        System.out.println("size=" + size);
        int line = RandomUtil.rand(1, (size / length) - 2);
        System.out.println("line=" + line);
        boolean seek = fseek(raf, length * line);
        System.out.println("seek=" + seek);
        String text = fgets(raf);
        System.out.println("text=" + text.trim());
        fclose(raf);

        // List<String> keys = new ArrayList<String>(fonts.keySet());
        // String randomKey = keys.get(RandomUtils.nextInt(keys.size()));
        // CfgFont font = fonts.get(randomKey);
        // System.out.println(font);
    }

}
