package com.dgut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.mapper.NodeMapper;
import com.dgut.model.entity.Node;
import com.dgut.service.NodeService;
import com.dgut.utils.K8sClient;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeAddress;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NodeServiceImpl extends ServiceImpl <NodeMapper, Node> implements NodeService  {
    @Override
    public List<Node> getNodes() throws ApiException {
        // 调用k8s接口获取node
        ApiClient apiClient = K8sClient.getApiClient();
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1NodeList nodeList =
                coreV1Api.listNode(null, null, null, null, null, null, null, null, null);

        // 包装node属性
        List<Node> resultList = new ArrayList<>();
        for (V1Node item :
                nodeList.getItems()) {
            Node node = new Node();
            node.setName(item.getMetadata().getName());
            node.setIp(item.getStatus().getAddresses().get(0).getAddress());
            node.setRole(item.getStatus().getAddresses().get(1).getAddress());

            // 包装标签，
            List<String> labelList = new ArrayList<>();
            Map<String, String> labels = item.getMetadata().getLabels();
            // 排除基础标签
            labels.forEach((key,value)->{
                if (!K8sClient.labelExist(key)){
                    labelList.add(value);
                }
            });
            node.setLabels(labelList.toArray(new String[labelList.size()]));
            resultList.add(node);
        }
        return resultList;
    }

}
