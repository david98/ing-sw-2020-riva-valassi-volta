package it.polimi.vovarini;

import it.polimi.vovarini.common.network.server.Server;
import it.polimi.vovarini.view.cli.GameView;
import it.polimi.vovarini.view.gui.GuiManager;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * This is the entry point for Santorini, parsing command line parameters
 * and launching either the GUI client, the CLI client or the server.
 */
@CommandLine.Command(name = "santorini", mixinStandardHelpOptions = true, version = "santorini 1.0",
        description = "Starts Santorini")
public class Application implements Callable<Integer> {

  @CommandLine.Option(names = {"-s", "--server"}, description = "Run as server")
  private boolean serverMode;
  @CommandLine.Parameters(arity = "0..1", index = "0", description = "The server hostname or IP address")
  private String serverIP = "santorini.davide.gdn";

  @CommandLine.Option(names = {"-p", "--port"}, description = "The port to connect to (or to listen on if running as server")
  private int port = Server.DEFAULT_PORT;

  @CommandLine.Option(names = {"-c", "--cli"}, description = "Run command line client")
  private boolean useCLI;


  private void launchServer(int port) throws IOException {
    Server server = new Server(port);
    Thread thread = new Thread(server);
    thread.start();
    try {
      Thread.currentThread().join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void launchClient(ClientMode mode, String serverIP, int serverPort) throws IOException {
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
    if (serverMode) {
      launchServer(port);
    } else {
      launchClient(useCLI ? ClientMode.CLI : ClientMode.GUI, serverIP, port);
    }
    return 0;
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Application()).execute(args);
    System.exit(exitCode);
  }
}
