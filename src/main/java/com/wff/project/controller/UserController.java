package com.wff.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wff.project.common.R;
import com.wff.project.entity.User;
import com.wff.project.service.UserService;
import com.wff.project.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码
     * 由于无法获取阿里云短信服务，发送验证码功能省略，此处只做手机号登录校验
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();

        if (!StringUtils.isEmpty(phone)) {
            // 生成四位验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            log.info("code={}", code);
            // 调用阿里云提供的短信服务API发送短信
            // 省略

            // 将生成的验证码保存到Session
            session.setAttribute(phone, code);
            return R.success("手机验证码短信发送成功!");
        }

        return R.error("短信发送失败!");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        // 获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        // 从session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        // 验证码比对
        if (codeInSession != null && codeInSession.equals(code)) {
            // 如果比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            // 判断当前手机号的用户是否为新用户，是新用户就自动完成注册
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("登录失败!");
    }
}
