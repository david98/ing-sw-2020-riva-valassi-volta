package it.polimi.vovarini.view.cli.screens;

import it.polimi.vovarini.server.GameClient;
import it.polimi.vovarini.view.ViewData;
import it.polimi.vovarini.view.cli.elements.Text;

public class WaitScreen extends Screen{
  private final Text message;

  public WaitScreen(ViewData data, GameClient client, String message){
    super(data, client);
    this.message = new Text(message + "\n");
  }

  @Override
  public String render() {
    return message.render();
  }
}
