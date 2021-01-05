package cn.th.teabag.event;

import cn.th.teabag.entity.User;
import cn.th.teabag.exception.ArgsErrorException;
import cn.th.teabag.exception.PermissionErrorException;
import cn.th.teabag.mapper.UserMapper;
import cn.th.teabag.service.impl.OsuServiceApiImpl;
import cn.th.teabag.service.impl.UserServiceApiImpl;
import cn.th.teabag.utils.ConvertUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Random;

@Component
public class MyGroupEventListener extends SimpleListenerHost {

    @Resource
    UserMapper userMapper;

    @Resource
    UserServiceApiImpl userServiceApi;

    @Autowired
    OsuServiceApiImpl osuServiceApi;

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        System.err.println("监听器出现了错误，错误message——"+exception.getMessage());
    }
    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent messageEvent)throws Exception{
        MessageChain messageChain= messageEvent.getMessage();
        SingleMessage singleMessage = messageChain.get(1);
        String plainText=singleMessage.contentToString();
        Group group= messageEvent.getSubject();
        //判断中英文！
        if(plainText.startsWith("!")||plainText.startsWith("！")){
            plainText=plainText.replace("！","!");
            Long senderId=messageEvent.getSender().getId();
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getQq, senderId));
            if((user==null||user.getUid()==null)&&!plainText.startsWith("!bind")&&!plainText.startsWith("!adminBind")){
                messageEvent.getSubject().sendMessage("您还没有绑定账号，输入!bind xxx（您的用户名）来进行账号绑定");
            }else{

                //处理命令与参数
                String order;String args;
                if(plainText.indexOf(' ')!=-1) {
                    order = plainText.substring(0, plainText.indexOf(' '));
                    args = plainText.substring(plainText.indexOf(" ") + 1);
                }else{
                    order=plainText;
                    args=null;
                }
                order=order.toLowerCase();
                try {
                    if (order.startsWith("!bind")) {
                        if (osuServiceApi.bindUser(senderId, args)) {
                            messageEvent.getSubject().sendMessage("绑定此账号到" + args + "成功!");
                        }
                    }
                    else if(order.startsWith("!adminunbind")){
                        if(senderId!=892265525L){
                            throw new PermissionErrorException("您没有权限哦~");
                        }else {
                            if(args==null){
                                throw new ArgsErrorException("参数有误~解绑指令例如:!adminUnBind <qq>");
                            }
                            if(osuServiceApi.unBind(Long.valueOf(args))){
                                messageEvent.getSubject().sendMessage("解绑成功");
                            }
                        }
                    }
                    else if(order.startsWith("!adminbind")){
                        if(senderId!=892265525L){
                            throw new PermissionErrorException("您没有权限哦~");
                        }else{
                            String[] twoArgs= ConvertUtils.splitTwoArgs(args);
                            if(osuServiceApi.bindUser(Long.valueOf(twoArgs[0]),twoArgs[1])){
                                messageEvent.getSubject().sendMessage("绑定"+twoArgs[0]+"账号到"+twoArgs[1]+"成功");
                            }
                        }
                    }else if(order.startsWith("!pp")){
                        if(args==null){
                            throw new ArgsErrorException("参数有误，需要bid 例:!pp xxxxxx");
                        }
                        String[] ppArgs=ConvertUtils.splitPPArgs(args);
                        messageEvent.getSubject().sendMessage(osuServiceApi.ppMapInfo(Long.valueOf(ppArgs[0]),ppArgs[1],group));
                    }
                    else if(order.startsWith("!pr")){
                        if(args==null) {
                            messageEvent.getSubject().sendMessage(osuServiceApi.pr(user,group));
                        }else{
                            messageEvent.getSubject().sendMessage(osuServiceApi.pr(userServiceApi.getUserByUserName(args),group));
                        }
                    }
                    else if(order.startsWith("!recent")){
                        if(args==null) {
                            messageEvent.getSubject().sendMessage(osuServiceApi.recent(user,group));
                        }else{
                            messageEvent.getSubject().sendMessage(osuServiceApi.recent(userServiceApi.getUserByUserName(args),group));
                        }
                    }
                }catch(Exception e){
                    messageEvent.getSubject().sendMessage(e.getMessage());
                }
            }
        }

        else if(messageChain.size()>=3) {
            SingleMessage atSingle = messageChain.get(1);
            SingleMessage message = messageChain.get(2);
            String trim = message.toString().trim();
            if (atSingle instanceof At && atSingle.contentToString().equals("@2654064901") && trim.contains("谁是现充")) {
                messageEvent.getSubject().sendMessage("xxx是现充，nike是现充，funny是现充，eus也是现充，透马呜呜呜。");
            }
            else if (atSingle instanceof At && atSingle.contentToString().equals("@2654064901") && trim.contains("谁是权限狗")) {
                messageEvent.getSubject().sendMessage("xxx是权限狗，权限狗4K+，老铁们我说的对吗？");
                Image uploadImage = messageEvent.getSubject().uploadImage(ExternalResource.create(new File("F:\\茶包\\pic\\茄子摇头.gif")));
                messageEvent.getSubject().sendMessage(uploadImage);
                messageEvent.getSubject().sendMessage("权限狗破防了吗？");
            }else if(atSingle instanceof  At && atSingle.contentToString().equals("@2654064901")){
                messageEvent.getSubject().sendMessage(messageChain.get(2));
            }
        }else{
            int i = new Random().nextInt(100);
            if(i==1){
                group.sendMessage(messageChain);
            }
        }
    }
}
