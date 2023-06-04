package com.dgut.utils;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import io.swagger.annotations.Api;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Author: ToukoYui
 * @Description: 项目启动时初始化连接k8s客户端
 **/
public class K8sClient {
    public static ApiClient apiClient;

    public static void initApiClient() throws IOException {
//        String kubeConfigPath = "src\\main\\resources\\config";
        String kubeConfigPath = "E:\\k8s-project\\src\\main\\resources\\config";
        // 读取配置文件验证连接
        apiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();

//        token方式验证连接
/*        String master = "https://192.168.10.101:6443";
          apiClient = new ClientBuilder()
                //设置 k8s 服务所在 ip地址
                .setBasePath(master)
                //是否开启 ssl 验证
                .setVerifyingSsl(false)
                //插入访问 连接用的 Token
                .setAuthentication(new AccessTokenAuthentication(oauthToken))
                .build();*/
    }

    public static ApiClient getApiClient(){
        return apiClient;
    }


    static String[] baseLabel = new String[]{
            "beta.kubernetes.io/arch",
            "beta.kubernetes.io/os",
            "kubernetes.io/arch",
            "kubernetes.io/hostname",
            "kubernetes.io/os",
            "node-role.kubernetes.io/master"
    };

    public static boolean labelExist(String label){
        for (String item:
                baseLabel ) {
            if (item.equals(label)){
                return true;
            }
        }
        return false;
    }


    static String[] baseNamespace = new String[]{
            "kube-flannel",
            "kube-node-lease",
            "kube-public",
            "kube-system"
    };
    public static boolean excludeNs(String label){
        for (String item:
                baseNamespace ) {
            if (item.equals(label)){
                return true;
            }
        }
        return false;
    }

}
