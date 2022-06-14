package ru.gb.chat.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.chat.server.auth.AuthenticationService;
import ru.gb.chat.server.auth.ClientHandler;
import ru.gb.chat.server.auth.ClientsBD;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private ServerSocket serverSocket;
    private AuthenticationService authenticationService;
    private final Set<ClientHandler> handlers;
    private static final Logger logger = LogManager.getLogger(Server.class);

    public Server() {
        logger.info("Server get started");
        handlers = new HashSet<>();
        final ClientsBD clientsBD = new ClientsBD();

        try {
            authenticationService = new AuthenticationService();
            authenticationService.connect();
            clientsBD.run();
            serverSocket = new ServerSocket(8989);
            init();
        } catch (SQLException | IOException e) {
            logger.throwing(Level.ERROR, e);
        } finally {
            AuthenticationService.disconnect();
        }
    }

    /** запуск сервера */
    private void init() throws IOException {
        while (true) {
            logger.info("Server is waiting for a connection...");
            Socket client = serverSocket.accept();

            new ClientHandler(this, client);
            logger.info("Client accepted: " + client);
        }
    }

    /** проверка на наличие в списке залогиненных пользователей */
    public synchronized boolean isNicknameFree(String nickname) {
        for (ClientHandler handler : handlers) {
            if (handler.getName().equals(nickname)) {
                return false;
            }
        }
        return true;
    }

    /** публикация сообщения для пользователя в общем чате */
    public synchronized void broadcast(String message) {
        for (ClientHandler handler : handlers) {
            handler.sendMessage(message);
        }
    }

    /** внесение в список залогиненных пользователей */
    public synchronized void subscribe(ClientHandler handler) {
        handlers.add(handler);
        logger.info(handler.getName() + " is authorized");
    }

    /** удаление из списка залогиненных пользователей */
    public synchronized void unsubscribe(ClientHandler handler) {
        broadcast(handler.getName() + ": Client is out.");
        logger.info(handler.getName() + " disconnect");
        handlers.remove(handler);
    }

    /** отправка сообщения конкретному пользователю*/
    public synchronized void sendMsgToClient(ClientHandler from, String nick, String message) {
        for (ClientHandler handler : handlers) {
            if (handler.getName().equals(nick)) {
                handler.sendMessage("Private message from " + from.getName() + ": " + message);
                from.sendMessage("Private to " + nick + ": " + message);
                logger.info(from.getName() + " send private message to " + handler.getName());
                return;
            }
        }
        from.sendMessage("Client " + nick + ": is offline");
        logger.info(from.getName() + " try to write offline client " + nick);
    }

    /** смена nickname*/
    public synchronized void changeNick(String oldNickname, String newNickname) throws SQLException {
        for (ClientHandler handler : handlers) {
            if (handler.getName().equals(oldNickname)) {
                ClientsBD.changeNickname(newNickname, ClientsBD.getLoginByNickname(handler.getName()));
                handler.setName(newNickname);
                logger.info(oldNickname + " change nickname to " + newNickname);
                return;
            }
        }
    }
}
