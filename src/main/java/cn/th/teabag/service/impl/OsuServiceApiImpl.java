package cn.th.teabag.service.impl;

import cn.th.teabag.entity.BeatMapPP;
import cn.th.teabag.entity.Mapbg;
import cn.th.teabag.entity.SuperPrRecent;
import cn.th.teabag.entity.User;
import cn.th.teabag.exception.ConvertJsonErrorException;
import cn.th.teabag.exception.UserAlreadyBindException;
import cn.th.teabag.exception.UserNotFoundException;
import cn.th.teabag.http.utils.HttpUtils;
import cn.th.teabag.mapper.MapbgMapper;
import cn.th.teabag.mapper.UserMapper;
import cn.th.teabag.service.OsuServiceApi;
import cn.th.teabag.utils.ConvertUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class OsuServiceApiImpl implements OsuServiceApi {

    @Resource
    UserMapper userMapper;

    @Resource
    MapbgMapper mapbgMapper;

    @Override
    public boolean bindUser(Long qq,String userName) throws UserAlreadyBindException, UserNotFoundException, IOException, URISyntaxException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getQq, qq));
        if(user!=null&&user.getUid()!=null){
            throw new UserAlreadyBindException("此qq号已绑定账号，有问题请联系X bol");
        }else{
            Long uid= getUserUidByUserName(userName);
            User user2 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, uid));
            if(user2!=null){
                throw new UserAlreadyBindException("用户"+userName+"已绑定账号，有问题请联系X bol");
            }
            return userMapper.insert(new User() {{
                this.setUid(uid);
                this.setQq(qq);
                this.setUserName(userName);
            }}) > 0;
        }
    }

    @Override
    public boolean unBind(Long qq) throws UserNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getQq, qq));
        if(user!=null){
            userMapper.deleteById(user.getId());
            return true;
        }else{
            throw new UserNotFoundException("未在数据库中找到"+qq+"绑定的用户");
        }
    }

    @Override
    public Long getUserUidByUserName(String userName) throws UserNotFoundException, IOException, URISyntaxException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
        if(user==null){
            return HttpUtils.getUidByUserName(userName);
        }else{
            return user.getUid();
        }
    }

    public Message printPrOrRecent(JsonNode prOrRecentJson, User user, Contact group) throws ConvertJsonErrorException, URISyntaxException {
        MessageChainBuilder messageChainBuilder=new MessageChainBuilder();
        if(prOrRecentJson.size()==0){
            messageChainBuilder.add("用户"+user.getUserName()+"没有最近游玩记录");
        }else {
            SuperPrRecent prRecent = ConvertUtils.convertJsonToRecnetOrPr(prOrRecentJson.get(0));
            BeatMapPP beatMapPP = ConvertUtils.convertJsonToBeatMapPP(HttpUtils.getPPJson(prRecent.getBeatMapInfo().getBeatMapId(), prRecent.getAcc(),prRecent.getModsId()));
            File coverFile=new File(ResourceUtils.CLASSPATH_URL_PREFIX+"beatmapsCover"+prRecent.getBeatMapSetsInfo().getId()+".jpg");
            if(!coverFile.exists()) {
                coverFile = HttpUtils.downloadCover(prRecent.getBeatMapSetsInfo().getCover(), prRecent.getBeatMapSetsInfo().getId());
            }
            if(coverFile!=null){
                Image uploadImage =group.uploadImage(ExternalResource.create(coverFile));
                messageChainBuilder.add(uploadImage);
            }
            StringBuilder result = new StringBuilder();
            result.append(prRecent.getBeatMapSetsInfo().getArtist()).append(" - ").append(prRecent.getBeatMapSetsInfo().getTitle()).append(" [").append(prRecent.getBeatMapInfo().getVersion()).append("] ").append(" ").append(prRecent.getMods()).append("\n");
            result.append("Star:").append(prRecent.getBeatMapInfo().getDifficultyStar()).append("☆").append(" BPM:").append(prRecent.getBeatMapInfo().getBpm()).append("\n");
            result.append("AR:").append(prRecent.getBeatMapInfo().getAr()).append(" OD:").append(prRecent.getBeatMapInfo().getOd()).append(" CS:").append(prRecent.getBeatMapInfo().getCs()).append(" HP:").append(prRecent.getBeatMapInfo().getHp()).append("\n");
            result.append("300:").append(prRecent.getNum_300()).append("\n");
            result.append("100:").append(prRecent.getNum_100()).append("\n");
            result.append("50:").append(prRecent.getNum_50()).append("\n");
            result.append("Miss:").append(prRecent.getNum_miss()).append("\n");
            result.append("Acc:").append(String.format("%.2f",prRecent.getAcc()*100)).append(" Rank: ").append(prRecent.getRank()).append("\n");
            result.append(prRecent.getMaxCombo()).append("/").append(prRecent.getBeatMapInfo().getCombo()).append(" PP:").append(prRecent.getPp()).append("\n");
            result.append("if fc PP: ").append(beatMapPP.getPP_IF_FC()).append("\n");
            result.append("95%:").append(beatMapPP.getPP_95()).append("\t97%:").append(beatMapPP.getPP_97()).append("\n");
            result.append("98%:").append(beatMapPP.getPP_98()).append("\t99%:").append(beatMapPP.getPP_99()).append("\n");
            result.append("SS:").append(beatMapPP.getPP_100()).append("\n");
            result.append("Played at: ").append(prRecent.getCreateAt()).append("\n");
            result.append("By ").append(prRecent.getUser().getUserName());
            messageChainBuilder.add(result.toString());
        }
        return messageChainBuilder.build();
    }

    @Override
    public Message pr(User user, Contact group) throws URISyntaxException, ConvertJsonErrorException {
        JsonNode prJson=HttpUtils.getPrJson(user.getUid());
        return printPrOrRecent(prJson,user,group);
    }

    @Override
    public Message recent(User user,Contact group) throws URISyntaxException, ConvertJsonErrorException {
        JsonNode recentJson=HttpUtils.getRecentJson(user.getUid());
        return printPrOrRecent(recentJson,user,group);
    }

    @Override
    public File getBg(Long bid) {
        Mapbg mapbg = mapbgMapper.selectOne(new LambdaQueryWrapper<Mapbg>().eq(Mapbg::getBeatmapsetsId, bid));
        return null;
    }
}
