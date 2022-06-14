package ru.gb.chat.client.network;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BasicChatNetwork {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private static final Logger logger = LogManager.getLogger(BasicChatNetwork.class);

    public BasicChatNetwork(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logger.info("SWW during establishing");
            logger.throwing(Level.ERROR, e);
            throw new RuntimeException();
        }
    }

    public void send(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            logger.info("SWW during send");
            logger.throwing(Level.ERROR, e);
            throw new RuntimeException();
        }
    }

    public String receive() {
        try {
            return in.readUTF();
        } catch (IOException e) {
            logger.info("SWW during receive");
            logger.throwing(Level.ERROR, e);
            throw new RuntimeException();
        }
    }
}

