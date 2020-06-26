package it.polimi.vovarini.common.events;

/**
 * Represents receiving a bad nickname choice by the player
 *
 * @author Marco Riva
 *
 * @version 0.1
 */
public class InvalidNicknameEvent extends GameEvent {

    private final String nickname;

    public InvalidNicknameEvent(Object source, String nickname) {
        super(source);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
