package com.dgut.model.dto;

import lombok.Data;

@Data
public class PodLogDto {
    private String name;
    private String namespace;
    private String container;
}
