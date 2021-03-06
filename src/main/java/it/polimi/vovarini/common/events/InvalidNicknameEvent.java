package it.polimi.vovarini.common.events;

/**
 * Represents receiving a bad nickname choice by the player
 *
 * @author Marco Riva
 * @version 0.1
 */
public class InvalidNicknameEvent extends GameEvent {

  private final int errorCode;
  private final String nickname;

  public static final int ERROR_DUPLICATE = 0;
  public static final int ERROR_INVALID = 1;

  /**
   * Builds an InvalidNicknameEvent
   * @param source is the source object of the event
   * @param errorCode is a code suggesting what kind of error has been made
   * @param nickname is the invalid nickname
   */
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
