package com.dgut.controller;

import com.dgut.model.dto.PodLogDto;
import com.dgut.model.entity.R;
import com.dgut.service.PodLogService;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dgut")
@CrossOrigin
public class PodLogController {
    @Autowired
    private PodLogService podLogService;


    /**
     * @Author: ToukoYui
     * @Date: 2023/4/2 18:50
     * @Description: 获取指定namespace下的pod日志
     **/
    @PostMapping("/log")
    public R<String> getPodLog(@RequestBody PodLogDto podLogDto) throws ApiException {
        String logInfo = podLogService.getPodLogInfo(podLogDto);
        return R.success(logInfo,"获取Pod日志成功");
    }
}
