package com.thegeekyasian.sockets;

import com.thegeekyasian.utils.MessageDecoder;
import com.thegeekyasian.utils.MessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * https://blog.csdn.net/qq_28289867/article/details/80423042.
 *
 */
@Slf4j
@Component
@ServerEndpoint(value = "/webSocket", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class Socket {

    @Autowired
    ProducerTemplate producerTemplate;



    static Map<String, String> myMap = new HashMap<>();

    private static ApplicationContext applicationContext;


    /*@Autowired
    ConsumerTemplate consumerTemplate;*/

    private Session session;
    public static Set<Socket> listeners = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) throws InterruptedException {

        // Map myMap =  (Map)applicationContext.getBean("myMap");

        this.session = session;
        listeners.add(this);
        log.info(String.format("New session connected! Connected listeners: %s", listeners.size()));
    }

    @OnMessage //Allows the client to send message to the socket.
    public void onMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message.toUpperCase());

            // run in a second
            final long timeInterval = 1000;
            Runnable runnable = new Runnable() {
                public void run() {
                    while (true) {
                        // ------- code for task to run
                        sendMessage(message + " -- "+ myMap.size() +" - "+ LocalDateTime.now());
                        // ------- ends here
                        try {
                            Thread.sleep(timeInterval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };
            Thread thread = new Thread(runnable);
            thread.start();
                //String object = (String)producerTemplate.requestBody("direct:firstRoute", "{ \"header\":\"heartbeat\", \"sender\":\"client\", \"senderTimestamp\":\"1212\",\"content\":\"1212121212\"}");
                //log.info("{}",object);
        }catch (IOException e) {
            e.printStackTrace();
            log.error("Caught exception while sending message to Session Id: " + this.session.getId(), e.getMessage(), e);
        }

        //broadcast(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) throws IOException {
        log.info("Reason for close : {}", reason.getReasonPhrase());
        listeners.remove(this);
        log.info(String.format("Session disconnected. Total connected listeners: %s", listeners.size()));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        //Error
    }

    public static void broadcast(String message) {
        myMap.put(message,message);
        for (Socket listener : listeners) {
            listener.sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("Caught exception while sending message to Session Id: " + this.session.getId(), e.getMessage(), e);
        }
    }
}