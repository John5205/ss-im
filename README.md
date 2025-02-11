# ss-im

## 介绍

ss-im 是一款简单便捷的即时通讯工具，基于Netty高性能框架实现。目前仅支持文字单聊,后面还会支持图片、语音、视频等。

## 安装教程

1、首先maven添加依赖

  ```
  <dependency>
      <groupId>io.gitee.harrison-ss</groupId>
      <artifactId>ss-im</artifactId>
      <version>1.0.1</version>
  </dependency>
  ```  

## 使用说明
### 1、添加配置文件使用@**NettyServer注解**、消息监听器@MessageMapping    

  #### NettyServer注解使用  
  1.  port 端口是默认8086，也可以自定义端口
  2. packageName 包名是你消息的处理类所在的包名：io.gitee.harrison-ss.handler，这样的话会自动扫描到 handler报名下所有的消息处理类  

  #### MessageMapping注解使用  
  1. value 消息处理类上使用@MessageMapping注解，消息处理类里方法上使用@MessageMapping注解，方法名就是消息类型，比如：@MessageMapping("login")，那么消息类型就是login  
  2. async 是否异步处理消息，默认是同步处理，如果设置为true，那么消息处理方法会异步执行，不会阻塞消息的接收，但是异步处理会增加系统负载，所以建议异步处理消息不要太多，如果消息处理方法执行时间过长，可能会导致消息处理失败，所以建议消息处理方法不要超过1秒，如果超过1秒，建议异步处理消息  
  3. priority 消息处理方法的优先级，默认是0，如果消息处理方法优先级大于0，那么消息处理方法会优先执行，如果消息处理方法优先级小于0，那么消息处理方法会后执行，如果消息处理方法优先级相同，那么消息处理方法会按照方法定义的顺序执行  
  4. authRequired 是否需要认证，默认是false，如果设置为true，那么消息处理方法会先认证用户是否登录，如果用户没有登录，那么消息处理方法会返回用户未登录的提示信息，如果用户登录了，那么消息处理方法会继续执行，如果用户登录了，但是用户没有权限，那么消息处理方法会返回用户没有权限的提示信息，如果用户登录了，并且用户有权限，那么消息处理方法会继续执行  