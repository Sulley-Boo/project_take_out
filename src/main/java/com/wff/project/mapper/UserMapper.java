package com.wff.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wff.project.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
