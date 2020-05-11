package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.server.GameClient;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.Text;

public class WaitGodCardsListScreen extends Screen {

  private final Text message;

  public WaitGodCardsListScreen(ViewData data, GameClient client){
    super(data, client);
    message = new Text("Waiting for elected player to choose which God Cards will be available...\n");
  }

  @Override
  public String render() {
    return message.render();
  }
}
