package com.example.route.utils;

import com.example.storeapi.vo.OfflineResponseVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConvertDubboPOJO {

    public static List<OfflineResponseVo> parseToOfflineResponseVo(List<HashMap<String, Object>> list){
        List<OfflineResponseVo> result = new ArrayList<>();
        for(HashMap map : list){
            OfflineResponseVo vo = new OfflineResponseVo();
            vo.setID((long)map.get("iD"));
            vo.setUID((long)map.get("uID"));
            vo.setMID((long)map.get("mID"));
            vo.setData(map.get("data"));
            vo.setTID((long)map.get("tID"));
            vo.setType(Byte.parseByte(map.get("type").toString()));
            result.add(vo);
        }
        return result;
    }
}
