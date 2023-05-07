package com.dgut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.model.dto.PodLogDto;
import com.dgut.model.entity.Node;
import com.dgut.model.entity.PodLog;
import io.kubernetes.client.openapi.ApiException;

import java.util.List;


public interface PodLogService extends IService<PodLog> {

    String getPodLogInfo(PodLogDto podLogDto) throws ApiException;
}
