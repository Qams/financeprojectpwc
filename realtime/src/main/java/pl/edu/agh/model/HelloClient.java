package pl.edu.agh.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HelloClient implements Serializable {

    private static Logger logger = Logger.getLogger(HelloClient.class);

    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    private ObjectMapper mapper = new ObjectMapper();

    public ListenableFuture<StompSession> connect() {

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        String url = "ws://{host}:{port}/hello";
        return stompClient.connect(url, headers, new MyHandler(), "localhost", 8080);
    }

    public void subscribeGreetings(StompSession stompSession) throws ExecutionException, InterruptedException {
        stompSession.subscribe("/topic/greetings", new StompFrameHandler() {

            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                logger.info("Received greeting " + new String((byte[]) o));
            }
        });
    }

    public void sendHello(StompSession stompSession, ExchangeRate exchangeRate) {
//        String jsonHello = String.format("{ \"name\" : \"%s\" }", id);
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(exchangeRate);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(jsonInString);
        stompSession.send("/app/hello", jsonInString.getBytes());
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            logger.info("Now connected");
        }
    }

//    public static void main(String[] args) throws Exception {
//        HelloClient helloClient = new HelloClient();
//
//        ListenableFuture<StompSession> f = helloClient.connect();
//        StompSession stompSession = f.get();
//
//        logger.info("Subscribing to greeting topic using session " + stompSession);
//        helloClient.subscribeGreetings(stompSession);
//
//        logger.info("Sending hello message" + stompSession);
//        helloClient.sendHello(stompSession);
//        Thread.sleep(60000);
//    }

}

