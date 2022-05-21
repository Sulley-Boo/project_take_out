package com.wff.project.dto;

import com.wff.project.entity.Setmeal;
import com.wff.project.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
