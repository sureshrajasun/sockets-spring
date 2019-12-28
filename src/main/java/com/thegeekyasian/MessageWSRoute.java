package com.thegeekyasian;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageWSRoute extends RouteBuilder {

    static Logger logger = LoggerFactory.getLogger(MessageWSRoute.class);

    @Override
    public void configure() throws Exception {

        onException(Exception.class).log("oops EofException");

        //{ "header":"heartbeat", "sender":"client", "senderTimestamp":"1212","content":"1212121212"}
        from("direct:firstRoute")
                .routeId("chartRoute")
                .log(LoggingLevel.INFO, ">> msg recieved : ${body}")
                .unmarshal().json(JsonLibrary.Jackson, WsMessage.class)
                .process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {
                        WsMessage info = exchange.getIn().getBody(WsMessage.class);
                        logger.info("data from client: " + info);
                        WsMessage response = new WsMessage();
                        response.setHeader("ping");
                        response.setSender("server");
                        response.setSenderTimestamp("" + System.currentTimeMillis());
                        response.setContent("pong");
                        response.setContentType("Response CONTENT to "+info.getSender());
                        response.setClientId(exchange.getExchangeId());
                        exchange.getIn().setBody(response, WsMessage.class);
                    }
                });
                //.marshal().json(JsonLibrary.Jackson, WsMessage.class)
                //.to("websocket:" + CONNECTION_URI).log(LoggingLevel.INFO, ">> msg sent back to client : ${body}");    // intern camel kø


    }
}
