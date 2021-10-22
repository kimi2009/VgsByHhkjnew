package com.hhkj.vgsbyhhkjnew;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @ProjectName: VgsByHhkj
 * @Package: com.hhkj.vgsbyhhkj
 * @ClassName: FileTransitionUtil
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/2/4 9:57
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class FileTransitionUtil {
    private static BinaryTextCodec binaryTextCodec = new BinaryTextCodecImpl();

    public static void binaryToText(String binaryFilePath, String textFilePath) throws IOException {
        File binaryFile = new File(binaryFilePath);
        if (!binaryFile.exists()) {
            System.out.println("====转换的二进制文件不存在....");
            return;
        }
        File textFile = new File(textFilePath);
        if (!textFile.exists())
            textFile.createNewFile();

        FileInputStream fileInputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            fileInputStream = new FileInputStream(binaryFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textFile)));

            String encoder = binaryTextCodec.encode(fileInputStream);
            if (encoder != null) {
                bufferedWriter.write(encoder);
                bufferedWriter.flush();
                System.out.println("====成功将【"+binaryFilePath+"】转换成文本文件【"+textFilePath+"】");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }


    public static void textToBinary(String binaryFilePath, String textFilePath) throws IOException {
        File textFile = new File(textFilePath);
        if (!textFile.exists()) {
            System.out.println("转换的文本文件不存在....");
            return;
        }
        File binaryFile = new File(binaryFilePath);
        if (!binaryFile.exists())
            binaryFile.createNewFile();

        FileOutputStream fileOutputStream = null;
        BufferedReader bufferedReader = null;

        try {
            fileOutputStream = new FileOutputStream(binaryFile);
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile)));

            binaryTextCodec.decode(fileOutputStream, bufferedReader.readLine());
            System.out.println("成功将【"+textFilePath+"】转换成二进制文件【"+binaryFilePath+"】");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

}
