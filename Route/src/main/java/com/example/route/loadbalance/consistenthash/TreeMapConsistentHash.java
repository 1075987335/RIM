package com.example.route.loadbalance.consistenthash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.SortedMap;
import java.util.TreeMap;

@Component
@Slf4j
public class TreeMapConsistentHash extends AbstractConsistentHash {
    private TreeMap<Long,String> treeMap = new TreeMap() ;

    /**
     * 虚拟节点数量
     */
    private static final int VIRTUAL_NODE_SIZE = 4 ;

    @Override
    public void add(long key, String value) {

        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            Long hash = super.hash("vir" + key + i);
            treeMap.put(hash,value);
        }
        treeMap.put(key, value);
    }

    public void remove(){
        treeMap.clear();
    }

    @Override
    public String getFirstNodeValue(String value) {
        long hash = super.hash(value);
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        if (!last.isEmpty()) {
            return last.get(last.firstKey());
        }
        if (treeMap.size() == 0){
            log.info("无可用服务节点...");
        }
        return treeMap.firstEntry().getValue();
    }
}
