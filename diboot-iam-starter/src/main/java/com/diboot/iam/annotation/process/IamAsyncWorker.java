/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.annotation.process;

import com.diboot.iam.auth.IamCustomize;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.entity.IamOperationLog;
import com.diboot.iam.service.IamLoginTraceService;
import com.diboot.iam.service.IamOperationLogService;
import com.diboot.iam.util.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 异步相关处理
 * @author mazc@dibo.ltd
 * @version v2.1.2
 * @date 2020/09/21
 */
@Slf4j
@Async
@Component
public class IamAsyncWorker {
    @Autowired
    private IamLoginTraceService iamLoginTraceService;
    @Autowired
    private IamOperationLogService iamOperationLogService;
    @Autowired(required = false)
    private IamCustomize iamCustomize;
    @Autowired
    private HttpServletRequest request;
    /**
     * 保存登录日志
     * @param loginTrace
     */
    public void saveLoginTraceLog(IamLoginTrace loginTrace){
        try{
            // 记录客户端信息
            String userAgent = HttpHelper.getUserAgent(request);
            String ipAddress = HttpHelper.getRequestIp(request);
            loginTrace.setUserAgent(userAgent).setIpAddress(ipAddress);
            iamLoginTraceService.createEntity(loginTrace);
        }
        catch (Exception e){
            log.error("保存登录日志异常", e);
        }
    }

    /**
     * 保存操作日志
     * @param operationLog
     */
    public void saveOperationLog(IamOperationLog operationLog) {
        try{
            // 操作用户信息
            BaseLoginUser loginUser = iamCustomize.getCurrentUser();
            if(loginUser != null){
                operationLog.setUserType(loginUser.getClass().getSimpleName())
                        .setUserId(loginUser.getId())
                        .setUserRealname(loginUser.getDisplayName());
            }
            iamOperationLogService.createEntity(operationLog);
        }
        catch (Exception e){
            log.error("保存操作日志异常", e);
        }
    }

}