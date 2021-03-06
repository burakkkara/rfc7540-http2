package test;

import http2.HTTP2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

// This class is written for speed test server.
// But changed to handle HTTP/2.0 requests

public class MultiThreadServer implements Runnable {
    private Socket clientSocket;
    private long time;
    private final String ROOT = ""; //"/home/ec2-user/webpage";
    private final String NEW_LINE = "\n";
    private final String BODY = NEW_LINE + NEW_LINE;
    private final String OK = "HTTP/1.1 200 OK" + NEW_LINE;

    MultiThreadServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Server started port at" + 9090);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("connected");
            new Thread(new MultiThreadServer(socket)).start();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request = inputReader.readLine();
            if (request != null) {
                time = System.currentTimeMillis();
                System.out.println(request);
                processRequest(request);
                clientSocket.close();
            }
        } catch (IOException ioe) {
            System.out.print("Error on connection from client" + NEW_LINE);
        }
    }

    private void processRequest(String request) {
        String tokens[] = request.split(" ");
        String method = tokens[0];
        String path = tokens[1];
        if (method.equalsIgnoreCase("get")) {
            if (path.startsWith("/pg"))
                sendPong();
            else if (path.startsWith("/dw"))
                sendDownloadResponse();
            else if (path.startsWith("/favicon.ico")) // TODO implement for .ico file
                sendPong();
                //sendWebpageResponse(ROOT + path);
            else if (path.startsWith("/http2"))
                sendHTTP2();
            else
                sendWebpageResponse(ROOT + path);
        } else if (path.startsWith("/up"))
            sendUploadResponse();
    }

    private void sendPong() {
        sendResponse(OK + NEW_LINE);
    }

    private void sendHTTP2() {
        HTTP2 packet = (new TestCreator()).packetExp();
        String bin = packet.getPacketAsString();
        String str = "HTTP/1.1 101 Switching Protocols" + NEW_LINE;
        str += "Connection: upgrade" + NEW_LINE;
        str += "Upgrade: h2c" + BODY;
        sendResponse(str + bin);
        System.out.println(bin);
    }

    private void sendDownloadResponse() {
        String headers = OK + addDownloadHeaders();
        String content = getFileContent(ROOT + "/download/file");
        time = System.currentTimeMillis() - time;
        content = Long.toString(time) + NEW_LINE + content;
        long size = content.length() * 8;
        headers += "Content-Length: " + size + BODY;
        sendResponse(headers + content);
    }

    private String addDownloadHeaders() {
        String str = "Date: " + new SimpleDateFormat().format(new Date());
        str += NEW_LINE + "Server: Kibitzer" + NEW_LINE;
        str += "Connection: Keep-Alive" + NEW_LINE;
        str += "Accept-Encoding: identity" + NEW_LINE;
        str += "Access-Control-Allow-Origin: *" + NEW_LINE;
        str += "Cache-Control: no-cache, no-store" + NEW_LINE;
        return str;
    }

    private String getFileContent(String path) {
        System.out.println("Serving from this directory: " + path);
        StringBuilder str = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(new FileReader(new File(path)));
            String line;
            while ((line = input.readLine()) != null)
                str.append(line).append("\r" + NEW_LINE);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    private void sendImageResponse() {
        // TODO implement
    }

    private void sendWebpageResponse(String path) {
        String page = OK + addWebpageHeaders() + getFileContent(path);
        sendResponse(page);
    }

    private String addWebpageHeaders() {
        String str = "Date: " + new SimpleDateFormat().format(new Date());
        str += NEW_LINE + "Cache-Control: public, max-age=31536000" + NEW_LINE;
        str += "Accept-Encoding: gzip, compress, br" + NEW_LINE;
        str += "Server: Kibitzer" + BODY;
        return str;
    }

    private void sendUploadResponse() {
        String upload = OK + addDownloadHeaders();
        sendResponse(upload);
    }

    private byte[] getIcoContent(String path) {
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private void sendResponse(String packet) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "ASCII")); // UTF-8
            out.write(packet);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}