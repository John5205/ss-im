package chat.config;


import xin.harrison.im.annotation.NettyServer;

/**
 * @author Harrison
 * @version 1.0.0
 * 
 */
@NettyServer(packageName = "chat.handler", port = 8086, wsPath = "/ws")
public class NettyConfig {
}
