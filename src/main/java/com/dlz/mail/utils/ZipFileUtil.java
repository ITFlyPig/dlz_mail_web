package com.dlz.mail.utils;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by wangyuelin on 2017/12/21.
 */
public class ZipFileUtil {
    private static final Logger logger = LogManager.getLogger(ZipFileUtil.class);

    /**
     * 把文件压缩成zip格式
     * @param files         需要压缩的文件
     * @param zipFilePath 压缩后的zip文件路径   ,如"D:/test/aa.zip";
     */
    public static void compressFiles2Zip(File[] files, String zipFilePath) {
        logger.debug("开始压缩文件：zip文件的路径：" + zipFilePath);
        if(files != null && files.length >0) {
            if(isEndsWithZip(zipFilePath)) {
                ZipArchiveOutputStream zaos = null;
                try {
                    File zipFile = new File(zipFilePath);
                    zaos = new ZipArchiveOutputStream(zipFile);
                    //Use Zip64 extensions for all entries where they are required
                    zaos.setUseZip64(Zip64Mode.AsNeeded);

                    //将每个文件用ZipArchiveEntry封装
                    //再用ZipArchiveOutputStream写到压缩文件中
                    for(File file : files) {
                        if(file != null) {
                            ZipArchiveEntry zipArchiveEntry  = new ZipArchiveEntry(file,file.getName());
                            zaos.putArchiveEntry(zipArchiveEntry);
                            InputStream is = null;
                            try {
                                is = new BufferedInputStream(new FileInputStream(file));
                                byte[] buffer = new byte[1024 * 5];
                                int len = -1;
                                while((len = is.read(buffer)) != -1) {
                                    //把缓冲区的字节写入到ZipArchiveEntry
                                    zaos.write(buffer, 0, len);
                                }
                                //Writes all necessary data for this entry.
                                zaos.closeArchiveEntry();
                            }catch(Exception e) {
                                throw new RuntimeException(e);
                            }finally {
                                if(is != null)
                                    is.close();
                            }

                        }
                    }
                    zaos.finish();
                    logger.debug("文件压缩成功");
                }catch(Exception e){
                    logger.debug("文件压缩失败");
                    throw new RuntimeException(e);
                }finally {
                    try {
                        if(zaos != null) {
                            zaos.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }

            }

        }
        logger.debug("文件压缩失败");

    }

    /**
     * 判断文件名是否以.zip为后缀
     * @param fileName        需要判断的文件名
     * @return 是zip文件返回true,否则返回false
     */
    public static boolean isEndsWithZip(String fileName) {
        boolean flag = false;
        if(fileName != null && !"".equals(fileName.trim())) {
            if(fileName.endsWith(".ZIP")||fileName.endsWith(".zip")){
                flag = true;
            }
        }
        return flag;
    }
}
