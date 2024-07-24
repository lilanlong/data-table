package com.lll.controller;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.IoUtil;
import com.lll.datatable.DataTable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;


@RestController
@RequestMapping("/dataTable")
public class DataTableController {

    @GetMapping("/test")
    public void test() {
        try {
            List<JSONObject> dataList = new ArrayList<JSONObject>();
            for (int i = 0; i < 100; i++) {
                dataList.add(JSONObject.of("id", i));
            }
            DataTable dataTable = new DataTable(dataList);
            List<JSONObject> rDataList = dataTable.select("id=3");
            System.out.println(JSON.toJSONString(rDataList));
            IoUtil.close(dataTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
