package it.polimi.vovarini.view.cli.elements;

public class Text extends CLIElement {

  private String content;

  public Text(String content){
    this.content = content;
  }
  @Override
  public String render() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
