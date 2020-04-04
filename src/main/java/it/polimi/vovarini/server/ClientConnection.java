package it.polimi.vovarini.server;

public interface ClientConnection {

    void closeConnection();

    void asyncSend(Object message);

}
