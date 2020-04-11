package it.polimi.vovarini.controller;

public class InvalidNicknameException extends Throwable {

  private int errorCode;
  public static int ERROR_DUPLICATE = 0;
  public static int ERROR_INVALID = 1;

  public InvalidNicknameException(int errorCode) {
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return errorCode;
  }
}
