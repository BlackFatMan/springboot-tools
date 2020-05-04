package com.blackfatman.websocket.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ClassName: WebSocketFactory
 * @Author : Black_FatMan
 * @Description : @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * @Date : Create in 8:46 2020/4/13
 * @Modified :
 */
@Component
@ServerEndpoint(value="/websocket/{id}")
@Slf4j
public class WebSocketFactory {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocketFactory> webSocketSet =
            new CopyOnWriteArraySet<WebSocketFactory>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private String uid;

    /**
     * 连接建立成功调用的方法 * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam("id") String id, Session session) {
        this.session=session;
        this.uid=id;
        log.info("用户"+id+"连接到websocket服务器");

        //加入set中
        webSocketSet.add(this);
        //在线数加1
        addOnlineCount();
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        webSocketSet.remove(this);
        //在线数减1
        subOnlineCount();
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);
        String[] msgs = message.split(":");
        for (WebSocketFactory item : webSocketSet) {
            String uid =  item.uid;
            //根据前端传过来的用户编号 和存储的用户编号进行对比,然后确定要发送给那个用户
            if (uid.equals(msgs[0])) {
                try {
                    item.sendMessage(uid+":"+msgs[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.info("发生错误");
        error.printStackTrace();
    }

    /**
     * 给在线的指定客户端用户发送消息
     * @author : Black_FatMan
     * @Description :
     * @date : 2020/4/15
     * @param message
     * @param uids
     * @return : void
     * @Modified :
     */
    public void sendMessage(String message, List<String> uids) {
        for (WebSocketFactory item : webSocketSet) {
            uids.forEach(receiveUid->{
                String uid =  item.uid;
                //如果接收人在线的话,给前端发送消息
                if (uid.equals(receiveUid)) {
                    try {
                        item.sendMessage(uid+":"+message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    }

    /**
     * 给所有客户端发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketFactory.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketFactory.onlineCount--;
    }
}
