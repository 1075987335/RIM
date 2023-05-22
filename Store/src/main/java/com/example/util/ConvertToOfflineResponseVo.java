package com.example.util;

import com.example.storeapi.vo.OfflineResponseVo;

public class ConvertToOfflineResponseVo {
    public static OfflineResponseVo parse(String str){
        String[] parse = str.split(":");
        OfflineResponseVo offlineResponseVo =new OfflineResponseVo();
        offlineResponseVo.setMID(Long.parseLong(parse[0]));
        offlineResponseVo.setData(parse[1]);
        return offlineResponseVo;
    }
}
