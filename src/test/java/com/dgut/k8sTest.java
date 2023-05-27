package com.dgut;

import com.dgut.model.entity.NodeResourceStatus;
import com.dgut.utils.K8sClient;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.JSON;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class k8sTest {
    public static ApiClient apiClient;
    public static CoreV1Api coreV1Api;

    public static void initApiClient() throws IOException {
        String kubeConfigPath = "src\\main\\resources\\config";
        // 读取配置文件验证连接
        apiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        coreV1Api = new CoreV1Api(apiClient);
    }

    @Test
    public void getNamespace() throws IOException, ApiException {
        initApiClient();
        V1NamespaceList list = coreV1Api.listNamespace(null, null, null, null, null, null, null, null, null);
        System.out.println("list = " + list);
    }

    @Test
    public void getNodeList() throws IOException, ApiException {
        initApiClient();
        V1NodeList nodeList =
                coreV1Api.listNode(null, null, null, null, null, null, null, null, null);
        System.out.println(nodeList);
    }

    @Test
    public void getPodByNs() throws ApiException, IOException {
        initApiClient();
//        coreV1Api.listNamespacedPod("default")
//        V1PodList podList = coreV1Api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        V1PodList podList = coreV1Api.listNamespacedPod("dev-rin", null, null, null, null, null, null, null, null, null);
        System.out.println(podList);
    }

    @Test
    public void createPod() throws IOException, ApiException {
        initApiClient();

        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setName("pod-imagepullpolicy");
        v1ObjectMeta.setNamespace("dev-rin");

        V1PodSpec v1PodSpec = new V1PodSpec();
        // 创建容器集合
        V1Container v1Container1 = new V1Container();
        v1Container1.setName("nginx");
        v1Container1.setImage("nginx:1.17.1");
        v1Container1.setImagePullPolicy("Always");

        // 设置容器端口映射
        V1ContainerPort containerPort = new V1ContainerPort();
        containerPort.setHostPort(80);
        containerPort.setContainerPort(80);
        List<V1ContainerPort> portList = new ArrayList<>();
        portList.add(containerPort);
        v1Container1.setPorts(portList);

        V1Container v1Container2 = new V1Container();
        v1Container2.setName("busbox");
        v1Container2.setImage("busybox:1.30");

        List<V1Container> containerList = new ArrayList<>();
        containerList.add(v1Container1);
        containerList.add(v1Container2);
        v1PodSpec.setContainers(containerList);

        V1Pod v1Pod = new V1Pod();
        v1Pod.setMetadata(v1ObjectMeta);
        v1Pod.setSpec(v1PodSpec);
        V1Pod pod = coreV1Api.createNamespacedPod("dev-rin", v1Pod, null, null, null);
        System.out.println(pod);
    }


    @Test
    public void deletePod() throws IOException, ApiException {
        initApiClient();
        try {
            coreV1Api.deleteNamespacedPod("pod-imagepullpolicy", "dev-rin", null, null, null, null, null, null);
        } catch (Exception e) {
            if (e.getCause() instanceof IllegalStateException) {
                IllegalStateException ise = (IllegalStateException) e.getCause();
                if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
                    log.info("Catching exception because of issue https://github.com/kubernetes/kubernetes/issues/65121");
                } else {
                    throw e;
                }
            }
        }
    }

    @Test
    public void getPodLogs() throws IOException, ApiException {
        initApiClient();
        //容器名为null则查该命名空间下的Pod的所有日志
        String s = coreV1Api.readNamespacedPodLog("cloud-iptables-manager-8q8fm", "dev-rin", null, null, null, null, null, null, null, null, null);
        System.out.println("s = " + s);
    }

    @Test
    public void getPersistVolume() throws IOException, ApiException {
        initApiClient();
        V1PersistentVolumeList volumeList = coreV1Api.listPersistentVolume(null, false, null, null, null, null, null, null, null);
        System.out.println(volumeList);
        V1Patch patch = new V1Patch("master");
        V1PersistentVolume v1PersistentVolume = coreV1Api.patchPersistentVolume("pc-deployment-6696798b78-74hcp", patch, null, null, null, null);
        System.out.println("v1PersistentVolume = " + v1PersistentVolume);
    }

    @Test
    public void replacePodByVolume() throws IOException, ApiException {
        initApiClient();
        V1Pod v1Pod = coreV1Api.readNamespacedPod("pc-deployment-6696798b78-74hcp", "dev-rin", null, null, null);
        JSON json = new JSON();
        String serialize = json.serialize(v1Pod);
        System.out.println(serialize);
    }

    @Test
    public void getNodeDescribe() throws IOException, ApiException {


    }
    // -Djdk.tls.client.protocols=TLSv1.2
    @Test
    public void getNodeResourceStatus() throws ApiException, IOException {
        initApiClient();
        V1NodeList nodeList =
                coreV1Api.listNode(null, null, null, null, null, null, null, null, null);
        Map<String,NodeResourceStatus> map = new HashMap<>();
        for (V1Node node :
                nodeList.getItems()) {
            String nodeName = node.getMetadata().getName();
            V1NodeStatus status = node.getStatus();
            map.put(nodeName,new NodeResourceStatus(status.getCapacity(),status.getAllocatable()));
        }
        System.out.println(map);
    }

    @Test
    public void get(){

    }
}
