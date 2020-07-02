package it.polimi.vovarini.view.cli.elements;

/**
 * A simple string of text.
 *
 * @author Davide Volta
 */
public class Text extends CLIElement {

  private String content;

  public Text(String content) {
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
