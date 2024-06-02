package test;


import java.io.*;
import java.util.Arrays;

public class BookScrabbleHandler implements ClientHandler{
    private BufferedReader br;
    private BufferedWriter bw;
    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient){
        try{
            br = new BufferedReader(new InputStreamReader(inFromclient));
            bw = new BufferedWriter(new OutputStreamWriter(outToClient));
            String inputLine = br.readLine();
            while (inputLine==null||inputLine.isEmpty()){
                return;
            }
            char firstChar = inputLine.charAt(0);
            String[] bookNamesAndQuery = Arrays.stream(inputLine.substring(1).trim().split(",")).map(String::trim).toArray(String[]::new);
            boolean result = 'Q'==firstChar ? DictionaryManager.get().query(bookNamesAndQuery) : DictionaryManager.get().challenge(bookNamesAndQuery);
            String outputLine = String.valueOf(result);
            bw.write(outputLine+"\r\n");
            bw.flush();
        }
        catch(IOException e){
//            e.printStackTrace();
        }
    }
    @Override
    public void close(){
        try{
            if(br!=null) br.close();
            if(bw!=null) bw.close();
        }
        catch(IOException e){
//            e.printStackTrace();
        }
    }
}
