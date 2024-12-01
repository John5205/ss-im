package cn.legaltech.lb.im.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 心跳检测处理器
 *
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/26
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(HeartbeatHandler.class);

    /**
     * 读空闲事件处理
     *
     * @param ctx 通道上下文
     * @param evt 心跳事件
     * @throws Exception 异常
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("Read timeout, closing connection.");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}