package com.wff.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wff.project.dto.DishDto;
import com.wff.project.entity.Dish;

public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据，操作两张表，dish和dish_flavor
    void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    // 更新菜品表和口味表基本信息
    void updateWithFlavor(DishDto dishDto);
}
