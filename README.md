# ss-im

#### 介绍

ss-im 是一款简单便捷的即时通讯工具，基于Netty高性能框架实现。目前仅支持文字单聊,后面还会支持图片、语音、视频等。

#### 安装教程

1、首先maven添加依赖

  ```
  <dependency>
      <groupId>io.gitee.harrison-ss</groupId>
      <artifactId>ss-im</artifactId>
      <version>1.0.0</version>
  </dependency>
  ```  

#### 使用说明

1、添加配置文件使用@NettyServer注解、消息监听器@MessageMapping

