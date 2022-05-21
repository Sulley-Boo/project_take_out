package com.wff.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wff.project.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long ids);
}
