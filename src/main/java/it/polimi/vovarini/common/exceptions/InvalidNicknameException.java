package it.polimi.vovarini.common.exceptions;

public class InvalidNicknameException extends Exception {

  private final int errorCode;
  public static final int ERROR_DUPLICATE = 0;
  public static final int ERROR_INVALID = 1;

  public InvalidNicknameException(int errorCode) {
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return errorCode;
  }
}
