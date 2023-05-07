package com.dgut.model.entity;

import io.kubernetes.client.custom.Quantity;
import lombok.Data;
import java.util.Map;

@Data
public class NodeResourceStatus {
    private Map<String, Quantity> capacity;
    private Map<String, Quantity> allocatable;

    public NodeResourceStatus(Map<String, Quantity> capacity, Map<String, Quantity> allocatable) {
        this.capacity = capacity;
        this.allocatable = allocatable;
    }
}
