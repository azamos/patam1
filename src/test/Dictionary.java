package test;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Dictionary {
    String[] fileNames;
    CacheManager existing;
    CacheManager missing;
    BloomFilter bloomfilter;
    String[] algorithms = {"MD5", "SHA256"};
    public Dictionary(String... fileNames) {
        this.fileNames = fileNames;
        existing = new CacheManager(400,new LRU());
        missing = new CacheManager(100,new LFU());
        bloomfilter = new BloomFilter(256,"MD5","SHA-256" );
        try{
            populateBloomFilter();
        }
        catch (Exception e){
//            e.printStackTrace();
        }
    }
    private void populateBloomFilter() throws FileNotFoundException {
        for (String fileName : fileNames) {
            Scanner scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(fileName)
                    )
            );
            while (scanner.hasNext()) {
                bloomfilter.add(scanner.next());
            }
            scanner.close();
        }
    }
    public boolean query(String word) {
        if (existing.query(word)) return true;
        if (missing.query(word)) return false;
        boolean inBloomFilter = bloomfilter.contains(word);
        if(inBloomFilter==true){
            existing.add(word);
        }
        else{
            missing.add(word);
        }
        return inBloomFilter;
    }
    public boolean challenge(String word) {
        try{
            boolean result = IOSearcher.search(word,fileNames);
            if(result==true){
                existing.add(word);
                bloomfilter.add(word);
            }
            else{
                missing.add(word);
            }
            return result;
        }
        catch (Exception e){
            return false;
        }
    }
}
