package it.polimi.vovarini.model.godcards;


import it.polimi.vovarini.model.board.items.Sex;

//Potere (Movimento): Se adiacenti ad uno dei worker avversari, posso scambiare di posizione il mio worker con esso.
//Fase Influenzata: MovementPhase
//1) PlayerOne possiede Apollo. All'inizio della MovementPhase, notificata dalla view tramite la NotifyNextButtonClicked(), devo controllare tramite un metodo se ci sono
//worker adiacenti appartenenti all'avversario (metodo di Game, CheckOpponentAdjacentWorkers())
// Durante il turno del giocatore che possiede Apollo, al momento della fase di movimento, ? va controllato se in caselle adiacenti sono presenti
//   worker dell'avversario  (metodo all'interno di Board? Di Game?) ?. Se sì, faccio partire l'evento EventOpponentWorkerAdjacent()
//2) Il controller riceve l'evento e comunica alla View di evidenziare la carta di Apollo. ? Serve un controllo in modo che questo venga fatto
// prima che parta la fase di Build. ?
//3) Il giocatore decide di usare Apollo, dunque scatena un evento EventClickOnGodCard()
//4) Il controller ? modifica il model permettendo come movimento lo scambio di pedine, aggiornando poi la posizione dell'avversario ?
public class Apollo extends GodCard {

    public Apollo(){
        this.sex = Sex.Male;
        this.name = GodName.Apollo;
    }

    public void applyEffect(){

    }
}
