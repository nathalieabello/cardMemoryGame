import java.awt.Color;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.AboveImage;
import javalib.worldimages.CircleImage;
import javalib.worldimages.EquilateralTriangleImage;
import javalib.worldimages.FontStyle;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.OverlayOffsetImage;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.RotateImage;
import javalib.worldimages.ScaleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.TriangleImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

// empty / normal card interface
interface ICard {
  // draws this ICard
  WorldImage draw();

  //draws the suits for this card
  WorldImage drawNumOfSuits(WorldImage suitImg);

  // visualizes the face card image
  WorldImage drawFaceCards(WorldImage suitImg);

  // visualizes the number card image
  WorldImage drawNonFaceCards(WorldImage suitImg, int numToUse);

  // determines whether this ICard is the same as the given ICard
  boolean sameCard(ICard card);

  // determines if the given card has the same fields as this card
  boolean sameCardFields(Card card);

  // determines if this card is covered
  boolean isCovered();

  // covers this card
  void cover();
}

// empty card class
class NoCard implements ICard{
  // empty card constructor
  NoCard() {}

  // visualizes NoCard
  public WorldImage draw() {
    return new CircleImage(10, OutlineMode.OUTLINE, 
        Color.yellow.darker().darker().darker());
  }

  // draws the suits for this card
  public WorldImage drawNumOfSuits(WorldImage suitImg) {
    throw new IllegalArgumentException("no card");
  }

  // visualizes the face card image
  public WorldImage drawFaceCards(WorldImage suitImg) {
    throw new IllegalArgumentException("no card");
  }

  // visualizes the number card image
  public WorldImage drawNonFaceCards(WorldImage suitImg, int numToUse) {
    throw new IllegalArgumentException("no card");
  }

  //determines whether this ICard is the same as the given ICard
  // noCards cannot be equal to one another
  public boolean sameCard(ICard card) {
    return false;
  }

  // determines if the given card has the same fields as this card
  // noCards cannot be equal to a Card
  public boolean sameCardFields(Card card) {
    return false;
  }

  // determines if this card is covered
  public boolean isCovered() {
    return true;
  }

  // does nothing because a noCard is always covered
  public void cover() {}

}

// card class
class Card implements ICard{
  int suit; // 0 spade, 1 heart, 2 clubs, 3 diamond
  boolean col; // true if black, false if red
  int num; // 1 if Ace, 10 if J, 11 if Q, 12 if K
  boolean covered;
  boolean gone;

  // card constructor
  Card(int suit, int num) {
    this.suit = suit;
    this.num = num;
    this.covered = true;
    this.gone = false;
    if ((this.suit == 1) ||( this.suit == 3)) {
      this.col = false; // red for hearts and diamonds
    } else {
      this.col = true; // black for spades and clubs
    }
  }

  // draws this card
  public WorldImage draw() {
    OutlineMode outline;
    Color col = new Color(195, 230, 255);
    if (this.gone) {
      outline = OutlineMode.SOLID;
      col = Color.green.darker().darker().darker();
    } else if (this.covered) {
      outline = OutlineMode.SOLID;
    } else {
      outline = OutlineMode.OUTLINE;
    }
    // blank card
    WorldImage cardImg = new RectangleImage(150, 250, OutlineMode.SOLID, new Color(230, 230, 230));
    // covered card
    WorldImage topImg = new RectangleImage(150, 250, outline, col);
    // suit image
    WorldImage suitImg = new Suits().drawSuit(this.col, this.suit);
    // number / letter (if face card) image
    WorldImage numImg = new Nums().drawNum(this.num, this.col);
    // buffer in between cards
    WorldImage buffer = new RectangleImage(200, 300, OutlineMode.SOLID, Color.green.darker().darker().darker());
    // overlays the numImg in corners & suits on this card
    return new OverlayImage(topImg, new OverlayImage(new OverlayOffsetImage(numImg, -50.0, -104.0, 
        new OverlayOffsetImage(numImg, 52.0, 104.0,
            new OverlayImage(this.drawNumOfSuits(suitImg), cardImg))),
        buffer));
  }

  // draws the suits for this card
  public WorldImage drawNumOfSuits(WorldImage suitImg) {
    // face cards
    if ((this.num == 1) || (this.num > 10)) { // A, J, Q, K
      return this.drawFaceCards(suitImg);
    } else {
      // number cards
      return this.drawNonFaceCards(suitImg, this.num);
    }
  }

  // visualizes the face card image
  public WorldImage drawFaceCards(WorldImage suitImg) {
    WorldImage crownTriangle = new EquilateralTriangleImage(32.0, OutlineMode.SOLID, Color.yellow);
    WorldImage twoCrownTriangle = new OverlayOffsetImage(crownTriangle, 16.0, 0.0, crownTriangle);
    WorldImage fourCrownTriangle = new OverlayOffsetImage(twoCrownTriangle, 32.0, 0.0, twoCrownTriangle);
    WorldImage leftCrown = new TriangleImage(new Posn(0,0), new Posn(0, 27), new Posn(16, 27), OutlineMode.SOLID, Color.yellow);
    WorldImage rightCrown = new TriangleImage(new Posn(0,0), new Posn(0, 27), new Posn(-16, 27), OutlineMode.SOLID, Color.yellow);
    WorldImage crown = new OverlayOffsetImage(rightCrown, -32.0, 0.0, new OverlayOffsetImage(fourCrownTriangle, -32.0, 0.0, leftCrown));
    return new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(suitImg, 3));
  }

  // visualizes the number card image
  public WorldImage drawNonFaceCards(WorldImage suitImg, int numToUse) {
    WorldImage twoSuit = new OverlayOffsetImage(suitImg, 100.0, 0.0, suitImg);
    if (numToUse <= 3) { // 2 or 3 --> draws suits aligned down center
      int i = 1;
      WorldImage soFar = suitImg;
      while (i < numToUse) {
        i = i + 1;
        soFar = new AboveImage(suitImg, soFar);
      }
      return soFar;
    } else if (numToUse <= 9) { // 4 to 9 --> arranges suits depending on parity
      int i;
      if ((numToUse % 2) == 0) { // even
        i = numToUse / 2; // -- 3
        WorldImage soFar = twoSuit;
        while (i > 1) { // 3 > 0
          i = i - 1; // 2
          soFar = new AboveImage(twoSuit, soFar);
        }
        return soFar;
      } else { // odd
        return new OverlayImage(suitImg, this.drawNonFaceCards(suitImg, numToUse - 1));
      }
    } else { // 10
      return new OverlayImage(this.drawNonFaceCards(suitImg, 8), this.drawNonFaceCards(suitImg, 2));
    }
  }

  //determines whether this ICard is the same as the given ICard
  public boolean sameCard(ICard card) {
    return card.sameCardFields(this);
  }

  // determines if the given card has the same fields as this card
  public boolean sameCardFields(Card card) {
    return (this.num == card.num)
        && (this.suit == card.suit);
  }

  // returns a new Card with the same information as this card
  public Card deepCopy() {
    return new Card(this.suit, this.num);
  }

  // determines if this card is covered
  public boolean isCovered() {
    return this.covered;
  }

  // covers this card
  public void cover() {
    this.covered = true;
  }
}

// world class --> to make the game interactive
class CardWorld extends World {
  // cardWorld constructor
  CardWorld() {

  }

  // creates the visuals for the user to see
  public WorldScene makeScene() {
    WorldScene backdrop = new WorldScene(500, 5000);
    return backdrop.placeImageXY(new Card(1, 7).draw(), 200, 200);
  }

}

// suits constructor
class Suits {
  // buffer between suits
  WorldImage background = new RectangleImage(50, 50, OutlineMode.SOLID, new Color(230, 230, 230));
  Suits() {}

  // visualizes this suit
  public WorldImage drawSuit(boolean color, int suit) {
    Color col;
    if (color) {
      col = Color.black;
    } else {
      col = Color.red;
    }

    WorldImage spade = new OverlayOffsetImage(new OverlayOffsetImage(new OverlayOffsetImage(new CircleImage(8, OutlineMode.SOLID, col), 
        15.0, 0.0, new CircleImage(8, OutlineMode.SOLID, col)), 0.0, 4.0, 
        new TriangleImage(new Posn(25,25), new Posn(20, 50), new Posn(30, 50), OutlineMode.SOLID, col)), 0.0, -14.0, 
        new TriangleImage(new Posn(25,25), new Posn(9, 40), new Posn(41, 40), OutlineMode.SOLID, col));

    WorldImage heart = new OverlayOffsetImage(new OverlayOffsetImage(new CircleImage(8, OutlineMode.SOLID, col), 
        15.0, 0.0, new CircleImage(8, OutlineMode.SOLID, col)), 0.5, 10.9, 
        new TriangleImage(new Posn(0, 0), new Posn(31, 0), new Posn(16, 16), OutlineMode.SOLID, col));

    WorldImage club = new OverlayOffsetImage(new OverlayOffsetImage(new OverlayOffsetImage(new CircleImage(8, OutlineMode.SOLID, col), 
        15.0, 0.0, new CircleImage(8, OutlineMode.SOLID, col)), 0.0, -10.0, 
        new CircleImage(8, OutlineMode.SOLID, col)), 0.0, 8.0, 
        new TriangleImage(new Posn(25,25), new Posn(20, 50), new Posn(30, 50), OutlineMode.SOLID, col));

    WorldImage diamond = new OverlayOffsetImage(new EquilateralTriangleImage(25.0, OutlineMode.SOLID, col), 
        0.0, 12.5, new RotateImage(new EquilateralTriangleImage(25.0, OutlineMode.SOLID, col), 180));

    if (suit == 0) {
      // spade
      return new OverlayImage(spade, background);
    } else if (suit == 1) {
      // heart
      return new OverlayImage(heart, background);
    } else if (suit == 2) {
      // club
      return new OverlayImage(club, background);
    } else if (suit == 3) {
      // diamond
      return new OverlayImage(diamond, background);      
    } else {
      // no more suits
      throw new IllegalArgumentException("invalid suit");
    }
  }
}

// constructor for card number / face
class Nums {
  WorldImage background = new RectangleImage(20, 20, OutlineMode.SOLID, Color.white);
  Nums() {}

  // visualizes the given number in the given color
  public WorldImage drawNum(int num, boolean color) {
    String number;
    Color col;
    if (color) {
      col = Color.black;
    } else {
      col = Color.red;
    }
    if (num == 1) {
      number = "A";
    } else if (num == 11) {
      number = "J";
    } else if (num == 12) {
      number = "Q";
    } else if (num == 13) {
      number = "K";
    } else {
      number = num + "";
    }
    return new TextImage(number, 32, FontStyle.BOLD, col);
  }
}

class CardExamples {

  Suits suits;
  Nums nums;
  CardWorld cw;
  WorldImage spade;
  WorldImage heart;
  WorldImage club;
  WorldImage diamond;
  WorldImage coveredCard;
  WorldImage uncoveredCard;
  WorldImage cardBackground;
  WorldImage buffer;
  WorldImage cardBuffer;
  WorldImage rAce;
  WorldImage bAce;
  WorldImage rTwo;
  WorldImage bTwo;
  WorldImage rNine; 
  WorldImage bNine; 
  WorldImage rQueen;
  WorldImage bQueen;
  WorldImage rKing;
  WorldImage bKing;
  WorldImage crownTriangle;
  WorldImage twoCrownTriangle;
  WorldImage fourCrownTriangle;
  WorldImage leftCrown;
  WorldImage rightCrown;
  WorldImage crown;

  void initData() {
    suits = new Suits();
    nums = new Nums();
    cw = new CardWorld();

    spade = 
        new OverlayOffsetImage(new OverlayOffsetImage(new OverlayOffsetImage(new CircleImage(8, OutlineMode.SOLID, Color.black), 
            15.0, 0.0, new CircleImage(8, OutlineMode.SOLID, Color.black)), 0.0, 4.0, 
            new TriangleImage(new Posn(25,25), new Posn(20, 50), new Posn(30, 50), OutlineMode.SOLID, Color.black)), 0.0, -14.0, 
            new TriangleImage(new Posn(25,25), new Posn(9, 40), new Posn(41, 40), OutlineMode.SOLID, Color.black));

    heart = 
        new OverlayOffsetImage(new OverlayOffsetImage(new CircleImage(8, OutlineMode.SOLID, Color.red), 
            15.0, 0.0, new CircleImage(8, OutlineMode.SOLID, Color.red)), 0.5, 10.9, 
            new TriangleImage(new Posn(0, 0), new Posn(31, 0), new Posn(16, 16), OutlineMode.SOLID, Color.red));

    club = 
        new OverlayOffsetImage(new OverlayOffsetImage(new OverlayOffsetImage(new CircleImage(8, OutlineMode.SOLID, Color.black), 
            15.0, 0.0, new CircleImage(8, OutlineMode.SOLID, Color.black)), 0.0, -10.0, 
            new CircleImage(8, OutlineMode.SOLID, Color.black)), 0.0, 8.0, 
            new TriangleImage(new Posn(25,25), new Posn(20, 50), new Posn(30, 50), OutlineMode.SOLID, Color.black));

    diamond = 
        new OverlayOffsetImage(new EquilateralTriangleImage(25.0, OutlineMode.SOLID, Color.red), 
            0.0, 12.5, new RotateImage(new EquilateralTriangleImage(25.0, OutlineMode.SOLID, Color.red), 180));

    coveredCard = new RectangleImage(150, 250, OutlineMode.OUTLINE, new Color(195, 230, 255));
    uncoveredCard = new RectangleImage(150, 250, OutlineMode.SOLID, new Color(195, 230, 255));
    cardBackground = new RectangleImage(150, 250, OutlineMode.SOLID, new Color(230, 230, 230));
    buffer = new RectangleImage(50, 50, OutlineMode.SOLID, new Color(230, 230, 230));
    cardBuffer = new RectangleImage(200, 300, OutlineMode.SOLID, Color.green.darker().darker().darker());

    rAce = new TextImage("A", 32, FontStyle.BOLD, Color.red);
    bAce = new TextImage("A", 32, FontStyle.BOLD, Color.black);
    rTwo = new TextImage("2", 32, FontStyle.BOLD, Color.red);
    bTwo = new TextImage("2", 32, FontStyle.BOLD, Color.black);
    rNine = new TextImage("9", 32, FontStyle.BOLD, Color.red);
    bNine = new TextImage("9", 32, FontStyle.BOLD, Color.black);
    rQueen = new TextImage("Q", 32, FontStyle.BOLD, Color.red);
    bQueen = new TextImage("Q", 32, FontStyle.BOLD, Color.black);
    rKing = new TextImage("K", 32, FontStyle.BOLD, Color.red);
    bKing = new TextImage("K", 32, FontStyle.BOLD, Color.black);

    crownTriangle = new EquilateralTriangleImage(32.0, OutlineMode.SOLID, Color.yellow);
    twoCrownTriangle = new OverlayOffsetImage(crownTriangle, 16.0, 0.0, crownTriangle);
    fourCrownTriangle = new OverlayOffsetImage(twoCrownTriangle, 32.0, 0.0, twoCrownTriangle);
    leftCrown = new TriangleImage(new Posn(0,0), new Posn(0, 27), new Posn(16, 27), OutlineMode.SOLID, Color.yellow);
    rightCrown = new TriangleImage(new Posn(0,0), new Posn(0, 27), new Posn(-16, 27), OutlineMode.SOLID, Color.yellow);
    crown = new OverlayOffsetImage(rightCrown, -32.0, 0.0, new OverlayOffsetImage(fourCrownTriangle, -32.0, 0.0, leftCrown));
  }
  // visualizes the cards
  void testMakeCard(Tester t) {
    this.initData();
    int worldWidth = 500;
    int worldHeight = 500;
    this.cw.bigBang(worldWidth, worldHeight, 0.5);  
  }

  // tests the card constructor
  boolean testCardConstructor(Tester t) {
    this.initData();
    // 0 spade, 1 heart, 2 clubs, 3 diamond
    // true if black, false if red
    // 1 if Ace, 10 if J, 11 if Q, 12 if K
    // spades and clubs should be black
    // hearts and diamonds should be red
    // Card(suit, num)
    return t.checkExpect(new Card(0,1).col, true)
        && t.checkExpect(new Card(1,1).col, false)
        && t.checkExpect(new Card(2,1).col, true)
        && t.checkExpect(new Card(3,1).col, false)
        && t.checkExpect(new Card(0,9).col, true)
        && t.checkExpect(new Card(1,9).col, false)
        && t.checkExpect(new Card(2,9).col, true)
        && t.checkExpect(new Card(3,9).col, false)
        && t.checkExpect(new Card(0,12).col, true)
        && t.checkExpect(new Card(1,12).col, false)
        && t.checkExpect(new Card(2,12).col, true)
        && t.checkExpect(new Card(3,12).col, false);
  }

  // tests the drawSuit method
  boolean testDrawSuit(Tester t) {
    this.initData();
    return t.checkExpect(this.suits.drawSuit(true, 0), new OverlayImage(this.spade, this.buffer))
        && t.checkExpect(this.suits.drawSuit(false, 1), new OverlayImage(this.heart, this.buffer))
        && t.checkExpect(this.suits.drawSuit(true, 2), new OverlayImage(this.club, this.buffer))
        && t.checkExpect(this.suits.drawSuit(false, 3), new OverlayImage(this.diamond, this.buffer));
  }

  // tests the drawNum method
  boolean testDrawNum(Tester t) {
    this.initData();
    return t.checkExpect(this.nums.drawNum(1, false), this.rAce)
        && t.checkExpect(this.nums.drawNum(1, true), this.bAce)
        && t.checkExpect(this.nums.drawNum(2, false), this.rTwo)
        && t.checkExpect(this.nums.drawNum(2, true), this.bTwo)
        && t.checkExpect(this.nums.drawNum(9, false), this.rNine)
        && t.checkExpect(this.nums.drawNum(9, true), this.bNine)
        && t.checkExpect(this.nums.drawNum(12, false), this.rQueen)
        && t.checkExpect(this.nums.drawNum(12, true), this.bQueen)
        && t.checkExpect(this.nums.drawNum(13, false), this.rKing)
        && t.checkExpect(this.nums.drawNum(13, true), this.bKing);
  }

  // tests the draw method
  void testDraw(Tester t) {
    this.initData();
    WorldImage twoSpade = 
        new OverlayOffsetImage(new OverlayImage(this.spade, this.buffer), 
            100.0, 0.0, new OverlayImage(this.spade, this.buffer));
    WorldImage twoHeart = 
        new OverlayOffsetImage(new OverlayImage(this.heart, this.buffer), 
            100.0, 0.0, new OverlayImage(this.heart, this.buffer));
    WorldImage spadeWithCrown = 
        new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(new OverlayImage(this.spade, this.buffer), 3));
    WorldImage heartWithCrown = 
        new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(new OverlayImage(this.heart, this.buffer), 3));
    WorldImage clubWithCrown = 
        new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(new OverlayImage(this.club, this.buffer), 3));
    WorldImage diamondWithCrown = 
        new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(new OverlayImage(this.diamond, this.buffer), 3));
    WorldImage aceOfSpades = new OverlayOffsetImage(this.bAce, -50.0, -104.0, 
        new OverlayOffsetImage(this.bAce, 52.0, 104.0, new OverlayImage(spadeWithCrown, this.cardBackground)));
    WorldImage aceOfHearts = new OverlayOffsetImage(this.rAce, -50.0, -104.0, 
        new OverlayOffsetImage(this.rAce, 52.0, 104.0, new OverlayImage(heartWithCrown, this.cardBackground)));
    WorldImage twoOfClubs = new OverlayOffsetImage(this.bTwo, -50.0, -104.0, 
        new OverlayOffsetImage(this.bTwo, 52.0, 104.0, 
            new OverlayImage(new AboveImage(new OverlayImage(this.club, this.buffer), 
                new OverlayImage(this.club, this.buffer)), this.cardBackground)));
    WorldImage twoOfDiamonds = new OverlayOffsetImage(this.rTwo, -50.0, -104.0, 
        new OverlayOffsetImage(this.rTwo, 52.0, 104.0, 
            new OverlayImage(new AboveImage(new OverlayImage(this.diamond, this.buffer), 
                new OverlayImage(this.diamond, this.buffer)), this.cardBackground)));
    WorldImage nineOfSpades = new OverlayOffsetImage(this.bNine, -50.0, -104.0, 
        new OverlayOffsetImage(this.bNine, 52.0, 104.0,
            new OverlayImage(new OverlayImage(new OverlayImage(this.spade, this.buffer), 
                new AboveImage(twoSpade, new AboveImage(twoSpade, 
                    new AboveImage(twoSpade, twoSpade)))), this.cardBackground)));
    WorldImage nineOfHearts = new OverlayOffsetImage(this.rNine, -50.0, -104.0, 
        new OverlayOffsetImage(this.rNine, 52.0, 104.0,
            new OverlayImage(new OverlayImage(new OverlayImage(this.heart, this.buffer), 
                new AboveImage(twoHeart, new AboveImage(twoHeart, 
                    new AboveImage(twoHeart, twoHeart)))), this.cardBackground)));
    WorldImage queenOfSpades = new OverlayOffsetImage(this.bQueen, -50.0, -104.0, 
        new OverlayOffsetImage(this.bQueen, 52.0, 104.0, new OverlayImage(spadeWithCrown, this.cardBackground)));
    WorldImage queenOfHearts = new OverlayOffsetImage(this.rQueen, -50.0, -104.0, 
        new OverlayOffsetImage(this.rQueen, 52.0, 104.0, new OverlayImage(heartWithCrown, this.cardBackground)));
    WorldImage kingOfClubs = new OverlayOffsetImage(this.bKing, -50.0, -104.0, 
        new OverlayOffsetImage(this.bKing, 52.0, 104.0, new OverlayImage(clubWithCrown, this.cardBackground)));
    WorldImage kingOfDiamonds = new OverlayOffsetImage(this.rKing, -50.0, -104.0, 
        new OverlayOffsetImage(this.rKing, 52.0, 104.0, new OverlayImage(diamondWithCrown, this.cardBackground)));

    aceOfSpades = new OverlayImage(aceOfSpades, this.cardBuffer);
    aceOfHearts = new OverlayImage(aceOfHearts, this.cardBuffer);
    twoOfClubs = new OverlayImage(twoOfClubs, this.cardBuffer);
    twoOfDiamonds = new OverlayImage(twoOfDiamonds, this.cardBuffer);
    nineOfSpades = new OverlayImage(nineOfSpades, this.cardBuffer);
    nineOfHearts = new OverlayImage(nineOfHearts, this.cardBuffer);
    queenOfSpades = new OverlayImage(queenOfSpades, this.cardBuffer);
    queenOfHearts = new OverlayImage(queenOfHearts, this.cardBuffer);
    kingOfClubs = new OverlayImage(kingOfClubs, this.cardBuffer);
    kingOfDiamonds = new OverlayImage(kingOfDiamonds, this.cardBuffer);

    Card kingOfDiamondsCard = new Card(3, 13);
    Card twoOfDiamondsCard = new Card(3, 2);
    t.checkExpect(new NoCard().draw(), 
        new CircleImage(0, OutlineMode.OUTLINE, Color.green.darker().darker().darker()));
    t.checkExpect(new Card(0, 1).draw(), new OverlayImage(this.uncoveredCard, aceOfSpades));
    t.checkExpect(new Card(1, 1).draw(), new OverlayImage(uncoveredCard, aceOfHearts));
    t.checkExpect(new Card(2, 2).draw(), new OverlayImage(uncoveredCard, twoOfClubs));
    t.checkExpect(new Card(3, 2).draw(), new OverlayImage(uncoveredCard, twoOfDiamonds));
    t.checkExpect(new Card(0, 9).draw(), new OverlayImage(uncoveredCard, nineOfSpades));
    t.checkExpect(new Card(1, 9).draw(), new OverlayImage(uncoveredCard, nineOfHearts));
    t.checkExpect(new Card(0, 12).draw(), new OverlayImage(uncoveredCard, queenOfSpades));
    t.checkExpect(new Card(1, 12).draw(), new OverlayImage(uncoveredCard, queenOfHearts));
    t.checkExpect(new Card(2, 13).draw(), new OverlayImage(uncoveredCard, kingOfClubs));
    t.checkExpect(new Card(3, 13).draw(), new OverlayImage(uncoveredCard, kingOfDiamonds));
    kingOfDiamondsCard.covered = false;
    twoOfDiamondsCard.covered = false;
    t.checkExpect(kingOfDiamondsCard.draw(), new OverlayImage(coveredCard, kingOfDiamonds));
    t.checkExpect(twoOfDiamondsCard.draw(), new OverlayImage(coveredCard, twoOfDiamonds));
  }

  // tests the drawNumOfSuits method
  boolean testDrawNumOfSuits(Tester t) {
    this.initData();
    WorldImage twoSpade = new OverlayOffsetImage(this.spade, 100.0, 0.0, this.spade);
    WorldImage twoHeart = new OverlayOffsetImage(this.heart, 100.0, 0.0, this.heart);
    WorldImage twoClub = new OverlayOffsetImage(this.club, 100.0, 0.0, this.club);
    WorldImage twoDiamond = new OverlayOffsetImage(this.diamond, 100.0, 0.0, this.diamond);
    return t.checkExpect(new Card(0,1).drawNumOfSuits(this.spade), 
        new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.spade, 3)))
        && t.checkExpect(new Card(1,1).drawNumOfSuits(this.heart), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.heart, 3)))
        && t.checkExpect(new Card(2,1).drawNumOfSuits(this.club), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.club, 3)))
        && t.checkExpect(new Card(3,1).drawNumOfSuits(this.diamond), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.diamond, 3)))
        && t.checkExpect(new Card(0,12).drawNumOfSuits(this.spade), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.spade, 3)))
        && t.checkExpect(new Card(1,12).drawNumOfSuits(this.heart), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.heart, 3)))
        && t.checkExpect(new Card(2,12).drawNumOfSuits(this.club), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.club, 3)))
        && t.checkExpect(new Card(3,12).drawNumOfSuits(this.diamond), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.diamond, 3)))
        && t.checkExpect(new Card(0,9).drawNumOfSuits(this.spade), 
            new OverlayImage(this.spade, new AboveImage(twoSpade, 
                new AboveImage(twoSpade, new AboveImage(twoSpade, twoSpade)))))
        && t.checkExpect(new Card(1,9).drawNumOfSuits(this.heart), 
            new OverlayImage(this.heart, new AboveImage(twoHeart, 
                new AboveImage(twoHeart, new AboveImage(twoHeart, twoHeart)))))
        && t.checkExpect(new Card(2,9).drawNumOfSuits(this.club), 
            new OverlayImage(this.club, new AboveImage(twoClub, 
                new AboveImage(twoClub, new AboveImage(twoClub, twoClub)))))
        && t.checkExpect(new Card(3,9).drawNumOfSuits(this.diamond), 
            new OverlayImage(this.diamond, new AboveImage(twoDiamond, 
                new AboveImage(twoDiamond, new AboveImage(twoDiamond, twoDiamond)))))
        && t.checkExpect(new Card(0,2).drawNumOfSuits(this.spade), new AboveImage(this.spade, this.spade))
        && t.checkExpect(new Card(1,2).drawNumOfSuits(this.heart), new AboveImage(this.heart, this.heart))
        && t.checkExpect(new Card(2,2).drawNumOfSuits(this.club), new AboveImage(this.club, this.club))
        && t.checkExpect(new Card(3,2).drawNumOfSuits(this.diamond), new AboveImage(this.diamond, this.diamond));
  }

  // tests the drawFaceCards method
  boolean testDrawFaceCards(Tester t) {
    this.initData();
    return t.checkExpect(new Card(0,1).drawFaceCards(this.spade), 
        new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.spade, 3)))
        && t.checkExpect(new Card(1,1).drawFaceCards(this.heart), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.heart, 3)))
        && t.checkExpect(new Card(2,1).drawFaceCards(this.club), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.club, 3)))
        && t.checkExpect(new Card(3,1).drawFaceCards(this.diamond), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.diamond, 3)))
        && t.checkExpect(new Card(0,12).drawFaceCards(this.spade), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.spade, 3)))
        && t.checkExpect(new Card(1,12).drawFaceCards(this.heart), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.heart, 3)))
        && t.checkExpect(new Card(2,12).drawFaceCards(this.club), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.club, 3)))
        && t.checkExpect(new Card(3,12).drawFaceCards(this.diamond), 
            new OverlayOffsetImage(crown, 0.0, 60.0, new ScaleImage(this.diamond, 3)));
  }

  // tests the drawNonFaceCards method
  boolean testDrawNonFaceCards(Tester t) {
    this.initData();
    WorldImage twoSpade = new OverlayOffsetImage(this.spade, 100.0, 0.0, this.spade);
    WorldImage twoHeart = new OverlayOffsetImage(this.heart, 100.0, 0.0, this.heart);
    WorldImage twoClub = new OverlayOffsetImage(this.club, 100.0, 0.0, this.club);
    WorldImage twoDiamond = new OverlayOffsetImage(this.diamond, 100.0, 0.0, this.diamond);
    return t.checkExpect(new Card(0,9).drawNonFaceCards(this.spade, 9), 
        new OverlayImage(this.spade, new AboveImage(twoSpade, 
            new AboveImage(twoSpade, new AboveImage(twoSpade, twoSpade)))))
        && t.checkExpect(new Card(1,9).drawNonFaceCards(this.heart, 9), 
            new OverlayImage(this.heart, new AboveImage(twoHeart, 
                new AboveImage(twoHeart, new AboveImage(twoHeart, twoHeart)))))
        && t.checkExpect(new Card(2,9).drawNonFaceCards(this.club, 9), 
            new OverlayImage(this.club, new AboveImage(twoClub, 
                new AboveImage(twoClub, new AboveImage(twoClub, twoClub)))))
        && t.checkExpect(new Card(3,9).drawNonFaceCards(this.diamond, 9), 
            new OverlayImage(this.diamond, new AboveImage(twoDiamond, 
                new AboveImage(twoDiamond, new AboveImage(twoDiamond, twoDiamond)))))
        && t.checkExpect(new Card(0,2).drawNonFaceCards(this.spade, 2), new AboveImage(this.spade, this.spade))
        && t.checkExpect(new Card(1,2).drawNonFaceCards(this.heart, 2), new AboveImage(this.heart, this.heart))
        && t.checkExpect(new Card(2,2).drawNonFaceCards(this.club, 2), new AboveImage(this.club, this.club))
        && t.checkExpect(new Card(3,2).drawNonFaceCards(this.diamond, 2), new AboveImage(this.diamond, this.diamond));
  }


}