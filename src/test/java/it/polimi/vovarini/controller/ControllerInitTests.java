package it.polimi.vovarini.controller;

import it.polimi.vovarini.common.events.RegistrationEvent;
import it.polimi.vovarini.common.exceptions.InvalidNicknameException;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.Game;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControllerInitTests {

    private static Controller controller;
    private static Game game;

    @Test
    @DisplayName("Controller instantiation")
    void controllerCreation() {
        try {
            game = new Game(2);
            controller = new Controller(game);
            assertEquals(game, controller.getGame());
        } catch (InvalidNumberOfPlayersException ignored) {
        }
    }

    @BeforeEach
    public void init() {
        try {
            game = new Game(2);
            controller = new Controller(game);

        } catch (InvalidNumberOfPlayersException ignored) {
        }
    }

    @Test
    @DisplayName("login test")
    void registrationTest() {

        String nickname = "Mengi_97";
        RegistrationEvent evt = new RegistrationEvent(this, nickname);

        try {
            controller.update(evt);
        } catch (InvalidNicknameException ignored) {
        } catch (InvalidNumberOfPlayersException ignored) {
        }

        assertEquals(game.getPlayers()[0].getNickname(), nickname);

        InvalidNicknameException e;

        // invalidNickname: null nickname
        nickname = null;
        RegistrationEvent evtNullNickname = new RegistrationEvent(this, nickname);
        e = assertThrows(InvalidNicknameException.class, () -> {
            controller.update(evtNullNickname);
        });
        assertEquals(e.getErrorCode(), e.ERROR_INVALID);

        // invalidNickname: length < 4
        nickname = "o_o";
        RegistrationEvent evtInvalidLength = new RegistrationEvent(this, nickname);
        e = assertThrows(InvalidNicknameException.class, () -> {
            controller.update(evtInvalidLength);
        });
        assertEquals(e.getErrorCode(), e.ERROR_INVALID);

        // invalidNickname: length > 16
        nickname = "0123456789ABCDEF_ZZZZ";
        RegistrationEvent evtInvalidLength2 = new RegistrationEvent(this, nickname);
        e = assertThrows(InvalidNicknameException.class, () -> {
            controller.update(evtInvalidLength2);
        });
        assertEquals(e.getErrorCode(), e.ERROR_INVALID);

        // invalidNickname: special character
        nickname = "Mengi-97";
        RegistrationEvent evtInvalidNickname = new RegistrationEvent(this, nickname);
        e = assertThrows(InvalidNicknameException.class, () -> {
            controller.update(evtInvalidNickname);
        });
        assertEquals(e.getErrorCode(), e.ERROR_INVALID);

        // invalidNickname: blank character
        nickname = "Mengi 97";
        RegistrationEvent evtInvalidNickname2 = new RegistrationEvent(this, nickname);
        e = assertThrows(InvalidNicknameException.class, () -> {
            controller.update(evtInvalidNickname2);
        });
        assertEquals(e.getErrorCode(), e.ERROR_INVALID);

        // invalidNickname: duplicate
        nickname = "mEnGi_97";
        RegistrationEvent evtDuplicateNickname = new RegistrationEvent(this, nickname);
        e = assertThrows(InvalidNicknameException.class, () -> {
            controller.update(evtDuplicateNickname);
        });
        assertEquals(e.getErrorCode(), e.ERROR_DUPLICATE);

        nickname = "Valas511";
        evt = new RegistrationEvent(this, nickname);

        try {
            controller.update(evt);
        } catch (InvalidNicknameException ignored) {
        } catch (InvalidNumberOfPlayersException ignored) {
        }

        assertEquals(game.getPlayers()[1].getNickname(), nickname);

        // There is no place for you in this Game
        nickname = "xXBEN00BXx";
        RegistrationEvent evtInvalidNumberOfPlayers = new RegistrationEvent(this, nickname);

        assertThrows(InvalidNumberOfPlayersException.class, () -> {
            controller.update(evtInvalidNumberOfPlayers);
        });

        assertEquals(game.getPlayers()[0].getNickname(), "Mengi_97");
        assertEquals(game.getPlayers()[1].getNickname(), "Valas511");
        assertEquals(game.getPlayers().length, 2);
    }



}
