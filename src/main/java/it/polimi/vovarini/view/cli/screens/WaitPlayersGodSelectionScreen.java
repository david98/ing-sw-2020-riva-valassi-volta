package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.server.GameClient;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.Text;

public class WaitPlayersGodSelectionScreen extends Screen {
  private final Text message;

  public WaitPlayersGodSelectionScreen(ViewData data, GameClient client){
    super(data, client);
    message = new Text("Waiting for all players to choose their card...\n");
  }

  @Override
  public String render() {
    return message.render();
  }
}
