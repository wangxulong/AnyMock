package com.dxm.anymock.web.service.controller;

import com.dxm.anymock.common.base.BaseResponse;
import com.dxm.anymock.common.base.GlobalConstant;
import com.dxm.anymock.web.biz.HostInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(GlobalConstant.URL_PREFIX_API_V2)
public class HostInfoController {

   @Autowired
   private HostInfoService hostInfoService;

    @PostMapping("/host_info/core")
    @ResponseBody
    public BaseResponse selectCoreHostInfo() {
        return BaseResponse.success(hostInfoService.selectCoreHostInfo());
    }
}