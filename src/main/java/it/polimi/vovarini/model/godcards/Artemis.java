package it.polimi.vovarini.model.godcards;

//Potere (Spostamento): il worker può spostarsi una volta in più, ma gli è vietato tornare nella casella in cui è partito
//Fase Influenzata: MovementPhase
//Questo significa che posso spostare entrambi i worker? Esempio sposto uno prima e l'altro dopo? Oppure scelto un worker, sposto due volte quello punto , sempre se uso la carta?
//Procedimento nella pura ipotesi che posso fare solo con un worker
//1) Durante il turno del giocatore che possiede Artemis, il giocatore seleziona e muove uno dei suoi worker. Scateno l'evento EventWorkerHasMoved()
//2) Il controller comunica alla view di evidenziare la carta Artemis del giocatore, ? IMPEDENDO di passare alla fase di build (e se il giocatore non la vuole usare?) ?
//3) Se il giocatore clicca su Artemis, la view scatena l'evento EventClickOnGodCard()
//4) Il controller riceve l'evento e lo comunica a Player. A questo punto, mediante la classe Artemis sono modificate le regole ? ed il giocatore può muovere ulteriormente il worker che
//   ha appena mosso, con il controllo che non torni indietro ?
public class Artemis extends GodCard {

    @Override
    public void applyEffect() {

    }
}
