package test;


import java.util.HashMap;
import java.util.function.BiFunction;

public class DictionaryManager {
    private HashMap<String,Dictionary> bookDict = new HashMap<>();
    private static DictionaryManager singlestonInstance = null;

    public static DictionaryManager get(){
        if(singlestonInstance == null){
            singlestonInstance = new DictionaryManager();
        }
        return singlestonInstance;
    }

    private boolean updateBookDictAndAct(BiFunction<Dictionary,String,Boolean> action, String ...args){
        String word = args[args.length-1];
        Boolean exist = false;
        for(int i =0; i< args.length-1;i++){
            String bookName = args[i];
            if(!bookDict.containsKey(bookName)){
                bookDict.put(bookName, new Dictionary(bookName));
            }
            if(action.apply(bookDict.get(bookName),word)){
                exist = true;
            }
        }
        return exist;
    }

    public boolean query(String ...args) {
        return updateBookDictAndAct(Dictionary::query, args);
    }

    public boolean challenge(String ...args) {
        return updateBookDictAndAct(Dictionary::challenge, args);
    }

    public int getSize(){
        return bookDict.size();
    }
}
