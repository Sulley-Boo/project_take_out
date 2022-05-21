package com.wff.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wff.project.entity.Employee;
import com.wff.project.mapper.EmploeeMapper;
import com.wff.project.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmploeeMapper, Employee> implements EmployeeService {

}
