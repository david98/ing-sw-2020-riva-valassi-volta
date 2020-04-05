package it.polimi.vovarini.model.godcards;



public class GodCardFactory {
  public static GodCard create(GodName name) {
    switch (name) {
      case Apollo:
        {
          return createApollo();
        }
      case Nobody:
      default:
        {
          return createNobody();
        }
    }
  }

  private static GodCard createApollo() {
    GodCard apollo = new GodCard(GodName.Apollo);
    return apollo;
  }

  private static GodCard createNobody() {
    return new GodCard(GodName.Nobody);
  }
}
