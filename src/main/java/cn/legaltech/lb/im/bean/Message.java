package cn.legaltech.lb.im.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息对象
 *
 * @author Harroson
 * @version 1.0.0
 * @since 2024/11/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String type;     // 消息类型
    private String content;  // 消息内容
    private String senderId;   // 发送方ID
    private String targetId;   // 目标方ID（可选）
}