package com.dgut.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_node")
public class Node {
    @TableId(type = IdType.ASSIGN_ID)
    private long id;
    private String name;
    private String ip;
    private String role;
    private String[] labels;
}
