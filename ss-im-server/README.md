# SS-IM Server

基于 Netty 的即时通讯服务器，使用 ss-im-core 核心模块。

## 功能特性

- WebSocket 支持
- 消息类型处理（文本、文件、音频、视频）
- 房间管理
- 用户认证
- 心跳检测
- 文件传输

## 启动方式

```bash
mvn spring-boot:run
```

服务器将在 `http://localhost:8086/ws` 启动 WebSocket 服务。

## 消息格式

### 认证消息
```json
{
  "type": "auth",
  "content": "your_token_here"
}
```

### 聊天消息
```json
{
  "type": "chat",
  "content": "Hello World",
  "senderId": "user123",
  "targetId": "user456"
}
```

### 房间操作
```json
{
  "type": "join_room",
  "content": "room123:user456"
}
```

### 心跳检测
```json
{
  "type": "ping",
  "content": "ping"
}
```

## 支持的消息类型

- `auth` - 用户认证
- `logout` - 用户登出
- `chat` - 聊天消息
- `text` - 文本消息
- `file` - 文件消息
- `image` - 图片消息
- `audio` - 音频消息
- `video` - 视频消息
- `ping` - 心跳检测
- `join_room` - 加入房间
- `leave_room` - 离开房间
- `broadcast_room` - 房间广播

## 配置

在 `NettyConfig` 类中可以配置：
- 端口号（默认 8086）
- WebSocket 路径（默认 /ws）
- 扫描包名（默认 chat.handler）
