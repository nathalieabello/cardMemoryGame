import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.FontStyle;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.ScaleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

class Board {
  int pairs;
  ArrayList<Card> cards = new ArrayList<Card>();
  int x;
  int y;
  Random rand = new Random();
  ArrayList<Card> cardList;
  double scale;
  ArrayList<Card> flippedCardsList = new ArrayList<Card>();
  ArrayList<Card> goneCardsList = new ArrayList<Card>();

  Board(int pairs) {
    this.pairs = pairs;
    cardList = this.shufflePairs();
    if (this.pairs <= 4) { // 8 cards -- 4 x 2
      this.y = 2;
      this.scale = 1.4;
    } else if (this.pairs <= 9) { // 18 cards-- 6 x 3
      this.y = 3;
      this.scale = .9;
    } else if (this.pairs <= 16) { // 32 cards -- 8 x 4
      this.y = 4;
      this.scale = .7;
    } else if (this.pairs <= 28) { // 56 cards -- 12 x 5
      this.y = 5;
      this.scale = .55;
    } else { //104 cards -- 15 x 7 (52 pairs)
      this.y = 7;
      this.scale = .4;
    } 
    if (Math.floorMod(this.pairs * 2, this.y) == 0) {
      this.x = ((this.pairs * 2) / this.y);
    } else {
      this.x = Math.floorDiv((this.pairs * 2), this.y) + 1;
    }
  }

  Board(int pairs, Random rand) {
    this.pairs = pairs;
    this.rand = rand;
    cardList = this.shufflePairs();
    if (this.pairs <= 4) { // 8 cards -- 4 x 2
      this.y = 2;
      this.scale = 1.4;
    } else if (this.pairs <= 9) { // 18 cards-- 6 x 3
      this.y = 3;
      this.scale = .9;
    } else if (this.pairs <= 16) { // 32 cards -- 8 x 4
      this.y = 4;
      this.scale = .7;
    } else if (this.pairs <= 28) { // 56 cards -- 12 x 5
      this.y = 5;
      this.scale = .55;
    } else { //104 cards -- 15 x 7 (52 pairs)
      this.y = 7;
      this.scale = .4;
    } 
    if (Math.floorMod(this.pairs * 2, this.y) == 0) {
      this.x = ((this.pairs * 2) / this.y);
    } else {
      this.x = Math.floorDiv((this.pairs * 2), this.y) + 1;
    }
  }

  // produces the list of all possible suit & number combinations
  public ArrayList<Card> allCards() {
    ArrayList<Card> workingList = new ArrayList<Card>();
    int n = 1;
    while (n <= 13) { // all numbers
      for (int i = 0; i < 4; i = i + 1) { // all suits
        workingList.add(new Card(i, n));
      }
      n = n + 1;
    }
    return workingList;
  }

  // produces a random list of cards with the given length
  public ArrayList<Card> randomCards() {
    ArrayList<Card> fullList = new ArrayList<Card>(this.allCards());
    ArrayList<Card> workingList = new ArrayList<Card>();
    int n = this.pairs;
    while(n > 0) {
      Card addCard = fullList.remove(this.rand.nextInt(fullList.size()));
      workingList.add(addCard);
      n = n - 1;
    }
    return workingList; 
  }

  // adds the pairs & shuffles the cards in this list
  public ArrayList<Card> shufflePairs() {
    ArrayList<Card> workingList = 
        new ArrayList<Card>(this.randomCards());
    workingList = this.cloneAndAdd(workingList);
    ArrayList<Card> newList = new ArrayList<Card>();
    while (newList.size() < (this.pairs * 2)) { // shuffles list
      Card toAdd = 
          workingList.remove(this.rand.nextInt(workingList.size()));
      newList.add(toAdd);
    }
    return newList;
  }

  // returns the given list, duplicated onto itself
  ArrayList<Card> cloneAndAdd(ArrayList<Card> list) {
    ArrayList<Card> newList = new ArrayList<Card>(list);
    for (Card item: list) {
      Card cardToAdd = item.deepCopy();
      newList.add(cardToAdd);
    }
    return newList;
  }

  // draws the cards on the given worldScene
  public WorldScene drawCards(WorldScene soFar, int cardInDeck, int xCoord, int yCoord) {
    if (cardInDeck < (this.pairs * 2) - 1) {
      cardInDeck = cardInDeck + 1;
      ICard card = this.cardList.get(cardInDeck);
      WorldImage cardImg = new ScaleImage(card.draw(), this.scale);
      if ((!(cardInDeck == 0)) && (cardInDeck % this.y) == 0) {
        // next column
        xCoord += (200 * this.scale);
        yCoord = (int) (150 * this.scale);
        soFar = this.placeCard(this.drawCards(soFar, cardInDeck, xCoord, yCoord),
            cardImg, xCoord, yCoord);
      } else {
        // add to column
        yCoord += (300 * this.scale);
        soFar = this.placeCard(this.drawCards(soFar, cardInDeck, xCoord, yCoord),
            cardImg, xCoord, yCoord);
      }
    } else {
      return soFar;
    }
    return soFar;
  }

  // places the given card on the given WorldScene at the given coordinates
  WorldScene placeCard(WorldScene worldSoFar, WorldImage card, int xCoord, int yCoord) {
    return worldSoFar.placeImageXY(card, xCoord, yCoord);
  }

  // flips the card with the closest given position
  public void flipCard(Posn pos) {
    ICard cardCheck = this.getCard(pos);
    if (cardCheck instanceof NoCard) {
    } else {
      Card cardToFlip = (Card) this.getCard(pos);
      if (flippedCardsList.contains(cardToFlip)) {
        // do nothing if already flipped
      } else {
        cardToFlip.covered = false;
        this.flippedCardsList.add(cardToFlip);
      }
    }
  }

  // flips the card back to be covered
  // removes the cards from the flippedCards list
  public void flipBack() {
    for (ICard card : this.flippedCardsList) {
      card.cover();
    }
    this.flippedCardsList.clear();
  }

  // determines which card to flip given a position
  ICard getCard(Posn pos) {
    int xScale;
    int yScale;
    int row;
    int col;
    if (this.scale == 1.4) {
      xScale = 150;
      yScale = -210;
    } else if (this.scale == .9) {
      xScale = 90;
      yScale = -135;
    } else if (this.scale == .7) {
      xScale = 70;
      yScale = -105;
    } else if (this.scale == .55) {
      xScale = 55;
      yScale = -83;
    } else {
      xScale = 40;
      yScale = -60;
    } 
    row = pos.x - xScale;
    row = (int) Math.round(row / (200 * this.scale));

    col = pos.y - yScale;
    col = (int) Math.round(col / (300 * this.scale));

    int cardNum = (row * this.y) + col - 1;

    if (cardNum <= (this.pairs * 2)) {
      return this.cardList.get(cardNum);
    } else {
      return new NoCard();
    }
  }

  // checks if the two uncovered cards match
  public boolean pairCheck() {
    return this.flippedCardsList.get(0)
        .sameCard(this.flippedCardsList.get(1));
  }

  // turns the cards that are flipped into NoCards
  public void removePair() {
    for (int i = 0; i < 2; i = i + 1) {
      Card thisCard = this.flippedCardsList.get(i);
      thisCard.gone = true;
      this.goneCardsList.add(thisCard);
    }
    this.flippedCardsList.clear();
  }
}


// world class --> to make the game interactive
class GameWorld extends World {
  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  int worldWidth = screenSize.width;
  int worldHeight = screenSize.height - 50;
  boolean playing = false;
  Board b;
  double scale;
  int xScale;
  int yScale;
  boolean gameWon = false;
  int pairsMatched = 0;
  int numPairs;
  String msg = "";
  boolean clickMode = true;
  int timer = 0;
  int tick = 0;
  ArrayList<Card> flippedList;
  WorldImage timerImg;
  WorldImage pairsImg;
  TextImage msgImg;

  // cardWorld constructor
  GameWorld(Board b) {
    this.b = b;
    this.scale = this.b.scale;
    this.numPairs = this.b.pairs;
    this.flippedList = this.b.flippedCardsList;
    this.pairsMatched = this.b.goneCardsList.size();
    if (this.scale == 1.4) {
      this.xScale = 150;
      this.yScale = -210;
    } else if (this.scale == .9) {
      this.xScale = 90;
      this.yScale = -135;
    } else if (this.scale == .7) {
      this.xScale = 70;
      this.yScale = -105;
    } else if (this.scale == .55) {
      this.xScale = 55;
      this.yScale = -83;
    } else {
      this.xScale = 40;
      this.yScale = -60;
    }
  }

  // creates the visuals for the user to see
  public WorldScene makeScene() {
    pairsMatched = this.b.goneCardsList.size() / 2;
    timerImg = 
        new TextImage(Time.valueOf("0:00:" + timer).toString(), 40, FontStyle.BOLD, Color.white);
    pairsImg = 
        new TextImage(this.pairsMatched + "/" + this.numPairs, 40, FontStyle.BOLD, Color.white);
    msgImg = 
        new TextImage(msg, 128, FontStyle.BOLD, Color.white);
    WorldScene backdrop = new WorldScene(worldWidth, worldHeight);
    WorldScene background = backdrop.placeImageXY(new RectangleImage(worldWidth, worldHeight, 
        OutlineMode.SOLID, Color.green.darker().darker().darker()), 
        worldWidth / 2, worldHeight / 2)
        .placeImageXY(timerImg, 1300, 50)
        .placeImageXY(pairsImg, 1300, 100);
    WorldScene cards = this.b.drawCards(background, -1, this.xScale, this.yScale)
        .placeImageXY(msgImg, this.worldWidth / 2, this.worldHeight / 2);
    return cards;
  }

  // controls the user's clicks
  public World onMouseClicked(Posn pos) {
    // if the game is over return last scene
    if (pairsMatched == numPairs) {
      msg = "Good job!";
      this.clickMode = false;
      this.gameWon = true;
    } else if (clickMode) { 
      // in click mode & still have more pairs to find
      this.b.flipCard(pos);
    } else {
      this.clickMode = true;
    }
    return this;
  }

  // controls the game's ticks
  public World onTick() {
    // if the game is over
    if (pairsMatched == numPairs) {
      msg = "Good job!";
      this.gameWon = true;
    } else {
      // game is not over
      if (tick % 10 == 0) {
        timer = timer + 1;
      }
      tick = tick + 1;
      if (this.flippedList.size() == 2) {
        try {
          Thread.sleep(800);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
        // two cards are flipped
        if (this.b.pairCheck()) {
          // they are pairs
          this.pairsMatched = this.pairsMatched + 1;
          this.b.removePair();
        } else {
          // they are not pairs
          this.b.flipBack();
        }
      } else {
        // still need to keep clicking
        this.clickMode = true;
      }
    }
    return this;
  }
}

class ExamplesBoard {
  ArrayList<Card> allCards; 
  GameWorld world;
  Board board;
  GameWorld realWorld;
  Board realBoard;
  int worldWidth;
  int worldHeight;
  WorldScene backdrop;
  WorldScene background;
  GameWorld easyWorld;
  Board easyBoard;

  // initializes all the variables
  void initData() {
    allCards = new ArrayList<Card>(); 
    allCards.add(new Card(0, 1));
    allCards.add(new Card(1, 1));
    allCards.add(new Card(2, 1));
    allCards.add(new Card(3, 1));
    allCards.add(new Card(0, 2));
    allCards.add(new Card(1, 2));
    allCards.add(new Card(2, 2));
    allCards.add(new Card(3, 2));
    allCards.add(new Card(0, 3));
    allCards.add(new Card(1, 3));
    allCards.add(new Card(2, 3));
    allCards.add(new Card(3, 3));
    allCards.add(new Card(0, 4));
    allCards.add(new Card(1, 4));
    allCards.add(new Card(2, 4));
    allCards.add(new Card(3, 4));
    allCards.add(new Card(0, 5));
    allCards.add(new Card(1, 5));
    allCards.add(new Card(2, 5));
    allCards.add(new Card(3, 5));
    allCards.add(new Card(0, 6));
    allCards.add(new Card(1, 6));
    allCards.add(new Card(2, 6));
    allCards.add(new Card(3, 6));
    allCards.add(new Card(0, 7));
    allCards.add(new Card(1, 7));
    allCards.add(new Card(2, 7));
    allCards.add(new Card(3, 7));
    allCards.add(new Card(0, 8));
    allCards.add(new Card(1, 8));
    allCards.add(new Card(2, 8));
    allCards.add(new Card(3, 8));
    allCards.add(new Card(0, 9));
    allCards.add(new Card(1, 9));
    allCards.add(new Card(2, 9));
    allCards.add(new Card(3, 9));
    allCards.add(new Card(0, 10));
    allCards.add(new Card(1, 10));
    allCards.add(new Card(2, 10));
    allCards.add(new Card(3, 10));
    allCards.add(new Card(0, 11));
    allCards.add(new Card(1, 11));
    allCards.add(new Card(2, 11));
    allCards.add(new Card(3, 11));
    allCards.add(new Card(0, 12));
    allCards.add(new Card(1, 12));
    allCards.add(new Card(2, 12));
    allCards.add(new Card(3, 12));
    allCards.add(new Card(0, 13));
    allCards.add(new Card(1, 13));
    allCards.add(new Card(2, 13));
    allCards.add(new Card(3, 13));

    board = new Board(52, new Random(0));
    world = new GameWorld(this.board);

    realBoard = new Board(52);
    realWorld = new GameWorld(this.realBoard);

    easyBoard = new Board(4, new Random(0));
    easyWorld = new GameWorld(this.easyBoard);

    worldWidth = 1000;
    worldHeight = 1000;
    backdrop = new WorldScene(worldWidth, worldHeight);
    background = backdrop.placeImageXY(new RectangleImage(worldWidth, worldHeight, 
        OutlineMode.SOLID, Color.green.darker().darker().darker()), 
        worldWidth / 2, worldHeight / 2);
  }

  // visualizes the cards on the grid
  void testMakeGrid(Tester t) {
    this.initData();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int worldWidth = screenSize.width;
    int worldHeight = screenSize.height - 50;
    this.easyWorld.bigBang(worldWidth, worldHeight, 0.5);
  }

  // tests the board dimensions
  boolean testDimensions(Tester t) {
    this.initData();
    return t.checkExpect(new Board(1).x, 1)
        && t.checkExpect(new Board(1).y, 2)
        && t.checkExpect(new Board(5).x, 4)
        && t.checkExpect(new Board(5).y, 3)
        && t.checkExpect(new Board(12).x, 6)
        && t.checkExpect(new Board(12).y, 4)
        && t.checkExpect(new Board(20).x, 8)
        && t.checkExpect(new Board(20).y, 5)
        && t.checkExpect(new Board(32).x, 10)
        && t.checkExpect(new Board(32).y, 7)
        && t.checkExpect(new Board(51).x, 15)
        && t.checkExpect(new Board(51).y, 7)
        && t.checkExpect(new Board(17).x, 7)
        && t.checkExpect(new Board(17).y, 5);
  }

  // tests the gameWorld's fields
  boolean testGameWorld(Tester t) {
    this.initData();
    return t.checkExpect(new GameWorld(new Board(1)).scale, 1.4)
        && t.checkExpect(new GameWorld(new Board(5)).scale, .9)
        && t.checkExpect(new GameWorld(new Board(12)).scale, .7)
        && t.checkExpect(new GameWorld(new Board(20)).scale, .55)
        && t.checkExpect(new GameWorld(new Board(32)).scale, .4)
        && t.checkExpect(new GameWorld(new Board(52)).scale, .4)
        && t.checkExpect(new GameWorld(new Board(17)).scale, .55)
        && t.checkExpect(new GameWorld(new Board(36)).scale, .4)
        && t.checkExpect(new GameWorld(new Board(18)).scale, .55)
        && t.checkExpect(new GameWorld(new Board(10)).scale, .7)
        && t.checkExpect(new GameWorld(new Board(1, new Random(0))).scale, 1.4)
        && t.checkExpect(new GameWorld(new Board(5, new Random(1))).scale, .9)
        && t.checkExpect(new GameWorld(new Board(12, new Random(2))).scale, .7)
        && t.checkExpect(new GameWorld(new Board(20, new Random(3))).scale, .55)
        && t.checkExpect(new GameWorld(new Board(32, new Random(4))).scale, .4)
        && t.checkExpect(new GameWorld(new Board(52, new Random(10000))).scale, .4)
        && t.checkExpect(new GameWorld(new Board(17, new Random(2))).scale, .55)
        && t.checkExpect(new GameWorld(new Board(36, new Random(0))).scale, .4)
        && t.checkExpect(new GameWorld(new Board(18, new Random(1))).scale, .55)
        && t.checkExpect(new GameWorld(new Board(10, new Random(0))).scale, .7);
  }

  // tests the allCards method
  boolean testAllCards(Tester t) {
    this.initData();
    return t.checkExpect(new Board(2).allCards(), this.allCards)
        && t.checkExpect(new Board(6).allCards(), this.allCards)
        && t.checkExpect(new Board(52).allCards(), this.allCards)
        && t.checkExpect(new Board(5, new Random(0)).allCards(), this.allCards);
  }

  // tests the randomCards method
  void testRandomCards(Tester t) {
    this.initData();
    ArrayList<Card> list = new ArrayList<Card>();
    ArrayList<Card> list1 = new ArrayList<Card>();
    ArrayList<Card> list2 = new ArrayList<Card>();
    ArrayList<Card> list3 = new ArrayList<Card>();
    t.checkExpect(new Board(0, new Random(0)).randomCards(), list);
    list.add(new Card(3, 12));
    t.checkExpect(new Board(1, new Random(0)).randomCards(), list);
    list1.add(new Card(1, 6));
    list1.add(new Card(0, 7));
    list1.add(new Card(1, 12));
    list1.add(new Card(1, 9));
    list1.add(new Card(0, 13));
    list1.add(new Card(3, 12));
    t.checkExpect(new Board(6, new Random(0)).randomCards(), list1);
    list2.add(new Card(1, 13));
    list2.add(new Card(3, 3));
    list2.add(new Card(3, 10));
    list2.add(new Card(1, 10));
    list2.add(new Card(1, 9));
    list2.add(new Card(2, 10));
    list2.add(new Card(3, 13));
    list2.add(new Card(1, 6));
    list2.add(new Card(0, 12));
    list2.add(new Card(1, 4));
    list2.add(new Card(0, 13));
    list2.add(new Card(2, 13));
    t.checkExpect(new Board(12, new Random(0)).randomCards(), list2);
    list3.add(new Card(1, 11));
    list3.add(new Card(3, 5));
    list3.add(new Card(0, 1));
    list3.add(new Card(1, 9));
    list3.add(new Card(2, 10));
    list3.add(new Card(2, 4));
    list3.add(new Card(2, 6));
    list3.add(new Card(1, 5));
    list3.add(new Card(3, 12));
    list3.add(new Card(3, 11));
    t.checkExpect(new Board(10, new Random(1)).randomCards(), list3);
  }

  // tests the shufflePairs method
  void testShufflePairs(Tester t) {
    this.initData();
    ArrayList<Card> list = new ArrayList<Card>();
    ArrayList<Card> list2 = new ArrayList<Card>();
    t.checkExpect(new Board(0, new Random(0)).shufflePairs(), 
        list);
    list.add(new Card(0, 1));
    list.add(new Card(2, 10));
    list.add(new Card(2, 7));
    list.add(new Card(2, 4));
    list.add(new Card(1, 3));
    t.checkExpect(new Board(5, new Random(0)).randomCards(), 
        list);
    t.checkExpect(new Board(5, new Random(0)).randomCards().size(), 
        5);
    t.checkExpect(new Board(5, new Random(0)).randomCards().size(), 
        5);
    list2.add(new Card(2, 4));
    list2.add(new Card(0, 1));
    list2.add(new Card(2, 7));
    list2.add(new Card(2, 7));
    list2.add(new Card(0, 1));
    list2.add(new Card(2, 10));
    list2.add(new Card(1, 3));
    list2.add(new Card(2, 4));
    list2.add(new Card(1, 3));
    list2.add(new Card(2, 10));
    t.checkExpect(new Board(5, new Random(0)).shufflePairs(), 
        list2);
    t.checkExpect(new Board(5, new Random(0)).shufflePairs().size(), 
        10);
    t.checkExpect(new Board(3).shufflePairs().size(), 
        6);
  }

  // tests the cloneAndAdd method
  void testCloneAndAdd(Tester t) {
    this.initData();
    ArrayList<Card> list = new ArrayList<Card>();
    ArrayList<Card> list2 = new ArrayList<Card>();
    ArrayList<Card> list3 = new ArrayList<Card>();
    t.checkExpect(this.board.cloneAndAdd(new ArrayList<Card>()), list);
    list.add(new Card(1, 5));
    list2.add(new Card(1, 5));
    list2.add(new Card(1, 5));
    t.checkExpect(this.board.cloneAndAdd(list).size(), 2);
    list.add(new Card(0, 5));
    list.add(new Card(3, 13));
    list2.clear();
    list2.add(new Card(1,5));
    list2.add(new Card(0,5));
    list2.add(new Card(3, 13));
    list2.add(new Card(1,5));
    list2.add(new Card(0,5));
    list2.add(new Card(3, 13));
    t.checkExpect(this.board.cloneAndAdd(list).size(), 6);
    t.checkExpect(this.board.cloneAndAdd(list), list2);
    list3.add(new Card(1,5));
    list3.add(new Card(0,5));
    list3.add(new Card(3, 13));
    list3.add(new Card(1,5));
    list3.add(new Card(0,5));
    list3.add(new Card(3, 13));
    list3.add(new Card(1,5));
    list3.add(new Card(0,5));
    list3.add(new Card(3, 13));
    list3.add(new Card(1,5));
    list3.add(new Card(0,5));
    list3.add(new Card(3, 13));
    t.checkExpect(this.board.cloneAndAdd(list2), list3);
  }

  // draws the cards on the given worldScene
  void testDrawCards(Tester t) {
    this.initData();
    Board testBoard = new Board(1, new Random(0));
    WorldScene cards = testBoard.placeCard(testBoard.drawCards(background, 0, 150, 210),
        new ScaleImage(new Card(2, 12).draw(), 1.4), 150, 210);
    t.checkExpect(testBoard.drawCards(background, -1, 150, -210), cards);
    testBoard = new Board(2, new Random(0));
    cards = testBoard.placeCard(testBoard.drawCards(cards, 0, 150, 210),
        new ScaleImage(new Card(2, 12).draw(), 1.4), 150, 210);
    t.checkExpect(testBoard.drawCards(background, -1, 150, -210), cards);
  }

  // places the given card on the given WorldScene at the given coordinates
  void testPlaceCard(Tester t) {
    this.initData();
    t.checkExpect(this.board.placeCard(background, new Card(0, 1).draw(), 0, 0), 
        background.placeImageXY(new Card(0, 1).draw(), 0, 0));
    t.checkExpect(this.board.placeCard(background, new Card(2, 4).draw(), 0, 0), 
        background.placeImageXY(new Card(2, 4).draw(), 0, 0));
    t.checkExpect(this.board.placeCard(background, new Card(1, 13).draw(), 0, 0), 
        background.placeImageXY(new Card(1, 13).draw(), 0, 0));
    t.checkExpect(this.board.placeCard(background, new Card(3, 7).draw(), 0, 0), 
        background.placeImageXY(new Card(3, 7).draw(), 0, 0));
  }

  // tests the method flipCard and the method flipBack
  void testFlipCardAndFlipBack(Tester t) {
    this.initData();
    t.checkExpect(this.board.cardList.get(0).isCovered(), true);
    t.checkExpect(this.board.cardList.get(1).isCovered(), true);
    this.board.flipCard(new Posn(44, 63));
    t.checkExpect(this.board.cardList.get(0).isCovered(), false);
    this.board.flipCard(new Posn(44, 120));
    t.checkExpect(this.board.cardList.get(1).isCovered(), false);
    this.board.flipBack();
    t.checkExpect(this.board.cardList.get(0).isCovered(), true);
    t.checkExpect(this.board.cardList.get(1).isCovered(), true);
  }

  // tests the onMouseClick method
  void testOnMouseClick(Tester t) {
    this.initData();
    // card 0
    this.easyWorld.onMouseClicked(new Posn(150, 192));
    t.checkExpect(this.easyWorld.b.cardList.get(0).isCovered(), false);
    Card toDraw = new Card(0, 8);
    toDraw.covered = false;
    t.checkExpect(this.easyWorld.b.cardList.get(0).draw(), 
        toDraw.draw());

    // card 1
    this.easyWorld.onMouseClicked(new Posn(150, 600));
    t.checkExpect(this.easyWorld.b.cardList.get(1).isCovered(), false);
    toDraw = new Card(0, 12);
    toDraw.covered = false;
    t.checkExpect(this.easyWorld.b.cardList.get(1).draw(), 
        toDraw.draw());
    
    t.checkExpect(this.easyWorld.flippedList.size(), 2);

    this.easyWorld.onTick();
    t.checkExpect(this.easyWorld.timer, 1);
    t.checkExpect(this.easyWorld.flippedList.size(), 0);
    this.easyWorld.onMouseClicked(new Posn(150, 600));
    t.checkExpect(this.easyWorld.b.cardList.get(0).isCovered(), true);
    this.easyWorld.onMouseClicked(new Posn(150, 600));
    t.checkExpect(this.easyWorld.b.cardList.get(1).isCovered(), false);
  }
  
  // tests the getCard method
  void testGetCard(Tester t) {
    this.initData();
    t.checkExpect(this.easyBoard.getCard(new Posn(200, 200)), 
        this.easyWorld.b.cardList.get(0));
    t.checkExpect(this.easyBoard.getCard(new Posn(200, 600)), 
        this.easyWorld.b.cardList.get(1));
    t.checkExpect(this.easyBoard.getCard(new Posn(500, 200)), 
        this.easyWorld.b.cardList.get(2));
    t.checkExpect(this.easyBoard.getCard(new Posn(500, 600)), 
        this.easyWorld.b.cardList.get(3));
    t.checkExpect(this.easyBoard.getCard(new Posn(800, 200)), 
        this.easyWorld.b.cardList.get(4));
    t.checkExpect(this.easyBoard.getCard(new Posn(800, 600)), 
        this.easyWorld.b.cardList.get(5));
    t.checkExpect(this.easyBoard.getCard(new Posn(1100, 200)), 
        this.easyWorld.b.cardList.get(6));
    t.checkExpect(this.easyBoard.getCard(new Posn(1100, 600)), 
        this.easyWorld.b.cardList.get(7));
  }
  
  // tests the pairCheck method
  void testPairCheck(Tester t) {
    this.initData();
    this.easyBoard.flipCard(new Posn(500, 600)); // 3
    this.easyBoard.flipCard(new Posn(800, 600)); // 5
    t.checkExpect(this.easyBoard.pairCheck(), true);
    this.initData();
    this.easyBoard.flipCard(new Posn(200, 600)); // 1
    this.easyBoard.flipCard(new Posn(800, 200)); // 4
    t.checkExpect(this.easyBoard.pairCheck(), true);
    this.initData();
    this.easyBoard.flipCard(new Posn(200, 200)); // 0
    this.easyBoard.flipCard(new Posn(1100, 200)); // 6
    t.checkExpect(this.easyBoard.pairCheck(), true);
    this.initData();
    this.easyBoard.flipCard(new Posn(500, 200)); // 2
    this.easyBoard.flipCard(new Posn(1100, 600)); // 7
    t.checkExpect(this.easyBoard.pairCheck(), true);
    this.initData();
    this.easyBoard.flipCard(new Posn(200, 600));
    this.easyBoard.flipCard(new Posn(500, 600));
    t.checkExpect(this.easyBoard.pairCheck(), false);
    this.initData();
    this.easyBoard.flipCard(new Posn(800, 200));
    this.easyBoard.flipCard(new Posn(1100, 200));
    t.checkExpect(this.easyBoard.pairCheck(), false);
  }
  
  // tests the removePair method
  void testRemovePair(Tester t) {
    this.initData();
    this.easyBoard.flipCard(new Posn(500, 600)); // 3
    this.easyBoard.flipCard(new Posn(800, 600)); // 5
    ArrayList<Card> flipCheck = new ArrayList<Card>();
    flipCheck.add((Card) this.easyBoard.cardList.get(3));
    flipCheck.add((Card) this.easyBoard.cardList.get(5));
    t.checkExpect(this.easyBoard.flippedCardsList.size(), 2);
    t.checkExpect(this.easyBoard.flippedCardsList, flipCheck);
    this.easyBoard.removePair();
    t.checkExpect(this.easyBoard.cardList.get(3).gone, true);
    t.checkExpect(this.easyBoard.cardList.get(5).gone, true);
    t.checkExpect(this.easyBoard.flippedCardsList.size(), 0);
  }
}






