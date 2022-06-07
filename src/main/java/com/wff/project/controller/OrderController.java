package com.wff.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wff.project.common.BaseContext;
import com.wff.project.common.R;
import com.wff.project.dto.OrderDto;
import com.wff.project.entity.Orders;
import com.wff.project.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功!");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, LocalDateTime beginTime, LocalDateTime endTime) {
        // 分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> dtoPage = new Page<>();


        // 条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null, Orders::getNumber, number);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        // 执行分页查询
        orderService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     * 目前只支持从待派送到已派送，从已派送到已完成
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        log.info("订单信息：{}", orders);

        orderService.updateById(orders);
        return R.success("状态已更新!");
    }

    /**
     * 移动端最新订单查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }
}
