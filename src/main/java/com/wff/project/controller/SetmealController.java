package com.wff.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wff.project.common.CustomException;
import com.wff.project.common.R;
import com.wff.project.dto.SetmealDto;
import com.wff.project.entity.Category;
import com.wff.project.entity.Setmeal;
import com.wff.project.entity.SetmealDish;
import com.wff.project.service.CategoryService;
import com.wff.project.service.SetmealDishService;
import com.wff.project.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    @ApiOperation(value = "新增套餐接口")
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功!");
    }

    /**
     * 套餐信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", readOnly = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", readOnly = true),
            @ApiImplicitParam(name = "name", value = "套餐名称", readOnly = false)
    })
    public R<Page> page(int page, int pageSize, String name) {
        // 分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        // 添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId(); // 分类id
            Category category = categoryService.getById(categoryId); // 根据分类id查询分类对象

            if (category != null) {
                String cagegoryName = category.getName(); // 分类名称
                setmealDto.setCategoryName(cagegoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    @ApiOperation(value = "删除套餐接口")
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids：{}", ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功!");
    }

    /**
     * 根据条件查询套餐数据
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    @ApiOperation(value = "套餐条件查询接口")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 套餐停售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> haltSale(@RequestParam List<Long> ids) {
        log.info("套餐id信息：{}", ids);
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            if (setmeal.getStatus() == 0) {
                throw new CustomException("套餐已经在停售中...");
            }
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        }
        return R.success("套餐停售成功!");
    }

    /**
     * 套餐启售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> startSale(@RequestParam List<Long> ids) {
        log.info("套餐id信息：{}", ids);
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            if (setmeal.getStatus() == 1) {
                throw new CustomException("套餐已经在启售中...");
            }
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        }
        return R.success("套餐启售成功!");
    }

    /**
     * 根据id查询套餐信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Setmeal> get(@PathVariable Long id) {
        Setmeal setmeal = setmealService.getByIdwithDish(id);
        return R.success(setmeal);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto.toString());
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功!");
    }
}
