# 一对一聊天功能使用指南

## 功能概述

已实现完整的一对一聊天功能，包括：
- 用户登录/登出
- 私聊消息发送
- 在线用户管理
- 广播消息
- 心跳检测

## 连接方式

WebSocket 连接到：`ws://localhost:8086/ws`

## 消息格式

所有消息都使用 JSON 格式：

### 1. 用户登录
```json
{
  "type": "login",
  "content": "user123"
}
```

**响应：**
```json
{
  "type": "login_success",
  "userId": "user123"
}
```

### 2. 发送私聊消息
```json
{
  "type": "private_message",
  "content": "Hello, how are you?",
  "senderId": "user123",
  "targetId": "user456"
}
```

**发送者收到确认：**
```json
{
  "type": "message_sent",
  "toUserId": "user456",
  "content": "Hello, how are you?",
  "timestamp": 1703123456789
}
```

**接收者收到消息：**
```json
{
  "type": "private_message",
  "fromUserId": "user123",
  "toUserId": "user456",
  "content": "Hello, how are you?",
  "timestamp": 1703123456789
}
```

### 3. 获取在线用户
```json
{
  "type": "get_online_users",
  "content": ""
}
```

**响应：**
```json
{
  "type": "online_users",
  "users": ["user123", "user456", "user789"],
  "count": 3
}
```

### 4. 广播消息
```json
{
  "type": "broadcast",
  "content": "Hello everyone!",
  "senderId": "user123"
}
```

**所有在线用户收到：**
```json
{
  "type": "broadcast_message",
  "fromUserId": "user123",
  "content": "Hello everyone!",
  "timestamp": 1703123456789
}
```

### 5. 心跳检测
```json
{
  "type": "ping",
  "content": "ping"
}
```

**响应：**
```json
{
  "type": "pong",
  "timestamp": 1703123456789
}
```

### 6. 用户登出
```json
{
  "type": "logout",
  "content": ""
}
```

**响应：**
```json
{
  "type": "logout_success",
  "message": "Logged out successfully"
}
```

## 错误处理

### 用户未登录
```json
{
  "type": "error",
  "message": "User not logged in"
}
```

### 目标用户离线
```json
{
  "type": "message_failed",
  "toUserId": "user456",
  "message": "User is offline"
}
```

### 消息格式错误
```json
{
  "type": "error",
  "message": "Invalid message format"
}
```

## 使用示例

### JavaScript 客户端示例

```javascript
const ws = new WebSocket('ws://localhost:8086/ws');

ws.onopen = function() {
    console.log('Connected to chat server');
    
    // 登录
    ws.send(JSON.stringify({
        type: 'login',
        content: 'user123'
    }));
};

ws.onmessage = function(event) {
    const message = JSON.parse(event.data);
    console.log('Received:', message);
    
    switch(message.type) {
        case 'login_success':
            console.log('Logged in as:', message.userId);
            break;
        case 'private_message':
            console.log('Private message from', message.fromUserId, ':', message.content);
            break;
        case 'message_sent':
            console.log('Message sent to', message.toUserId);
            break;
        case 'online_users':
            console.log('Online users:', message.users);
            break;
    }
};

// 发送私聊消息
function sendPrivateMessage(toUserId, content) {
    ws.send(JSON.stringify({
        type: 'private_message',
        content: content,
        senderId: 'user123',
        targetId: toUserId
    }));
}

// 获取在线用户
function getOnlineUsers() {
    ws.send(JSON.stringify({
        type: 'get_online_users',
        content: ''
    }));
}
```

## 服务端组件

### 1. UserConnectionService
- 管理用户连接状态
- 处理用户上线/下线
- 发送私聊消息
- 广播消息

### 2. PrivateChatHandler
- 处理私聊相关消息
- 用户登录/登出
- 在线用户查询

### 3. MessageRoutingService
- 消息路由分发
- 统一消息处理入口

### 4. ConnectionManagerHandler
- 连接生命周期管理
- 异常处理

## 部署说明

1. 启动服务器：`mvn spring-boot:run`
2. WebSocket 服务运行在 `ws://localhost:8086/ws`
3. 支持多客户端同时连接
4. 自动处理用户重复登录（踢出旧连接）

## 注意事项

1. **用户ID唯一性**：确保每个用户使用唯一的用户ID
2. **消息格式**：严格按照 JSON 格式发送消息
3. **连接管理**：客户端需要处理连接断开和重连
4. **错误处理**：客户端需要处理各种错误响应
5. **心跳检测**：建议定期发送 ping 消息保持连接
