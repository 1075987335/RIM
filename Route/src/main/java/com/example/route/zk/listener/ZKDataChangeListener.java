package com.example.route.zk.listener;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;

@Slf4j
public class ZKDataChangeListener implements IZkDataListener {

    @Override
    public void handleDataChange(String s, Object o) {
        log.info("s:{}, o:{}", s, o);
    }

    @Override
    public void handleDataDeleted(String s) {
        String[] str = s.split("/");
        String serverInfo = str[str.length-1];

        log.info("s:{}", s);
    }
}
