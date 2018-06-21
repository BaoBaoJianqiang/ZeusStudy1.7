package com.example.jianqiang.mypluginlibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author weishu
 * @date 16/3/29
 */
public class Utils {

    /**
     * 把Assets里面得文件复制到 /data/data/files 目录下
     *
     * @param context
     * @param sourceName
     */
    public static void extractAssets(Context context, String sourceName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(sourceName);
            File extractFile = context.getFileStreamPath(sourceName);
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(fos);
        }

    }

    // --------------------------------------------------------------------------
    private static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable e) {
            // ignore
        }
    }


    public static boolean copyFileFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);



            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "[copyFileFromAssets] IOException "+e.toString());
        }
        return copyIsFinish;
    }

    // 获取手机CPU类型信息
    static public String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""}; // 1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        FileReader fr = null;
        BufferedReader localBufferedReader = null;
        try {
            fr = new FileReader(str1);
            localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2]; // cpu频率。
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();

                if (localBufferedReader != null)
                    localBufferedReader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Log.i(TAG, "cpuinfo:" + cpuInfo[0] + " " + cpuInfo[1]);
        if ((cpuInfo[0].toLowerCase().contains("64"))) {
            return "arm64-v8a";
        } else if ((cpuInfo[0].toLowerCase().contains("armv7"))) {
            return "armeabi-v7a";
        } else if ((cpuInfo[0].toLowerCase().contains("arm"))) {
            return "armeabi";
        } else if ((cpuInfo[0].toLowerCase().contains("mips"))) {
            return "mips";
        } else {
            return "x86";
        }
    }

    public static String UnzipSpecificFile(String zipFile, String targetDir) {
        int BUFFER = 4096; // 这里缓冲区我们使用4KB，
        String strEntry; // 保存每个zip的条目名称
        ZipInputStream zis = null;
        try {
            BufferedOutputStream dest = null; // 缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; // 每个zip条目的实例

            while ((entry = zis.getNextEntry()) != null) {
                try {
                    // Log.i("Unzip: ","="+ entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();

                    boolean find = false;
                    if(!strEntry.endsWith(".so")) {
                        continue;
                    }

                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());

                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }

                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();

                    return entryDir.getAbsolutePath();

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (null != dest)
                        dest.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != zis)
                    zis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
