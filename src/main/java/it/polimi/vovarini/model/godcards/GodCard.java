package it.polimi.vovarini.model.godcards;

import it.polimi.vovarini.model.board.items.Sex;

//Nota sull'ipotetico evento EventClickOnGodCard(): teoricamente andrebbe poi a ricavare la carta in questione dai dati presenti in giocatore e lì, nella classe specifica della carta,
//dovrò andare ad agire per modificare le regole. Può andare come procedimento?
public abstract class GodCard {
    protected Sex sex;
    protected GodName name;
    //possibile che vada aggiunto il famoso attributo descrizione?
    //private String desc;



    public Sex getSex() {
        return sex;
    }

    public GodName getName() {
        return name;
    }

    //Metodo astratto poi specificato da tutte le sottoclassi
    public abstract void applyEffect();
}
