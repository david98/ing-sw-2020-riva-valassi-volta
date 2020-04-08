package it.polimi.vovarini.controller;

import it.polimi.vovarini.controller.events.WorkerSelectionEvent;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.Phase;
import it.polimi.vovarini.model.board.items.Sex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Controller Tests")
public class ControllerTests {

    private static Controller controller;

    @Test
    @DisplayName("Controller Instance")
    void controllerCreation(){
        Game game = null;
        try {
            game = new Game(2);
        }
        catch (InvalidNumberOfPlayersException ignored){

        }
        controller = new Controller(game);
        assertEquals(game, controller.getGame());
    }

    @Test
    @DisplayName("Worker Selection due to a WorkerSelectionEvent")
    void workerSelection(){

        Game game = null;
        try {
            game = new Game(2);
        }
        catch (InvalidNumberOfPlayersException ignored){

        }

        controller = new Controller(game);
        WorkerSelectionEvent evtF = new WorkerSelectionEvent(this, game.getCurrentPlayer(), Sex.Female);
        try {
            controller.update(evtF);
        }
        catch (InvalidPhaseException e){
            assertNotEquals(game.getCurrentPhase(), Phase.Start);
        }
        assertEquals(game.getCurrentPlayer().getCurrentWorker().getSex(), Sex.Female);

        WorkerSelectionEvent evtM = new WorkerSelectionEvent(this, game.getCurrentPlayer(), Sex.Male);
        try {
            controller.update(evtM);
        }
        catch (InvalidPhaseException e){
            assertNotEquals(game.getCurrentPhase(), Phase.Start);
        }
        assertEquals(game.getCurrentPlayer().getCurrentWorker().getSex(), Sex.Male);

        

    }

}
