package com.dgut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.mapper.PodLogMapper;
import com.dgut.mapper.PodMapper;
import com.dgut.model.dto.PodDto;
import com.dgut.model.dto.PodLogDto;
import com.dgut.model.entity.Pod;
import com.dgut.model.entity.PodLog;
import com.dgut.service.PodLogService;
import com.dgut.service.PodService;
import com.dgut.utils.K8sClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PodLogServiceImpl extends ServiceImpl<PodLogMapper, PodLog> implements PodLogService {

    @Override
    public String getPodLogInfo(PodLogDto podLogDto) throws ApiException {
        CoreV1Api coreV1Api = new CoreV1Api(K8sClient.getApiClient());
        String containName = podLogDto.getContainer();
        containName = containName.equals("all")? null:containName;
        String logString = coreV1Api.readNamespacedPodLog(podLogDto.getName(), podLogDto.getNamespace(), containName, null, null, null, null, null, null, null, null);
        return logString;
    }

    @Override
    public List<String> getContainerByNsandPod(String ns, String pod) throws ApiException {
        CoreV1Api coreV1Api = new CoreV1Api(K8sClient.getApiClient());
        V1Pod v1Pod = coreV1Api.readNamespacedPod(pod, ns, null, null, null);
        List<V1Container> containers = v1Pod.getSpec().getContainers();
        List<String> collect = containers.stream().map(V1Container::getName).collect(Collectors.toList());
        System.out.println(collect);
        return collect;
    }


}
