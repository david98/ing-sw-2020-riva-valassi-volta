package it.polimi.vovarini.common.events;

import it.polimi.vovarini.model.board.items.Worker;

public class ChangeWorkerEvent extends GameEvent {

    private final Worker workerToSelect;

    public ChangeWorkerEvent (Object source, Worker toSelect){
        super(source);
        workerToSelect = toSelect;
    }

    public Worker getWorkerToSelect() {
        return workerToSelect;
    }
}
