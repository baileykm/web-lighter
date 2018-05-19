package com.pr.web.lighter.test.service;

import com.pr.web.lighter.test.vo.VOTest;
import com.pr.web.lighter.utils.upload.FileInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service4Test {
    public String getSomeWords() {
        return "words from Service4Test";
    }

    public Map<String, Object> getResponse(String project, Integer id, String str, Integer i, Date date, String[] arr, VOTest singleVO, List<VOTest> voList, List<FileInfo> fileInfos, FileInfo firstFile) {
        Map<String, Object> rt = new HashMap<>();
        rt.put("project", project);
        rt.put("id", id);
        rt.put("str", str);
        rt.put("i", i);
        rt.put("date", date);
        rt.put("arr", arr);
        rt.put("singleVO", singleVO);
        rt.put("voList", voList);
        rt.put("fileInfos", fileInfos);
        rt.put("firstFile", firstFile);
        return rt;
    }
}
