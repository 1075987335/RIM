package com.example.common.enums;

public enum MessageType implements CodeAdapter {
    DATA_TYPE_JSON((byte) 0),
    DATA_TYPE_PROTOBUF((byte) 1),
    DATA_TYPE_XML((byte) 2),
    DATA_TYPE_KRYO((byte) 3),
    ;

    private byte msgType;

    //根据数字来获取消息类型
    MessageType(byte msgType) {
        this.msgType = msgType;
    }


    @Override
    public byte getCode() {
        return msgType;
    }
}
