package com.logan.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/30
 * @description TODO
 */
@Data
public class User implements Serializable {
    private Long id;
    private String name;
    private Integer age;
}
