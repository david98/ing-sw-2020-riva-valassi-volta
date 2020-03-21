package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Box;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.board.items.Worker;
import it.polimi.vovarini.model.godcards.GodCard;


public class Player {

    protected int movementsLeft;
    protected boolean hasMoved;
    protected boolean hasBuilt;

    protected Worker[] workers;
    protected int currentWorkerIndex;

    protected GodCard card;
    protected String nickname;
    protected Box[][] reachableBoxes;
    protected Box[][] buildableBoxes;
    protected Phase currentPhase;

    public Box[][] getReachableBoxes() {
        return reachableBoxes;
    }

    public Box[][] getBuildableBoxes() {
        return buildableBoxes;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Player (GodCard assignedCard, String nickname){

        //che valore dovrei mettere qui di default?
        movementsLeft = 0;
        hasMoved = false;
        hasBuilt = false;
        workers = new Worker[2];
        workers[0] = new Worker(Sex.Female);
        workers[1] = new Worker(Sex.Male);
        currentWorkerIndex = 0;
        card = assignedCard;
        //possibile che serva un controllo da parte di Game per vedere che il nick non sia già usato?
        this.nickname = nickname;
        currentPhase = Phase.WaitPhase;
    }

    public Worker getCurrentWorker(){
        return workers[currentWorkerIndex];
    }

    //permette al giocatore di selezionare quali dei due worker vuole utilizzare
    public Worker chooseWorker (int i){
        //serve sicuramente un controllo sull'indice, ed eventualmente un'eccezione
        currentWorkerIndex = i;
        return workers[currentWorkerIndex];
    }




}
