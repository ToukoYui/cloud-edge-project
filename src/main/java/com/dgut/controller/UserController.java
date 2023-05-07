package com.dgut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dgut.model.entity.R;
import com.dgut.model.entity.User;
import com.dgut.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/dgut")
@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * @Author: ToukoYui
     * @Date: 2023/3/20 1:00
     * @Description: 用户登录
     **/
    @PostMapping("user/login")
    public R<User> login(HttpServletRequest request, @RequestBody User user){
        return R.success(user,"登录成功");
        //1.对用户输入的密码加密处理用来和数据库比对
//        String password = user.getPassword();
////        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        //2.根据用户名（唯一）查询数据库
//        LambdaQueryWrapper<User> lqw = new  LambdaQueryWrapper<>();
//        lqw.eq(User::getUsername,user.getUsername());
//        User result = userService.getOne(lqw);
//
//        //3.1 根据查不到该用户名
//        if (result == null){
//            return R.error(500,"该用户不存在！！！");
//        }
//        //3.2 查到该用户，但是密码不一致
//        if (!password.equals(user.getPassword())){
//            return R.error(500,"密码错误！！！");
//        }

        //4 查看员工状态是否被禁用
//        if(1 != emp.getStatus()){
//            return R.error("该账号也被禁用！！！");
//        }

        //成功,将用户id存入session中
//        request.getSession().setAttribute("employee",emp.getId());
//        return R.success(user,"登录成功");
    }


    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.removeAttribute("employee");
        return R.success("退出成功！！！");
    }
}
