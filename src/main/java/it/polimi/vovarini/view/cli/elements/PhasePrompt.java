package it.polimi.vovarini.view.cli.elements;

import it.polimi.vovarini.model.Phase;

public class PhasePrompt extends CLIElement {

  private Phase currentPhase;

  public PhasePrompt(Phase currentPhase) {
    this.currentPhase = currentPhase;
  }

  public void setCurrentPhase(Phase currentPhase) {
    this.currentPhase = currentPhase;
  }

  @Override
  public String render() {
    switch (currentPhase) {
      case Start -> {
        return "START - Select a Worker";
      }
      case Movement -> {
        return "MOVEMENT - Select a destination.";
      }
      case Construction -> {
        return "CONSTRUCTION - Where do you want to build?";
      }
      case End -> {
        return "END";
      }
      default -> {
        return "";
      }
    }


  }
}
