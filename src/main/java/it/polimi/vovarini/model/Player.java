package it.polimi.vovarini.model;

import it.polimi.vovarini.model.board.Worker;
import it.polimi.vovarini.model.board.items.Sex;
import it.polimi.vovarini.model.godcards.GodCard;


public class Player {

    private int movementsLeft;
    private boolean hasMoved;
    private boolean hasBuilt;

    private Worker[] workers;
    private int currentWorkerIndex;

    private GodCard card;
    private String nickname;


    public Player (GodCard assignedCard, String nickname){

        //che valore dovrei mettere qui di default?
        this.movementsLeft = 0;
        this.hasMoved = false;
        this.hasBuilt = false;
        this.workers = new Worker[2];
        this.workers[0] = new Worker(/*Sex.Female*/);
        this.workers[1] = new Worker(/*Sex.Male*/);
        this.currentWorkerIndex = 0;
        this.card = assignedCard;
        //possibile che serva un controllo da parte di Game per vedere che il nick non sia già usato?
        this.nickname = nickname;
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
