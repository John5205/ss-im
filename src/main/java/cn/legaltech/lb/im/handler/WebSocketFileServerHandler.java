package cn.legaltech.lb.im.handler;

import cn.legaltech.lb.im.utils.JsonUtils;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 文件传输服务处理器
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/12/2
 */
public class WebSocketFileServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger log = LogManager.getLogger(WebSocketFileServerHandler.class);

    /**
     * 文件输出流
     */
    private final ConcurrentHashMap<String, FileOutputStream> fileStreams = new ConcurrentHashMap<>();

    /**
     * 处理接收到的消息
     *
     * @param ctx   通道
     * @param frame 消息
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String message = ((TextWebSocketFrame) frame).text();
            processMessage(ctx, message);
        }
    }

    /**
     * 处理消息
     *
     * @param ctx     通道
     * @param message 消息
     * @throws Exception 异常
     */
    private void processMessage(ChannelHandlerContext ctx, String message) throws Exception {
        log.info("Received message: " + message);
        ConcurrentHashMap<String, Object> concurrentHashMap = JsonUtils.fromJson(message, ConcurrentHashMap.class);

        String type = (String) concurrentHashMap.get("type");

        switch (type) {
            case "file-init":
                handleFileInit(concurrentHashMap, ctx);
                break;
            case "file-data":
                handleFileData(concurrentHashMap);
                break;
            case "file-complete":
                handleFileComplete(concurrentHashMap, ctx);
                break;
            default:
                ctx.writeAndFlush(new TextWebSocketFrame("Unknown message type: " + type));
        }
    }

    /**
     * 处理文件初始化
     *
     * @param msg 消息
     * @param ctx 通道
     * @throws Exception 异常
     */
    private void handleFileInit(ConcurrentHashMap<String, Object> msg, ChannelHandlerContext ctx) throws Exception {
        log.info("Received file init message: " + msg);
        String fileName = (String) msg.get("fileName");
        File file = new File("uploads/" + fileName);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fileStreams.put(fileName, fos);

        ctx.writeAndFlush(new TextWebSocketFrame("File init received for: " + fileName));
    }

    /**
     * 处理文件数据
     *
     * @param msg 消息
     * @throws Exception 异常
     */
    private void handleFileData(ConcurrentHashMap<String, Object> msg) throws Exception {
        log.info("Received file data message: " + msg);
        String fileName = (String) msg.get("fileName");
        String data = (String) msg.get("data");

        byte[] decodedData = Base64.getDecoder().decode(data);
        FileOutputStream fos = fileStreams.get(fileName);

        if (fos != null) {
            fos.write(decodedData);
        }
    }

    /**
     * 处理文件完成
     *
     * @param msg 消息
     * @param ctx 通道
     * @throws Exception 异常
     */
    private void handleFileComplete(ConcurrentHashMap<String, Object> msg, ChannelHandlerContext ctx) throws Exception {
        log.info("Received file complete message: " + msg);
        String fileName = (String) msg.get("fileName");
        FileOutputStream fos = fileStreams.remove(fileName);

        if (fos != null) {
            fos.close();
        }

        ctx.writeAndFlush(new TextWebSocketFrame("File transfer complete: " + fileName));
    }
}