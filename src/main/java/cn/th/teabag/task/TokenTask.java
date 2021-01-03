package cn.th.teabag.task;

import cn.th.teabag.http.utils.HttpUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URISyntaxException;

@Configuration
public class TokenTask {

    @Scheduled(cron = "0 0 0,12 * * ? *")
    private void getToken() throws URISyntaxException {
        HttpUtils.getToken();
    }


}
