package httpServer;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 *  HTTP server for COS 460/540 Project 2.
 */
public class serverProject {
    private final int port;                 // TCP port to listen on
    private final File rootDirectory;       // Document root folder
    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;

    /**
     * Constructor: initializes server with root directory, port, and max threads.
     */
    public serverProject(File rootDirectory, int port, int maxThreads) throws IOException {
        this.rootDirectory = rootDirectory;
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(maxThreads); // Thread pool for concurrent clients
    }

    /**
     * Start the server: listen for incoming connections and handle each in a separate thread.
     */
    public void start() {
        System.out.println("Server started on port " + port);
        System.out.println("Serving files from " + rootDirectory.getAbsolutePath());

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket, rootDirectory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles a single client request.
     */
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final File rootDirectory;

        public ClientHandler(Socket clientSocket, File rootDirectory) {
            this.clientSocket = clientSocket;
            this.rootDirectory = rootDirectory;
        }

        @Override
        public void run() {
            try (
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input))
            ) {
                // Read the first request line: e.g., GET /index.html HTTP/1.1
                String requestLine = reader.readLine();
                if (requestLine == null || requestLine.isEmpty()) return;

                System.out.println("Request: " + requestLine);
                StringTokenizer tokenizer = new StringTokenizer(requestLine);
                String method = tokenizer.nextToken();
                String path = tokenizer.nextToken();

                // Only GET requests are supported
                if (!method.equals("GET")) {
                    sendResponse(output, "501 Not Implemented", "text/plain", "Method not implemented".getBytes());
                    return;
                }

                // Handle default index.html for directories
                if (path.endsWith("/")) {
                    path += "index.html";
                }

                File file = new File(rootDirectory, URLDecoder.decode(path, "UTF-8")).getCanonicalFile();

                // Security check: prevent access outside the document root
                if (!file.exists() || !file.getAbsolutePath().startsWith(rootDirectory.getAbsolutePath())) {
                    sendResponse(output, "404 Not Found", "text/plain", "File not found".getBytes());
                    return;
                }

                // Read file data and determine MIME type
                String contentType = getContentType(file);
                byte[] fileData = readFileData(file);

                // Send HTTP response
                sendResponse(output, "200 OK", contentType, fileData);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { clientSocket.close(); } catch (IOException e) { /* ignore */ }
            }
        }

        /**
         * Send an HTTP response to the client.
         */
        private void sendResponse(OutputStream output, String status, String contentType, byte[] content) throws IOException {
            PrintWriter writer = new PrintWriter(output);
            writer.print("HTTP/1.1 " + status + "\r\n");
            writer.print("Date: " + new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).format(new Date()) + "\r\n");
            writer.print("Server: COS460-HTTPServer/1.0\r\n");
            writer.print("Content-Type: " + contentType + "\r\n");
            writer.print("Content-Length: " + content.length + "\r\n");
            writer.print("\r\n");
            writer.flush();

            output.write(content);
            output.flush();
        }

        /**
         * Reads the entire file into a byte array.
         */
        private byte[] readFileData(File file) throws IOException {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                return data;
            }
        }

        /**
         * Determines the MIME type based on file extension.
         */
        private String getContentType(File file) {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".html") || name.endsWith(".htm")) return "text/html";
            if (name.endsWith(".txt")) return "text/plain";
            if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
            if (name.endsWith(".png")) return "image/png";
            if (name.endsWith(".gif")) return "image/gif";
            if (name.endsWith(".css")) return "text/css";
            if (name.endsWith(".js")) return "application/javascript";
            if (name.endsWith(".json")) return "application/json";
            if (name.endsWith(".pdf")) return "application/pdf";
            return "application/octet-stream"; // Default for unknown types
        }
    }

    /**
     * Entry point. Expects document root and port as command-line arguments.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java httpServer.serverProject <document_root> <port>");
            return;
        }

        File root = new File(args[0]);
        int port = Integer.parseInt(args[1]);

        if (!root.isDirectory()) {
            System.out.println("Error: Document root is not a directory.");
            return;
        }

        try {
            serverProject server = new serverProject(root, port, 10); // 10 threads in pool
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
