package test;


import java.util.HashSet;

public class CacheManager {
    private int size;
    private CacheReplacementPolicy crp;
    private HashSet<String> cache;
    public CacheManager(int size, CacheReplacementPolicy crp) {
        this.size = size;
        this.crp = crp;
        cache = new HashSet<>(size);
    }
    public boolean query(String word) {
        return cache.contains(word);
    }
    public void add(String word) {
        int currentSize = cache.size();
        if (currentSize >= size) {
            String removalVictim = crp.remove();
            cache.remove(removalVictim);
        }
        cache.add(word);
        crp.add(word);
    }

}
