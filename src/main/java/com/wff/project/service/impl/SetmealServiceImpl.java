package com.wff.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wff.project.common.CustomException;
import com.wff.project.dto.SetmealDto;
import com.wff.project.entity.Setmeal;
import com.wff.project.entity.SetmealDish;
import com.wff.project.mapper.SetmealMapper;
import com.wff.project.service.SetmealDishService;
import com.wff.project.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);

        // 如果不能删除，抛出一个业务异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除!");
        }

        // 如果可以删除，先删除套餐中的数据--setmeal
        this.removeByIds(ids);

        // 删除关系表中的数据--setmeal_dish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 根据id查询套餐信息以及关联的菜品信息
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealDto getByIdwithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    /**
     * 修改套餐信息以及相关菜品信息
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);
    }
}
