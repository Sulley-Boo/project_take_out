package com.wff.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 插入时填充字段

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 插入和更新是填充字段

    @TableField(fill = FieldFill.INSERT)
    private Long createUser; // 插入时填充字段

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser; // 插入和更新是填充字段

}
