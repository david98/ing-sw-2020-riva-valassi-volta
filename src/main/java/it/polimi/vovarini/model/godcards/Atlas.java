package it.polimi.vovarini.model.godcards;

//Potere (Costruzione): il giocatore può costruire un blocco di livelo 4 (cupola) sopra qualsiasi altro livello che non sia il livello 4 stesso
//Fase Influenzata: ConstructionPhase
//1) Il giocatore che possiede Atlante ha concluso la fase di movimento, viene lanciato un evento EventMovementPhaseEnded()
//2) Il controller ordina alla View di evidenziare la carta di Atlante. ? Io a sto punto proporrei un pulsante avanti se si vuole procedere senza godcard ?
//3) Se il giocatore clicca la carta, viene scatenato il bellissimo evento EventClickOnGodCard()
//4) Il controller comunica a Player il click (o a Game?, anche qui da chiarire). La classe Atlante modifica le regole ? permettendo la costruzione di cupole sui livelli 0, 1 e 2
//   oltre che sul 3. ?
public class Atlas extends GodCard {

    @Override
    public void applyEffect() {

    }

}
