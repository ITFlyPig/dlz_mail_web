package com.dlz.mail.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HtmlTableUtil {
    private static final Logger logger = LoggerFactory.getLogger(HtmlTableUtil.class);
    private static String TABLE_PRE = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "\n" +
            "    <style type=\"text/css\">\n" +
            "        .tabtop13 {\n" +
            "            margin-top: 13px;\n" +
            "        }\n" +
            "        .tabtop13 td{\n" +
            "            background-color:#ffffff;\n" +
            "            height:25px;\n" +
            "            line-height:150%;\n" +
            "        }\n" +
            "        .font-center{ text-align:center}\n" +
            "        .btbg1{background:#f2fbfe !important;}\n" +
            "        .btbg2{background:#f3f3f3 !important;}\n" +
            "        .title {\n" +
            "            font-family: 微软雅黑;\n" +
            "            font-size: 16px;\n" +
            "            font-weight: bold;\n" +
            "            padding-right: 10px;\n" +
            "        }\n" +
            "\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<table border=\"0\" cellspacing=\"1\" cellpadding=\"10\" bgcolor=\"#cccccc\" class=\"tabtop13\" align=\"center\">";

    private static final String TABLE_SUFIX = "</table>\n" +
            "\n" +
            "</body>\n" +
            "</html>";


    /**
     * 据数据生成对应的html表格
     * @param data
     * @return
     */
    public static String createHtmlTable(List<List<Object>> data){
        logger.debug("创建html函数开始");
        if (data == null || data.size() == 0){
            logger.debug("数据为空，直接退出函数");
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(TABLE_PRE);
        int rowSize = data.size();
        logger.debug("开始循环");
        for (int i = 0; i < rowSize; i++){//总的行数
            buffer.append("<tr>");
            List<Object> colData = data.get(i);
            int colSize = colData.size();
            for (int j = 0 ; j < colSize; j++){//每一行
                Object obj = colData.get(j);
                String dataStr = obj.toString();
                String cell = "";
                if (i == 0){//标题
                    cell = "<td class=\"btbg1 title font-center\">" + dataStr + "</td>";
                }else {//普通的数据
                    cell = "<td class=\"font-center\">" + dataStr + "</td>";
                }
                buffer.append(cell);

            }
            buffer.append("</tr>");
        }
        buffer.append(TABLE_SUFIX);
        logger.debug("创建表结束");
        return buffer.toString();
    }


}
