package com.hhkj.vgsbyhhkjnew;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ProjectName: VgsByHhkj
 * @Package: com.hhkj.vgsbyhhkj
 * @ClassName: BinaryTextCodec
 * @Description:
 * @Author: D.Han
 * @CreateDate: 2021/2/4 9:59
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version: 1.0
 */

public interface BinaryTextCodec {

    String encode(FileInputStream fileInputStream) throws IOException;

    void decode(FileOutputStream outputStream, String text) throws IOException;


}
