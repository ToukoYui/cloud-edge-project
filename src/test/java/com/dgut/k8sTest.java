package com.dgut;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;

public class k8sTest {
    public static ApiClient apiClient;
    public static void initApiClient() throws IOException {
        String kubeConfigPath = "src\\main\\resources\\config";
        // 读取配置文件验证连接
        apiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
    }

    @Test
    public void getNodeList() throws IOException, ApiException {
        initApiClient();
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        V1NodeList nodeList =
                coreV1Api.listNode(null, null, null, null, null, null, null, null, null);
        System.out.println(nodeList);
    }
}
