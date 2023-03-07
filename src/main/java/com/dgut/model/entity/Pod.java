package com.dgut.model.entity;

import lombok.Data;
import org.joda.time.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Pod {
    private long id;
    private String name;
    private String status;
    private List<String> image;
    private String ip;
    private List<String> portMapper;
    private String createdTime;
}
