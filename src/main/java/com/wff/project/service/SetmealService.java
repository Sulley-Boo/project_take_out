package com.wff.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wff.project.dto.SetmealDto;
import com.wff.project.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);
    void removeWithDish(List<Long> ids);
    SetmealDto getByIdwithDish(Long id);
    void updateWithDish(SetmealDto setmealDto);
}
