package com.fdzang.micro.fabric.driver.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {

    /**
     * 将输入流转为字节数组
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] inputStreamToByte(InputStream is) throws IOException {
        int bufferSize = 1024;
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[bufferSize];

        int rc;
        while((rc = is.read(buff, 0, bufferSize)) > 0) {
            swapStream.write(buff, 0, rc);
        }

        return swapStream.toByteArray();
    }
}
