package it.polimi.vovarini.model.godcards;

//Potere: se al tuo worker ce n'è uno dell'avversario adiacente, posso scambiare di posto SE la casella successiva è libera.
//        La pedina dell'avversario non prende il tuo posto, bensì passa alla casella successiva libera forzatamente, indipendente dal livello
//Fase Influenzata: MovementPhase (non quella dell'avversario, semplicemente modifico la posizione dell'avversario direttamente nel caso)
public class Minotaur extends GodCard {

    @Override
    public void applyEffect() {

    }

}
