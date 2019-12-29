package com.thegeekyasian.controllers;

import com.thegeekyasian.sockets.Socket;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by The Geeky Asian
 */
@Controller
@RequestMapping(value = "/")
public class AppController {

    @Autowired
    ProducerTemplate producerTemplate;

    @Resource(name = "myMap")
    private Map<String, String> myMap;

    @RequestMapping(value = "/broadcast")
    public ResponseEntity<String> testSocket(@RequestParam("message") String message) throws IOException, EncodeException {
        myMap.put(message, message);

        Socket.broadcast(message);
        String successMessage = String.format(myMap.size() + "Operation completed! " +
                "Data broadcast to %s listeners", Socket.listeners.size());
        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }

    @RequestMapping(value = "/route")
    public ResponseEntity<String> route(@RequestParam("message") String message) throws IOException, EncodeException {


        String object = (String)producerTemplate.requestBody("direct:firstRoute", "{ \"header\":\"heartbeat\", \"sender\":\"client\", \"senderTimestamp\":\"1212\",\"content\":\"1212121212\"}");
        Socket.broadcast(object);
        String successMessage = String.format("Operation completed! " +
                "Data broadcast to %s listeners", Socket.listeners.size());

        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }
}
