package cn.legaltech.lb.im.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 鉴权处理器
 * @author Harrison
 * @version 1.0.0
 * @since 2024/11/26
 */
public class AuthHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 示例鉴权逻辑：检查是否附带了合法的 Token
        String token = parseTokenFromMessage(msg);
        if (!isValidToken(token)) {
            System.out.println("Unauthorized connection, closing channel.");
            ctx.close(); // 关闭连接
            return;
        }
        super.channelRead(ctx, msg); // 继续处理消息
    }

    private String parseTokenFromMessage(Object msg) {
        // 假设消息是字符串，提取 Token 示例
        return msg.toString().split(" ")[0]; // 简单分隔获取 Token
    }

    private boolean isValidToken(String token) {
        // 实际验证逻辑：这里返回 true 表示通过
        return "valid_token".equals(token);
    }
}