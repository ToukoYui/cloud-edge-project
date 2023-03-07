package com.dgut.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.model.entity.Pod;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;

import java.util.List;

public interface PodService extends IService<Pod> {
    List<Pod> getPods(String ns) throws ApiException;
    void createPod(V1Pod pod) throws ApiException;
}
