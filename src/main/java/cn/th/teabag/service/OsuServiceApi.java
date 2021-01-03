package cn.th.teabag.service;

import cn.th.teabag.entity.User;
import cn.th.teabag.exception.ConvertJsonErrorException;
import cn.th.teabag.exception.UserAlreadyBindException;
import cn.th.teabag.exception.UserNotFoundException;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public interface OsuServiceApi {

    boolean bindUser(Long qq,String userName) throws UserAlreadyBindException, UserNotFoundException, IOException, URISyntaxException;

    Long getUserUidByUserName(String userName) throws UserNotFoundException, IOException, URISyntaxException;

    Message pr(User user, Contact group) throws URISyntaxException, ConvertJsonErrorException;

    Message recent(User user, Contact group) throws URISyntaxException, ConvertJsonErrorException;

    File getBg(Long bid);
}
