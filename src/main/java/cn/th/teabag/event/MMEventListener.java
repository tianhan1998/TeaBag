package cn.th.teabag.event;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class MMEventListener extends SimpleListenerHost {
    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        System.err.println(exception.getMessage());
    }

    @EventHandler
    public void onMessage(@NotNull FriendMessageEvent messageEvent)throws Exception {
        if (messageEvent.getSender().getId()==834276213L){
            messageEvent.getBot().getGroup(1092212971L).sendMessage(messageEvent.getMessage());
        }
    }
}
