package test;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MyServer {
    private int port;
    private ClientHandler ch;
    private volatile boolean stop;
    private Thread backgroundThread;
    private ServerSocket server;
    public MyServer(int port,ClientHandler chInstance) {
        this.port = port;
        ch = chInstance;
        stop = false;
    }

    private void runServer() throws Exception {
        server = new ServerSocket(port);
        server.setSoTimeout(1000);
        while (!stop) {
            try{
                Socket aClient = server.accept();
                try{
                    ch.handleClient(aClient.getInputStream(),aClient.getOutputStream());
                    aClient.close();
                }
                catch (IOException e){}
            }
            catch (SocketTimeoutException e){}
        }
        server.close();
    }

    public void start()  {
        try{
            backgroundThread = new Thread(()-> {
                try {
                    runServer();
                } catch (Exception e) {
                }
            });
            backgroundThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        stop = true;
        try{
            backgroundThread.join();
            ch.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(backgroundThread.isAlive()){
                backgroundThread.interrupt();
            }
        }
    }
}
