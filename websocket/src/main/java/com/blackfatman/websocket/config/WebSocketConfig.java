/*
* All rights Reserved, Designed By Elite
* @version: V1.0
* @Copyright: 2019 Shannxi Elite Software Technology Co., Ltd. All Rights Reserved.
* 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
* 法律声明：若将其内容公开、传播或用于其他商业目的，公司将追究外泄人的相关法律责任
*/

package com.blackfatman.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 *  配置WebSocket
 *
 * 通过EnableWebSocketMessageBroker
 * 开启使用STOMP协议来传输基于代理(message broker)的消息
 * 此时浏览器支持使用@MessageMapping 就像支持@RequestMapping一样
 *
 * 这个配置类不仅配置了 WebSocket，还配置了基于代理的 STOMP消息
 */
@Configuration
//@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
//public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    //endPoint 注册协议节点,并映射指定的URl
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        //注册一个名字为"endpointChat" 的endpoint,并指定 SockJS协议。   点对点-用
        registry.addEndpoint("/websocket").withSockJS();
    }
/*
    //配置消息代理(message broker)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 订阅Broker名称：
            // topic 代表发布广播，即群发
            // queue 代表点对点，即发指定用户
        registry.enableSimpleBroker("/queue", "/topic");
        // 全局使用的消息前缀（客户端订阅路径上会体现出来）
            //例如客户端发送消息的目的地为/app/send，则对应控制层@MessageMapping(“/send”)
            //客户端订阅主题的目的地为/app/subscribe，则对应控制层@SubscribeMapping(“/subscribe”)
        //registry.setApplicationDestinationPrefixes("/app");
        // 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        // registry.setUserDestinationPrefix("/user/");

    }*/
}
