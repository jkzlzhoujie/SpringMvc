package com.example.demo.common.export;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by janseny on 2018/11/28.
 */
public class WorkbookUtil {

    /**
     * excel 文件导出
     * @param wwb
     * @param listMap 数据
     * @param columnMap 每列对应的数据集中的key
     * @param header  表头
     * @throws Exception
     */
    public void write(WritableWorkbook wwb, List<Map<String,Object>> listMap,Map<Integer,String> columnMap,String [] header) throws Exception {
        try {
            WritableSheet ws;
            ws = wwb.createSheet("sheet",1);
            addHeader(ws,header);
            int i = 1;
            for (Map<String,Object> map : listMap ) {
                for(int key :columnMap.keySet()){
                    String cloumn = columnMap.get(key);
                    if(map.get(cloumn) != null){
                        addCell(ws, i, key, map.get(cloumn).toString());
                    }
                }
                i++;
            }
            wwb.write();
            wwb.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (wwb != null) {
                wwb.close();
            }
            throw e;
        }
    }

    public void addHeader(WritableSheet ws,String[] header) throws WriteException {
        int i = 0;
        for (String h : header) {
            addCell(ws, 0, i, h);//表名，行，列，header
            i++;
        }
    }

    //添加单元格内容
    public void addCell(WritableSheet ws, int row, int column,  String data) throws WriteException {
        Label label = new Label(column ,row, data);
        ws.addCell(label);
    }
}
