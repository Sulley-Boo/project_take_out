package com.wff.project.dto;

import com.wff.project.entity.Orders;
import lombok.Data;

@Data
public class OrderDto extends Orders {

    // 订单用户名
    private String name;
}
