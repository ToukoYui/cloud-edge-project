package com.dgut.service.impl;

import ch.qos.logback.classic.spi.EventArgUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.mapper.NodeMapper;
import com.dgut.mapper.PodMapper;
import com.dgut.model.dto.PodDto;
import com.dgut.model.entity.Node;
import com.dgut.model.entity.Pod;
import com.dgut.service.NodeService;
import com.dgut.service.PodService;
import com.dgut.utils.K8sClient;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.StringUtil;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class PodServiceImpl extends ServiceImpl<PodMapper, Pod> implements PodService {


    @Override
    public List<Pod> getPods(String ns) throws ApiException {
        CoreV1Api coreV1Api = new CoreV1Api(K8sClient.getApiClient());
        V1PodList podList = null;
        // ns为空取所有ns下的pod，否则取指定ns下的pod
        if (StringUtils.isEmpty(ns)) {
            podList = coreV1Api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        } else {
            podList = coreV1Api.listNamespacedPod(ns, null, null, null, null, null, null, null, null, null);
        }
        List<Pod> resultList = new ArrayList<>();
        for (V1Pod item :
                podList.getItems()) {
            // 排除k8s自带的命名空间下的pod
            if (K8sClient.excludeNs(item.getMetadata().getNamespace())) {
                continue;
            }
            // 遍历容器集合，获取镜像和映射端口
            List<V1Container> containers = item.getSpec().getContainers();

            List<String> images = new ArrayList<>();
            List<String> portMapperStrList = new ArrayList<>();


            for (V1Container container :
                    containers) {
                images.add(container.getImage());
                // 如果该container没有设置端口映射，则直接返回默认字符串，否则获取V1ContainerPort对象中的端口数据
                if (container.getPorts() == null) {
                    portMapperStrList.add("--:--");
                    continue;
                }
                String portMapperStr = getPortMapper(container.getPorts().get(0));// 一个容器可能有多个端口映射，这里一个容器只取一个映射
                portMapperStrList.add(portMapperStr);
            }

            // 封装pod
            Pod pod = new Pod();
            V1PodStatus status = item.getStatus();
            pod.setStatus(status.getPhase());
            pod.setName(item.getMetadata().getName());
            pod.setNamespace(item.getMetadata().getNamespace());
            pod.setImage(images);
            pod.setPortMapper(portMapperStrList);
            pod.setIp(status.getPodIP());
            pod.setCreatedTime(status.getStartTime().toString("yyyy-MM-dd HH:mm:ss"));
            resultList.add(pod);
        }

        return resultList;
    }

    @Override
    public void createPod(V1Pod pod) throws ApiException {
        CoreV1Api coreV1Api = new CoreV1Api(K8sClient.getApiClient());
        String ns = pod.getMetadata().getNamespace();
        V1Pod result = coreV1Api.createNamespacedPod(ns, pod, null, null, null);
        System.out.println(result);
    }

    @Override
    public boolean deletePod(List<PodDto> podDtoList) throws ApiException {
        CoreV1Api coreV1Api = new CoreV1Api(K8sClient.getApiClient());
        try {
            for (PodDto podDto : podDtoList) {
                coreV1Api.deleteNamespacedPod(podDto.getName(), podDto.getNamespace(), null, null, null, null, null, null);
            }
        } catch (Exception e) {
            // 由于k8s删除pod后期待返回对象和实际返回对象不一致，导致抛IllegalStateException异常，是k8s自身的一个bug，这里捕获后当正常处理
            if (e.getCause() instanceof IllegalStateException) {
                IllegalStateException ise = (IllegalStateException) e.getCause();
                if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
                    log.info("Catching exception because of issue https://github.com/kubernetes/kubernetes/issues/65121");
                } else {
                    return false;
                }
            }
        }
        return true;
    }


    // 包装端口映射数据
    private String getPortMapper(V1ContainerPort v1ContainerPort) {
        Integer containerPort = v1ContainerPort.getContainerPort();
        Integer hostPort = v1ContainerPort.getHostPort();
        String hostPortStr = hostPort == null ? "--" : hostPort.toString();
        String containerPortStr = containerPort == null ? "--" : containerPort.toString();
        String portMapper = hostPortStr + ":" + containerPortStr;
        return portMapper;
    }

}
