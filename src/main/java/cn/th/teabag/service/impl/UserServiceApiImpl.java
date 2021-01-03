package cn.th.teabag.service.impl;

import cn.th.teabag.entity.User;
import cn.th.teabag.exception.UserNotFoundException;
import cn.th.teabag.mapper.UserMapper;
import cn.th.teabag.service.UserServiceApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceApiImpl implements UserServiceApi {

    @Resource
    UserMapper userMapper;

    @Override
    public User getUserByUserName(String userName) throws UserNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
        if(user!=null) {
            return user;
        }else{
            throw new UserNotFoundException("玩家"+userName+"未绑定");
        }
    }
}
