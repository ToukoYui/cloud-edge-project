package com.dgut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.model.entity.Node;
import io.kubernetes.client.openapi.ApiException;

import java.util.List;


public interface NodeService extends IService<Node> {

    List<Node> getNodes() throws ApiException;
}
