package cn.th.teabag.service;


import cn.th.teabag.entity.User;
import cn.th.teabag.exception.UserNotFoundException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface UserServiceApi {

    User getUserByUserName(String userName) throws UserNotFoundException, IOException, URISyntaxException;

}
