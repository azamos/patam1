package test;


import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class LFU implements CacheReplacementPolicy {
    String min = null;
    private final HashMap<String, Integer> frequencies=new HashMap<String, Integer>();
    public void add(String word){
        if(frequencies.get(word)!=null){
            frequencies.put(word,frequencies.get(word)+1);
        }
        else{
            frequencies.put(word,1);
            if(min==null || frequencies.get(min)>1){
                min = word;
            }
        }
        setMin();
    }
    private void setMin(){/*TODO: probably won't work*/
        String newMin=null;
        Iterator<String> it = frequencies.keySet().iterator();
        while(it.hasNext()){
            String word = it.next();
            if(newMin==null || frequencies.get(word)<frequencies.get(newMin)){
                newMin=word;
            }
        }
        min=newMin;
    }

    public String remove(){
        String ret= min;
        frequencies.remove(min);
        setMin();
        return ret;
    }
}
