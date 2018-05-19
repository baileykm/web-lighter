package com.pr.web.lighter.utils.upload;

import com.google.gson.Gson;

import java.util.*;

/**
 * 上传结果信息. 包含成功上传的文件信息集合 和 额外的参数集合
 * @author Bailey
 */
public  class UploadResult {

    private List<FileInfo> files = new ArrayList<>();

    private Map<String, List<String>> parameters = new LinkedHashMap<>();

    public void addFile(String fieldName, String origFileName, long fileSize, String fileType, String serverFileName) {
        FileInfo file = new FileInfo(fieldName, origFileName, fileSize, fileType, serverFileName);
        files.add(file);
    }

    public void addParameter(String name, String value) {
        List<String> valueList = parameters.get(name);
        if (valueList == null) {
            valueList = new ArrayList<>();
            parameters.put(name, valueList);
        }
        valueList.add(value);
    }

    /**
     * 获得成功上传的文件信息
     * @return 成功上传的文件信息
     */
    public List<FileInfo> getFiles() {
        return files;
    }

    /**
     * 获得上传文件时同时携带的参数
     * @return 上行参数
     */
    public Map<String, String[]> getParameters() {
        // 把参数转成 Map<String, String[]>, 以便和普通的表单提交数据格式统一 (Content-Type : application/x-www-form-urlencoded)
        Map<String, String[]> rt = new LinkedHashMap<>();
        for (Iterator<String> itr = parameters.keySet().iterator(); itr.hasNext();) {
            String       name  = itr.next();
            List<String> value = parameters.get(name);
            rt.put(name, value.toArray(new String[value.size()]));
        }
        return rt;
    }
}