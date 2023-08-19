package com.example.client.response;


import lombok.Data;

import java.io.Serializable;

@Data
public class ServerInfo implements Serializable {
    private String ip;

    private int ServerPort;

    private int httpPort;
}
