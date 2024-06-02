package test;

import java.util.LinkedHashMap;

public class LRU implements CacheReplacementPolicy {
    private final LinkedHashMap lru = new LinkedHashMap(16,0.75F, true);
    public void add(String word){
        lru.put(word, true);
    }
    public String remove(){
        if(lru.isEmpty()) return null;
        String word = (String) lru.keySet().toArray()[0];
        lru.remove(word);
        return word;
    }
}
