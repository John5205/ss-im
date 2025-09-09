package chat.handler;

import chat.service.UserConnectionService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 连接管理处理器
 * 负责处理用户连接和断开事件
 *
 * @author Harrison
 * @version 1.0.0
 */
@Component
@Log4j2
public class ConnectionManagerHandler extends ChannelInboundHandlerAdapter {
    
    @Autowired
    private UserConnectionService userConnectionService;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("New client connected: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client disconnected: {}", ctx.channel().remoteAddress());
        
        // 用户下线
        userConnectionService.userOffline(ctx.channel());
        
        super.channelInactive(ctx);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Connection error: {}", cause.getMessage(), cause);
        
        // 用户下线
        userConnectionService.userOffline(ctx.channel());
        
        ctx.close();
    }
}
