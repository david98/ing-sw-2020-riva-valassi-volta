package it.polimi.vovarini.model.godcards;

//Potere: Se il worker non sale di livello, può costruire sia prima che dopo essere stato mosso
//Fase Influenzata: questo è difficile. Essenzialmente salto la MovementPhase e vado direttamente in ConstructionPhase.
//Se costruisce, allora ripasso alla MovementPhase impedendo la salita.
//Se non costruisce (Skip Button), ripasso alla MovementPhase che rimane normale.
//In ogni caso poi torno alla Construction Phase
//Dall'analisi, potremmo dire che influenza direttamente la ApplyGCPhase, in quanto cambia l'ordine delle fasi successive
public class Prometheus extends GodCard {

    @Override
    public void applyEffect() {

    }

}
