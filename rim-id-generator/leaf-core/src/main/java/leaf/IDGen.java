package leaf;


import leaf.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
