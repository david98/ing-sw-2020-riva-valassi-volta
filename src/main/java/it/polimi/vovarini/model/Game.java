package it.polimi.vovarini.model;

import it.polimi.vovarini.common.events.*;
import it.polimi.vovarini.common.exceptions.InvalidNumberOfPlayersException;
import it.polimi.vovarini.model.board.Board;
import it.polimi.vovarini.model.godcards.GodCard;
import it.polimi.vovarini.model.godcards.GodCardFactory;
import it.polimi.vovarini.model.godcards.GodName;
import it.polimi.vovarini.model.moves.Construction;
import it.polimi.vovarini.model.moves.Move;
import it.polimi.vovarini.model.moves.Movement;

import java.io.Serializable;
import java.util.*;

public class Game implements Serializable, GameDataAccessor {

  public static final int MIN_PLAYERS = 2;
  public static final int MAX_PLAYERS = 3;

  private final Player[] players;
  private int currentPlayerIndex;

  private Phase currentPhase;

  private final Board board;

  private final Stack<Move> moves;
  private final Stack<Move> undoneMoves;

  private GodName[] availableGodCards;

  private boolean setupComplete;

  private final Random random;

  public Game(int numberOfPlayers) throws InvalidNumberOfPlayersException {
    if (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
      throw new InvalidNumberOfPlayersException();
    }

    players = new Player[numberOfPlayers];
    availableGodCards = new GodName[numberOfPlayers];

    currentPlayerIndex = 0;

    moves = new Stack<>();
    undoneMoves = new Stack<>();

    board = new Board(Board.DEFAULT_SIZE);

    currentPhase = Phase.Start;

    setupComplete = false;

    random = new Random();
  }

  public Board getBoard() {
    return board;
  }

  /**
   * This method adds a new player into the game with the nickname already
   * validated through {@link Player#validateNickname(String)}
   *
   * @param nickname the name of the player to be added
   * @throws InvalidNumberOfPlayersException if there is already the maximum number of players
   */
  public void addPlayer(String nickname) {

    if (players[players.length - 1] != null) {
      throw new InvalidNumberOfPlayersException();
    }

    Player player = new Player(nickname);

    for (int i = 0; i < players.length; i++) {
      if (players[i] == null) {
        players[i] = player;
        GameEventManager.raise(new NewPlayerEvent(this, player));
        return;
      }
    }
  }

  public void drawElectedPlayer() {
    currentPlayerIndex = random.nextInt(players.length);
  }

  // se è rimasta solo una carta, la assegna, altrimenti chiede al prossimo giocatore la carta che vuole (tra quelle rimaste)
  public void setupGodCards() {

    nextPlayer();

    if(availableGodCards.length == 1) {
      GodCard lastGodCard = GodCardFactory.create(availableGodCards[0]);
      lastGodCard.setGameData(this);
      getCurrentPlayer().setGodCard(lastGodCard);
      GameEventManager.raise(new CardAssignmentEvent(this, getCurrentPlayer(), lastGodCard));
      LinkedList<GodName> availableGods = new LinkedList<>(Arrays.asList(availableGodCards));
      availableGods.remove(lastGodCard.getName());
      availableGodCards = availableGods.toArray(GodName[]::new);

      // settare currentPlayer a players[0], oppure potremmo proseguire il turno da qui, lasciando invariato il codice attuale
      GameEventManager.raise(new PlaceYourWorkersEvent(this, getCurrentPlayer()));
    } else {
      GameEventManager.raise(new SelectYourCardEvent(this, getCurrentPlayer(), availableGodCards));
    }
  }

  public void performMove(Movement move) {

    undoneMoves.clear();
    moves.push(move);
    getCurrentPlayer().getMovementList().add(move);
    GameEventManager.raise(new PlayerInfoUpdateEvent(this, getCurrentPlayer()));
    boolean isMovementWinning = getCurrentPlayer().getGodCard().isMovementWinning(move);

    for(Movement executableMove : getCurrentPlayer().getGodCard().consequences(move, this)) {
      Movement temp = new Movement(board, executableMove.getStart(), executableMove.getEnd());
      temp.execute();
      //executableMove.execute();
      //JDK ti odio più di sistemi informativi
    }

    if(isMovementWinning) {
      GameEventManager.raise(new VictoryEvent(this, getCurrentPlayer()));
    }

  }

  public void performMove(Construction move){

    undoneMoves.clear();
    moves.push(move);
    getCurrentPlayer().getConstructionList().add(move);
    GameEventManager.raise(new PlayerInfoUpdateEvent(this, getCurrentPlayer()));

    for(Move executableMove : getCurrentPlayer().getGodCard().consequences(move, this)){
      executableMove.execute();
    }

  }

  public Phase getCurrentPhase() {
    return currentPhase;
  }

  public Player[] getPlayers() {
    return players;
  }

  public Player getCurrentPlayer() {
    return players[currentPlayerIndex];
  }

  public GodName[] getAvailableGodCards() {
    return availableGodCards;
  }

  public void setAvailableGodCards(GodName[] availableGodCards) {
    this.availableGodCards = availableGodCards;
  }

  public void nextPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    GameEventManager.raise(new CurrentPlayerChangedEvent(this, players[currentPlayerIndex]));
  }

  public void setCurrentPhase(Phase phase){
    this.currentPhase = phase;
    GameEventManager.raise(new PhaseUpdateEvent(this, phase));
  }

  // needs to manage turn flow
  public void undoLastMove() {
    try {
      Move opposite = moves.pop().reverse();
      undoneMoves.push(opposite);
      opposite.execute();
    } catch (EmptyStackException ignored) {

    }
  }

  public void redoMove() {
    try {
      Move move = undoneMoves.pop().reverse();
      moves.push(move);
      move.execute();
    } catch (EmptyStackException ignored) {

    }
  }

  public void start(){
    setupComplete = true;
    for (Player p: players){
      p.setWorkerSelected(false);
    }
    GameEventManager.raise(new GameStartEvent(this, this.getPlayers()));
  }

  public boolean isSetupComplete() {
    return setupComplete;
  }

  public boolean isFull(){
    return Arrays.stream(players).noneMatch(Objects::isNull);
  }

  public boolean isAvailableCardsAlreadySet() { return Arrays.stream(availableGodCards).noneMatch(Objects::isNull); }
}
