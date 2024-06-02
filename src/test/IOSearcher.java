package test;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class IOSearcher {
    public static boolean search(String word,String... fileNames) throws FileNotFoundException {
        for (String fileName : fileNames) {
            Scanner scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(fileName)
                    )
            );
            while (scanner.hasNext()) {
                if(word.equals(scanner.next())) return true;
            }
            scanner.close();
        }
        return false;
    }
}
