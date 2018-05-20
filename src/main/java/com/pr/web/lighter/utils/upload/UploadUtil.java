package com.pr.web.lighter.utils.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具
 *
 * @author Bailey
 */
public class UploadUtil {

    // 3MB, 内存中数据超过此阀值时使用临时文件缓冲数据
    final private static int MEMORY_THRESHOLD = 1024 * 1024 * 3;

    /**
     * 处理文件上传表单数据, 保存文件到磁盘, 提取文件信息及表单字段值并返回
     *
     * @param request            HttpServletRequest请求对象
     * @param uploadRelativePath 文件保存路径, 相对路径
     * @param serverNameRule     服务器端文件命名规则, 空字符串("")表示使用原文件名. 规则中的星号("*")表示此部分使用UUID替换, 例如: "tmp*" 表示使用 "temp" + 32位UUID 作为文件名. 文件扩展名始终与原文件一致
     * @param maxFileSize        允许上传的单个文件最大字节数
     * @param maxRequestSize     HttpServletRequest最大字节数
     * @return 文件上传处理结果
     */
    public UploadResult upload(HttpServletRequest request, String uploadRelativePath, String serverNameRule, int maxFileSize, int maxRequestSize) {

        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new RuntimeException("File Upload REQUIRE set Content-Type as 'multipart/form-data'");
        }

        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);

        // 设置最大文件上传值
        upload.setFileSizeMax(maxFileSize);

        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(maxRequestSize);

        // 中文处理
        upload.setHeaderEncoding("UTF-8");

        // 构造临时路径来存储上传的文件
        // 这个路径相对当前应用的目录
        String uploadPath = request.getServletContext().getRealPath("./") + File.separator + uploadRelativePath;

        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        UploadResult result = new UploadResult();
        try {
            // 解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                // 迭代处理表单数据
                for (FileItem item : formItems) {
                    String fieldName = item.getFieldName();
                    if (!item.isFormField()) {      // 文件
                        if (item.getSize() == 0) continue;
                        String origFileName = new File(item.getName()).getName();

                        String serverFileName;
                        if ("".equals(serverNameRule)) {
                            serverFileName = origFileName;    // 传入的服务器端文件名为null, 则使用客户端原文件名保存
                        } else {
                            serverFileName = serverNameRule.replaceAll("\\*", UUID.randomUUID().toString().replace("-", ""))
                                    + origFileName.substring(origFileName.lastIndexOf(".", origFileName.length()));
                        }

                        String filePath  = uploadPath + File.separator + serverFileName;
                        File   storeFile = new File(filePath);
                        item.write(storeFile); // 保存文件到硬盘
                        result.addFile(fieldName, origFileName, item.getSize(), item.getContentType(), serverFileName);
                    } else {    // 普通表单字段
                        result.addParameter(fieldName, item.getString());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("上传文件出错!!!", e);
        }

        return result;
    }


}
