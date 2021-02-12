package cn.th.teabag;

import cn.th.teabag.context.Path;
import cn.th.teabag.event.MMEventListener;
import cn.th.teabag.event.MyGroupEventListener;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
@MapperScan("cn.th.teabag.mapper")
//@EnableScheduling
public class TeabagApplication {

    private static MyGroupEventListener myGroupEventListener;
    private static MMEventListener mmEventListener;

    @Autowired
    public TeabagApplication(MyGroupEventListener myGroupEventListener, MMEventListener mmEventListener){
        TeabagApplication.myGroupEventListener=myGroupEventListener;
        TeabagApplication.mmEventListener=mmEventListener;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        SpringApplication.run(TeabagApplication.class, args);
        Bot bot= BotFactory.INSTANCE.newBot(2654064901L,"xxxxxxxxx",new BotConfiguration(){{
            setProtocol(MiraiProtocol.ANDROID_PAD);
            fileBasedDeviceInfo(Path.DEVICE_JSON_PATH);
        }});
        HttpUtils.getToken();
//        Bot bot= BotFactoryJvm.newBot(1535266756L, "Abcd1234", new BotConfiguration() {{
//            setProtocol(MiraiProtocol.ANDROID_PHONE);
////            fileBasedDeviceInfo();
//        }});
//        Bot bot= BotFactory.INSTANCE.newBot(2591373680L, "dhj20001206", new BotConfiguration() {{
//            setProtocol(MiraiProtocol.ANDROID_PAD);
//            fileBasedDeviceInfo();
//        }});
        bot.getEventChannel().registerListenerHost(myGroupEventListener);
        bot.getEventChannel().registerListenerHost(mmEventListener);
        bot.login();
        bot.join();
    }

}
