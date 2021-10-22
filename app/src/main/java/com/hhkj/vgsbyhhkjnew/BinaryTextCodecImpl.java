package com.hhkj.vgsbyhhkjnew;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ProjectName: VgsByHhkj
 * @Package: com.hhkj.vgsbyhhkj
 * @ClassName: BinaryTextCodecImpl
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/2/4 10:00
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */
public class BinaryTextCodecImpl implements BinaryTextCodec {

    private static final String TEXT_SPLIT_CHAR = ",";

    @Override
    public String encode(FileInputStream fileInputStream) throws IOException {
        if (fileInputStream == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        /*int i = 0;
        while ((i = fileInputStream.read()) != -1) {
            builder.append(i );        }*/
        DataInputStream dis = new DataInputStream(fileInputStream);
        while (dis.available() > 0) {
            builder.append(dis.read());
        }
        return builder.toString();
    }

    @Override
    public void decode(FileOutputStream fileOutputStream, String text) throws IOException {
        if (fileOutputStream == null || text == null) {
            return;
        }
        String[] lineone = text.split(TEXT_SPLIT_CHAR);
        for (int j = 0; j < lineone.length; j++) {
            fileOutputStream.write(Integer.valueOf(lineone[j]));
        }
        fileOutputStream.flush();
    }
}
