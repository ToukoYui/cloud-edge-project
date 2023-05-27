package com.dgut.controller;


import com.dgut.model.entity.R;
import com.dgut.utils.K8sClient;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NodeList;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@RestController
@RequestMapping("/dgut")
@CrossOrigin
public class CommonController {

    /**
     * @Author: ToukoYui
     * @Date: 2023/4/1 20:16
     * @Description: 获取namespace
     **/
    @GetMapping("/namespace")
    public R<List<String>> getNamespace() throws ApiException {
        Object o = new Object();
        Class<?> aClass = o.getClass();
        ApiClient apiClient = K8sClient.getApiClient();
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        List<String> result = new ArrayList<>();
        V1NamespaceList namespaceList = coreV1Api.listNamespace(null, null, null, null, null, null, null, null, null);
        for (V1Namespace v1Namespace: namespaceList.getItems() ) {
            result.add(v1Namespace.getMetadata().getName());
        }
        return R.success(result,"获取namespace成功");
    }
}
