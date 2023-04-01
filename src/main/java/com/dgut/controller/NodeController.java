package com.dgut.controller;

import com.dgut.model.entity.Node;
import com.dgut.model.entity.R;
import com.dgut.service.NodeService;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dgut")
@CrossOrigin
public class NodeController {

    @Autowired
    private NodeService nodeService;


    @GetMapping("node")
    public R<List<Node>> getNodesByPage() throws ApiException {
        List<Node> nodeList = nodeService.getNodes();
        System.out.println("nodeList = " + nodeList);

        return R.success(nodeList,"获取节点成功");
    }

}
