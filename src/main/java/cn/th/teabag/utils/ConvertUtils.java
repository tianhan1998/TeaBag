package cn.th.teabag.utils;

import cn.th.teabag.entity.*;
import cn.th.teabag.exception.ConvertArgsErrorException;
import cn.th.teabag.exception.ConvertJsonErrorException;
import cn.th.teabag.http.utils.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.platform.commons.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;

public class ConvertUtils {

    private static final ObjectMapper objectMapper=new ObjectMapper();

    public static String[] splitTwoArgs(String args) throws ConvertArgsErrorException {
        if(StringUtils.isNotBlank(args)) {
            String [] twoArgs=new String[2];
            twoArgs[0]=args.substring(0,args.indexOf(" "));
            twoArgs[1]=args.substring(args.indexOf(" ")+1);
            return twoArgs;
        }else{
            throw new ConvertArgsErrorException("参数转换失败");
        }
    }

    public static BeatMapPP convertJsonToBeatMapPP(JsonNode jsonNode) throws ConvertJsonErrorException {
        BeatMapPP beatMapPP=new BeatMapPP();
        try{
            JsonNode entry=jsonNode.get("ppForAcc").get("entry");
            for (JsonNode node : entry) {
                switch (node.get("key").asText()){
                    case "0.95":beatMapPP.setPP_95(node.get("value").asInt());break;
                    case "0.97":beatMapPP.setPP_97(node.get("value").asInt());break;
                    case "0.98":beatMapPP.setPP_98(node.get("value").asInt());break;
                    case "0.99":beatMapPP.setPP_99(node.get("value").asInt());break;
                    case "1.0":beatMapPP.setPP_100(node.get("value").asInt());break;
                    default:beatMapPP.setPP_IF_FC(node.get("value").asInt());break;
                }
            }
            return beatMapPP;
        }catch (Exception e){
            throw new ConvertJsonErrorException();
        }
    }

    public static SuperPrRecent convertJsonToRecnetOrPr(JsonNode jsonNode) throws ConvertJsonErrorException {
        SuperPrRecent superPrRecent=new SuperPrRecent();
        try {
            JsonNode mods=jsonNode.get("mods");
            if(mods.size()!=0){
                StringBuilder modString=new StringBuilder();
                for (JsonNode mode : mods) {
                    modString.append(mode.toString());
                }
                superPrRecent.setMods(modString.toString());
            }else{
                superPrRecent.setMods("NM");
            }
            superPrRecent.setAcc(jsonNode.get("accuracy").asDouble());
            superPrRecent.setMaxCombo(jsonNode.get("max_combo").asInt());
            superPrRecent.setSS(jsonNode.get("perfect").asBoolean());
            superPrRecent.setScore(jsonNode.get("score").asText());
            JsonNode stat = jsonNode.get("statistics");
            superPrRecent.setNum_50(stat.get("count_50").asInt());
            superPrRecent.setNum_100(stat.get("count_100").asInt());
            superPrRecent.setNum_300(stat.get("count_300").asInt());
            superPrRecent.setNum_miss(stat.get("count_miss").asInt());
            superPrRecent.setPp(jsonNode.get("pp").asInt());
            superPrRecent.setRank(jsonNode.get("rank").asText());
            superPrRecent.setCreateAt(DateUtils.convert(jsonNode.get("created_at").asText()));
            JsonNode beatMap = jsonNode.get("beatmap");
            superPrRecent.setBeatMapInfo(convertJsonToBeatMapInfo(beatMap));
            JsonNode beatMapSetsInfo = jsonNode.get("beatmapset");
            superPrRecent.setBeatMapSetsInfo(convertJsonToBeatMapSetsInfo(beatMapSetsInfo));
            JsonNode userJson = jsonNode.get("user");
            User user = new User();
            user.setUserName(userJson.get("username").asText());
            user.setUid(userJson.get("id").asLong());
            superPrRecent.setUser(user);
            return superPrRecent;
        }catch (Exception e){
            throw new ConvertJsonErrorException();
        }
    }

    public static BeatMapSetsInfo convertJsonToBeatMapSetsInfo(JsonNode jsonNode){
        BeatMapSetsInfo beatMapSetsInfo=new BeatMapSetsInfo();
        beatMapSetsInfo.setArtist(jsonNode.get("artist").asText());
        beatMapSetsInfo.setArtistUnicode(jsonNode.get("artist_unicode").asText());
        beatMapSetsInfo.setCover(jsonNode.get("covers").get("cover").asText());
        beatMapSetsInfo.setCreator(jsonNode.get("creator").asText());
        beatMapSetsInfo.setSource(jsonNode.get("source").asText());
        beatMapSetsInfo.setTitle(jsonNode.get("title").asText());
        beatMapSetsInfo.setTitleUnicode(jsonNode.get("title_unicode").asText());
        beatMapSetsInfo.setId(jsonNode.get("id").asLong());
        return beatMapSetsInfo;
    }

    public static BeatMapInfo convertJsonToBeatMapInfo(JsonNode jsonNode) throws URISyntaxException, IOException {
        BeatMapInfo beatMapInfo=new BeatMapInfo();
        beatMapInfo.setDifficultyStar(jsonNode.get("difficulty_rating").asDouble());
        beatMapInfo.setBeatMapId(jsonNode.get("id").asLong());
        beatMapInfo.setBeatMapSetsId(jsonNode.get("beatmapset_id").asLong());
        beatMapInfo.setAr(jsonNode.get("ar").asDouble());
        beatMapInfo.setBpm(jsonNode.get("bpm").asDouble());
        beatMapInfo.setCs(jsonNode.get("cs").asDouble());
        beatMapInfo.setHp(jsonNode.get("drain").asDouble());
        beatMapInfo.setOd(jsonNode.get("accuracy").asDouble());
        beatMapInfo.setVersion(jsonNode.get("version").asText());
        beatMapInfo.setStatus(jsonNode.get("ranked").asText());
        beatMapInfo.setCombo(HttpUtils.getBeatMapCombo(beatMapInfo.getBeatMapId()));
        return beatMapInfo;
    }

}
