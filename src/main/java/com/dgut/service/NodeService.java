package com.dgut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.model.entity.Node;
import com.dgut.model.entity.NodeResourceStatus;
import io.kubernetes.client.openapi.ApiException;

import java.util.List;
import java.util.Map;


public interface NodeService extends IService<Node> {

    List<Node> getNodes() throws ApiException;
    Map<String, NodeResourceStatus> getNodeResourceStatus() throws ApiException;
}
