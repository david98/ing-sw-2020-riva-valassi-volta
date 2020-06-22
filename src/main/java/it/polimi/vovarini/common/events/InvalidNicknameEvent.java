package it.polimi.vovarini.common.events;

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
