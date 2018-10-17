package com.dlz.mail.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {
    private static final Logger logger =  LogManager.getLogger(ExcelUtil.class);
    private static String TAG = "ExcelUtil";
    private static final int FLUSH_ROW = 1000;//行开始从内存持久化到磁盘

    /**
     * 使用pio创建excel文件
     *
     * @param outPutPath
     * @param fileName
     * @param data
     * @return
     */
    public static String createExcelByPOI(String outPutPath, String fileName, List<List<Object>> data) {
        logger.debug("开始创建Excel文件");
        if (TextUtil.isEmpty(outPutPath)) {
            logger.debug("createExcelByPOI创建excel失败，保存的路径为空");
            return "";
        }
        if (TextUtil.isEmpty(fileName)) {
            logger.debug("createExcelByPOI创建excel失败，文件名为空");
            return "";
        }
        if (data == null || data.size() == 0) {
            logger.debug("createExcelByPOI创建excel失败，数据集为空");
            return "";
        }
        logger.debug("查询到的总行数：" + data.size());

        SXSSFWorkbook wb = null;
        try {
            //第一步创建workbook
            logger.debug("创建SXSSFWorkbook");
             wb = new SXSSFWorkbook(FLUSH_ROW);
            //第二步创建sheet
            String curTime = TimeUtil.stampToDate(System.currentTimeMillis());
            logger.debug("创建Sheet");
            Sheet sheet = wb.createSheet(curTime);
            //第三步创建行row:添加表头0行
            logger.debug("创建和设置CellStyle");
            CellStyle style = wb.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);//居中


            logger.debug("开始循环" );
            int rowSize = data.size();//行的size
            for (int i = 0; i < rowSize; i++) {
//                logger.debug("外层循环" );
                Row row = sheet.createRow(i);//创建行
                List<Object> rowData = data.get(i);//每一行的数据
                for (int j = 0; j < rowData.size(); j++) {
//                    logger.debug("内层循环" );
                    //创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
                    Object cellData = rowData.get(j);
                    String  cellStr= "";
                    if (cellData != null){
                        cellStr = cellData.toString();
                    }
                    if (TextUtil.isEmpty(cellStr)){
                        cellStr = " ";
                    }
                    Cell cell = row.createCell(j);//创建列
                    cell.setCellValue(cellStr);
                    cell.setCellStyle(style);

                }
            }

            logger.debug("调整宽度开始" );
            //自动调整每一列的宽度
//            for (int i = 0; i < columNum; i++){
//                logger.debug("调整列：" + i );
//                sheet.autoSizeColumn(i);
//            }
            logger.debug("调整宽度结束" );



        }catch (Exception e){
            e.printStackTrace();
            logger.debug("异常" );
            logger.debug(e.getLocalizedMessage());
        }

        logger.debug("将数据生成文件开始：outPutPath：" + outPutPath + " fileName：" + fileName );
        //第六步将生成excel文件保存到指定路径下
        try {
            String fileStr = outPutPath + File.separator + fileName + ".xlsx";
            logger.debug("创建的文件的路劲：" + fileStr);

            File file = new File(fileStr);
            File parent = file.getParentFile();
            logger.debug("开始检测文件的父文件夹是否存在");
            if (!parent.exists()) {
                logger.debug("不存在，开始创建文件的父文件夹");
                parent.mkdirs();
            }else {
                logger.debug("父文件夹存在");
            }
            logger.debug("开始检测文件是否存在");
            if (!file.exists()) {
                try {
                    logger.debug("不存在，开始创建文件");
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.debug( "缓存文件创建失败");
                }
            }else {
                logger.debug("文件存在");
            }

            logger.debug("文件创建成功");

            FileOutputStream fout = new FileOutputStream(file);
            wb.write(fout);
            fout.close();
            // dispose of temporary files backing this workbook on disk
            wb.dispose();
            logger.debug("文件写入成功");
            return fileStr;
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug(e.getLocalizedMessage());
            logger.debug("文件写入异常");
        }

        return "";

    }

}
