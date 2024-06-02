package test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class BloomFilter {
	private List<String> algs=new ArrayList<String>();
    private List<MessageDigest> hashFunctions;
    BitSet bitSet;
    int bitArrSize;
    public BloomFilter(int bitArrSize, String... algs) {
        this.bitArrSize = bitArrSize;
        initAlgs(algs);
        bitSet = new BitSet(bitArrSize);
        hashFunctions = new ArrayList<MessageDigest>();
        initHashFunctions();
    }
    private void initAlgs(String... alorithms){
        for (String alorithm : alorithms) {
            algs.add(alorithm);
        }
    }
    private void initHashFunctions() {
        algs.forEach(alg -> {
            try {
                hashFunctions.add(MessageDigest.getInstance(alg));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void add(String word){
        hashFunctions.forEach(hashFunction -> {
            byte[] bytes = hashFunction.digest(word.getBytes());
            BigInteger bigInteger = new BigInteger(1, bytes);
            int index = Math.abs(bigInteger.intValue())%bitArrSize;
            bitSet.set(index);
        });
    }
    public boolean contains(String word){
        for (MessageDigest hashFunction : hashFunctions) {
            byte[] bytes = hashFunction.digest(word.getBytes());
            BigInteger bigInteger = new BigInteger(1, bytes);
            int index = Math.abs(bigInteger.intValue()) % bitArrSize;
            if (!bitSet.get(index)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        char[] chars = new char[bitArrSize];
        for (int i = 0; i < bitArrSize; i++) {
            chars[i] = bitSet.get(i)?'1':'0';
        }
        String withTrailingZeros = new String(chars);
        int finalOne = withTrailingZeros.lastIndexOf('1');
        return withTrailingZeros.substring(0, finalOne+1);
    }
}
