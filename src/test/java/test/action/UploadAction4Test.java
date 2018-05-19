package com.pr.web.lighter.test.action;

import com.pr.web.lighter.test.vo.VOTest;
import com.pr.web.lighter.action.ActionResult;
import com.pr.web.lighter.action.ActionSupport;
import com.pr.web.lighter.annotation.*;
import com.pr.web.lighter.test.service.Service4Test;
import com.pr.web.lighter.utils.upload.FileInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Bailey
 */
public class UploadAction4Test extends ActionSupport {

    @Request(url = "/{project}/uploadFile/{id}")
    @Upload
    public ActionResult uploadFile(@Inject Service4Test service,
                                   @Param(name = "project") String project,
                                   @Param(name = "id") Integer id,
                                   @Param(name = "str") String str,
                                   @Param(name = "int") Integer i,
                                   @Param(name = "date") Date date,
                                   @Param(name = "arr") String[] arr,
                                   @Param(name = "vo") com.pr.web.lighter.test.vo.VOTest vo,
                                   @Param(name = "volist") List<VOTest> voList,
                                   @ParamFileInfo List<FileInfo> fileInfos,
                                   @ParamFileInfo FileInfo firstFile) {

        Map<String, Object> result = service.getResponse(project, id, str, i,  date, arr, vo, voList, fileInfos, firstFile);
        return ActionResult.success(result);
    }
}
