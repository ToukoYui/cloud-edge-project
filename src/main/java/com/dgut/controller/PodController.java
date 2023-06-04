package com.dgut.controller;


import com.dgut.model.dto.PodDto;
import com.dgut.model.entity.Pod;
import com.dgut.model.entity.R;
import com.dgut.service.PodService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dgut")
@CrossOrigin
public class PodController {
    @Autowired
    private PodService podService;


    /**
     * @Author: ToukoYui
     * @Date: 2023/3/7 14:56
     * @Description: 获取pod
     **/
    @GetMapping("/pod")
    public R<List<Pod>> getPods(@RequestParam("namespace") String ns) throws ApiException {
        List<Pod> podList = podService.getPods(ns);
        return R.success(podList,"获取pod成功");
    }

    /**
     * @Author: ToukoYui
     * @Date: 2023/3/7 17:12
     * @Description: 创建新pod
     **/
    @PostMapping("/pod")
    public R createPod(@RequestBody V1Pod pod) throws ApiException {
        podService.createPod(pod);
        return R.success("创建pod成功");
    }


    /**
     * @Author: ToukoYui
     * @Date: 2023/3/8 14:41
     * @Description: 删除pod
     **/
    @DeleteMapping("/pod")
    public R deletePod(@RequestBody List<PodDto> podDtoList) throws ApiException{
        System.out.println("podDto = " + podDtoList);
        boolean isDeleted = podService.deletePod(podDtoList);
        if (!isDeleted){
            return R.error(500,"删除pod失败");
        }
        return R.success("删除pod成功");
    }

}
