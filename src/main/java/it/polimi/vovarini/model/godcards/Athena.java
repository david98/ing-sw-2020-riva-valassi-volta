package it.polimi.vovarini.model.godcards;

//Potere (Movimento): Se nel tuo turno uno dei lavoratori è salito di livello, allora nel prossimo i lavoratori dell'avversario non possono salire di livello
//Fase Influenzata: EndPhase
//1) Durante il turno del giocatore che possiede Athena, se il giocatoee fa salire di livello uno dei suoi worker scateno un evento EventWorkerLevelUp();
//2) Il controller è in ascolto, e riceve l'evento. Comunica alla View di evidenziare la carta di Athena (in modo che il giocatore sa di poterla usare)
//3) Il giocatore clicca sulla carta Athena, scatenando un evento dalla View EventClickOnGodCard()
//4) Il controller riceve l'evento e ? modifica il model impedendo all'avversario di far salire di un livello i suoi operai ?
public class Athena extends GodCard {

    public Athena(){};

    @Override
    public void applyEffect() {

    }

}
