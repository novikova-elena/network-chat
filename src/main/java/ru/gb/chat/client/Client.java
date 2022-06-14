package ru.gb.chat.client;

public class Client {
    public static void main(String[] args) {
        new ClientChatAdapter("localhost", 8989);
    }
}
