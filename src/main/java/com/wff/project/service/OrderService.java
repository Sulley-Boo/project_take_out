package com.wff.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wff.project.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
