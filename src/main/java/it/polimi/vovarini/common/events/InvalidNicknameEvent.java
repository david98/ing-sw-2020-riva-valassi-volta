package it.polimi.vovarini.common.events;

public class InvalidNicknameEvent extends GameEvent {

    private final int errorCode;
    private final String nickname;

    public static final int ERROR_DUPLICATE = 0;
    public static final int ERROR_INVALID = 1;

    public InvalidNicknameEvent(Object source, int errorCode, String nickname) {
        super(source);
        this.errorCode = errorCode;
        this.nickname = nickname;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getNickname() {
        return nickname;
    }
}
