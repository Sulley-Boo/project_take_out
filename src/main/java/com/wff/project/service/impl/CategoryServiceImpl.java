package com.wff.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wff.project.common.CustomException;
import com.wff.project.entity.Category;
import com.wff.project.entity.Dish;
import com.wff.project.entity.Setmeal;
import com.wff.project.mapper.CategoryMapper;
import com.wff.project.service.CategoryService;
import com.wff.project.service.DishService;
import com.wff.project.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，在删除之前需要先进行判断
     *
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        // 查询当前分类是否关联了菜品，如果关联，抛出一个业务异常
        if (count1 > 0) {
            // 抛出异常
            throw new CustomException("当前分类关联了菜品，不能删除!");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);

        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        // 查询当前分类是否关联了套餐，如果关联，抛出一个业务异常
        if (count2 > 0) {
            // 抛出异常
            throw new CustomException("当前分类关联了套餐，不能删除!");
        }
        // 正常删除分类
        super.removeById(ids);
    }
}
