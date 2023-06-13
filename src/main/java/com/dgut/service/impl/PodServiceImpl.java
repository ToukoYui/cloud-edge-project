package com.dgut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.mapper.PodMapper;
import com.dgut.model.dto.PodDto;
import com.dgut.model.entity.NodeResourceStatus;
import com.dgut.model.entity.Pod;
import com.dgut.service.NodeService;
import com.dgut.service.PodService;
import com.dgut.utils.K8sClient;
import com.fasterxml.jackson.databind.util.BeanUtil;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PodServiceImpl extends ServiceImpl<PodMapper, Pod> implements PodService {
    @Autowired
    private NodeService nodeService;

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
            if (status.getStartTime() !=null){
                pod.setCreatedTime(status.getStartTime().toString("yyyy-MM-dd HH:mm:ss"));
            }
            resultList.add(pod);
        }

        return resultList;
    }

    @Override
    public void createPod(V1Pod pod) throws ApiException {
        CoreV1Api coreV1Api = new CoreV1Api(K8sClient.getApiClient());
        // 节点调度
        // 获取节点状态信息
        Map<String, NodeResourceStatus> nodeResourceStatusMap = nodeService.getNodeResourceStatus();
        // 得到每个节点的状态权重
        Map<String,Double> scoreMap = getNodeStatusDetail(nodeResourceStatusMap);
        // 排序，拿到权重最小的节点名
        scoreMap.entrySet().stream().sorted(Map.Entry.comparingByValue());
        // 将最小的节点封装成一个list
        Double temp = Double.valueOf(0);
        List<String> list = new ArrayList<>();
        boolean flag = false;
        for (Map.Entry<String, Double> entry :
                scoreMap.entrySet()) {
            if (!flag){
                list.add(entry.getKey());
                temp = entry.getValue();
                flag = true;
            }else{
                // 如果权重相等需要随机选择节点
                if (entry.getValue().equals(temp)){
                    list.add(entry.getKey());
                }
            }
        }
        // 随机获取list中的节点,设置节点名
        int size = list.size();
        String resultNodeName = "";
        if (size==1){
            resultNodeName = list.get(0);
            pod.getSpec().setNodeName(resultNodeName);
        }else if (size >1){
            Random random = new Random(System.currentTimeMillis());
            int index = random.nextInt(size);
            resultNodeName = list.get(index);
            pod.getSpec().setNodeName(resultNodeName);
        }
        log.info("pod调度到节点========>"+resultNodeName);
        String ns = pod.getMetadata().getNamespace();
        V1Pod result =
                coreV1Api.createNamespacedPod(ns, pod, null, null, null);
        // todo 如果出现创建失败情况需要进行后续处理
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

    // 获取封装节点状态信息
    public Map<String,Double> getNodeStatusDetail(Map<String, NodeResourceStatus> nodeResourceStatusMap){
        Map<String,Double> scoreFromNodeMap = new HashMap<>();
        for (Map.Entry<String, NodeResourceStatus> entry:
                nodeResourceStatusMap.entrySet()) {
            String nodeName = entry.getKey();
            NodeResourceStatus status = entry.getValue();
            Map<String, Quantity> allocatable = status.getAllocatable();
            Map<String, Quantity> capacity = status.getCapacity();
            // 可分配的cpu数量,内存,pod数
            BigDecimal allocatableCpu = allocatable.get("cpu").getNumber();
            BigDecimal allocatableMemory = allocatable.get("memory").getNumber();
            BigDecimal allocatablePods = allocatable.get("pods").getNumber();
            // 总的cpu数量,内存,pod数
            BigDecimal capacityCpu = capacity.get("cpu").getNumber();
            BigDecimal capacityMemory = capacity.get("memory").getNumber();
            BigDecimal capacityPods = capacity.get("pods").getNumber();
            //使用占比
            Double cpuPercent = allocatableCpu.divide(capacityCpu, 4, RoundingMode.DOWN).doubleValue();
            Double memoryPercent = allocatableMemory.divide(capacityMemory, 4, RoundingMode.DOWN).doubleValue();
            Double podsPercent = allocatablePods.divide(capacityPods, 4, RoundingMode.DOWN).doubleValue();
            // 得到权重
            Double score = memoryPercent * 0.6 + cpuPercent * 0.3 + podsPercent * 0.1;
            scoreFromNodeMap.put(nodeName,score);
        }
            return scoreFromNodeMap;
    }
}
