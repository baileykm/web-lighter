package com.pr.web.lighter.test.action;

import com.pr.web.lighter.test.vo.VOTest;
import com.pr.web.lighter.action.ActionResult;
import com.pr.web.lighter.action.ActionSupport;
import com.pr.web.lighter.annotation.Inject;
import com.pr.web.lighter.annotation.Param;
import com.pr.web.lighter.annotation.Request;
import com.pr.web.lighter.test.service.Service4Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Bailey
 */
public class Action4Test extends ActionSupport {

    @Request(url = "/{project}/doSomeThing/{id}")
    public ActionResult doSomeThing(@Inject Service4Test service,
                                    @Param(name = "project") String project,
                                    @Param(name = "id") Integer id,
                                    @Param(name = "str") String str,
                                    @Param(name = "int") Integer i,
                                    @Param(name = "date") Date date,
                                    @Param(name = "arr") String[] arr,
                                    @Param(name = "vo") com.pr.web.lighter.test.vo.VOTest vo,
                                    @Param(name = "volist") List<VOTest> voList) {
        Map<String, Object> result = service.getResponse(project, id, str, i,  date, arr, vo, voList, null, null);
        return ActionResult.success(result);

    }
}
