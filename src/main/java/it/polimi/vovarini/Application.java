package it.polimi.vovarini;

import it.polimi.vovarini.common.network.server.Server;
import it.polimi.vovarini.view.cli.GameView;
import it.polimi.vovarini.view.gui.GuiManager;
import it.polimi.vovarini.view.gui.Settings;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "santorini", mixinStandardHelpOptions = true, version = "santorini 1.0",
        description = "Starts Santorini")
public class Application implements Callable<Integer> {

  @CommandLine.Option(names = {"-s", "--server"}, description = "Run as server")
  private boolean serverMode;
  @CommandLine.Parameters(arity = "0..1", index = "0", description = "The server hostname or IP address")
  private String serverIP = "santorini.davide.gdn";

  @CommandLine.Option(names = {"-n", "--number"}, description = "The number of players")
  private int playersNumber = 2;

  @CommandLine.Option(names = {"-p", "--port"}, description = "The port to connect to (or to listen on if running as server")
  private int port = Server.DEFAULT_PORT;

  @CommandLine.Option(names = {"-c", "--cli"}, description = "Run command line client")
  private boolean useCLI;


  public void launchServer(int port, int numberOfPlayers) throws IOException {
    Server server = new Server(port, numberOfPlayers);
    Thread thread = new Thread(server);
    thread.start();
    try {
      Thread.currentThread().join();
    } catch (InterruptedException e){
      Thread.currentThread().interrupt();
    }
  }

  public void launchClient(ClientMode mode, String serverIP, int serverPort) throws IOException {
    switch (mode) {
      case CLI -> {
        GameView view = new GameView(serverIP, serverPort);
        view.gameSetup();
      }
      case GUI -> {
        GuiManager gui = GuiManager.getInstance();
        gui.gameSetup();
      }
    }
  }

  @Override
  public Integer call() throws IOException {
    if (serverMode){
      launchServer(port, playersNumber);
    } else {
      launchClient(useCLI ? ClientMode.CLI : ClientMode.GUI, serverIP, port);
    }
    return 0;
  }

  public static void main(String[] args){
    int exitCode = new CommandLine(new Application()).execute(args);
    System.exit(exitCode);
  }
}
