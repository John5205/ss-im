package cn.legaltech.lb.im.enums;

/**
 * 消息类型枚举
 *
 * @author Harrison
 * @version 1.0.0
 */
public enum MessageTypeEnum {

    HEARTBEAT(0, "heartbeat", "心跳"),
    TEXT(1, "text", "文本"),
    FILE(2, "file", "文件"),
    IMAGE(3, "image", "图片"),
    AUDIO(4, "audio", "音频"),
    VIDEO(5, "video", "视频"),
    COMMAND(6, "command", "命令"),
    ;

    public int code;
    public String typeCode;
    public String desc;

    MessageTypeEnum(int code, String typeCode, String desc) {
        this.typeCode = typeCode;
        this.code = code;
        this.desc = desc;
    }
}
