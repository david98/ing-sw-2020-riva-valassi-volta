package it.polimi.vovarini.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class EchoClient {
  public static void main(String[] args) throws IOException {
    ServerSocket s = new ServerSocket(6969);
    System.out.println("Started: " + s);
    // Blocks until a connection occurs:
    Socket socket = s.accept();
    System.out.println("Connection accepted: " + socket);
  }
}
