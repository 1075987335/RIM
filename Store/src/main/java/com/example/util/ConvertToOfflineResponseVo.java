package com.example.util;

import com.example.storeapi.vo.OfflineResponseVo;

public class ConvertToOfflineResponseVo {
    public static OfflineResponseVo parse(String str){
        String[] parse = str.split(":");
        OfflineResponseVo offlineResponseVo =new OfflineResponseVo();
        if(parse.length == 2){
            offlineResponseVo.setMID(Long.parseLong(parse[0]));
            offlineResponseVo.setData(parse[1]);
        }
        else{
            offlineResponseVo.setMID(Long.parseLong(parse[0]));
            offlineResponseVo.setData(parse[1]);
            offlineResponseVo.setUID(Long.parseLong(parse[2]));
        }
        return offlineResponseVo;
    }
}
