import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI implements ActionListener, KeySelectionManager, KeyListener {
  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  int worldWidth = screenSize.width;
  int worldHeight = screenSize.height - 50;
  Dimension panelSize;
  private JLabel label;
  private JPanel panel;
  private JFrame frame;
  private JButton enterButton;
  private JComboBox<Integer> dropDown;
  int defaultNum = 0;
  Integer[] choices = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
      11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
      21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 
      31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 
      41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52};

  // 
  public GUI(){
    frame = new JFrame();
    enterButton = new JButton("Enter");
    dropDown = new JComboBox<Integer>(this.choices);
    label = new JLabel("How many pairs would you like to match? (Max of 52)");

    frame.setFocusable(true);

    dropDown.setForeground(new Color(50, 70, 160));
    dropDown.setKeySelectionManager(this);

    label.setFont(new Font("Sans Serif", 1, 16));
    label.setForeground(new Color(50, 70, 160));

    panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
    panel.setBackground(new Color(255, 255, 245));
    panel.setLayout(new GridLayout(3, 1));
    panel.add(label);
    panel.add(dropDown);
    panel.add(enterButton);
    panelSize = panel.getSize();

    enterButton.addActionListener(this);
    enterButton.setBorder(BorderFactory.createLoweredSoftBevelBorder()); 
    enterButton.setForeground(Color.white);
    enterButton.setOpaque(true);
    enterButton.setBackground(new Color(50, 70, 160));
    enterButton.setFont(new Font("Sans Serif", 1, 16));

    frame.add(panel, BorderLayout.CENTER);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocation((this.worldWidth - panel.getWidth()) / 2, (this.worldHeight - panel.getHeight()) / 2);
    frame.setTitle("Memory Game");
    frame.pack();
    frame.setVisible(true);
  }

  // graphic user interface
  public static void main(String[] args) { 
    new GUI();
  }

  // gets the user's choice & starts the game
  public void actionPerformed(ActionEvent e) {
    int pairNum = (int) dropDown.getSelectedItem();
    GameWorld world = new GameWorld(new Board(pairNum));
    world.bigBang(worldWidth, worldHeight, 0.1);
  }

  // shifts the dropdown depending on the user's key press
  public int selectionForKey(char aKey, ComboBoxModel<?> aModel) {
    int toUse = aKey + defaultNum;
    if ((toUse < 59) && (toUse > 49)) {
      return toUse - 49;
    } else {
      return - 1;
    }
  }

  // changes the dropDown depending on the user's key presses
  // enter, up, and down are covered
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      int pairNum = (int) dropDown.getSelectedItem();
      GameWorld world = new GameWorld(new Board(pairNum));
      world.bigBang(worldWidth, worldHeight, 0.1);
    } else if ((e.getKeyCode() == KeyEvent.VK_UP)
        || (e.getKeyCode() == KeyEvent.VK_KP_UP)) {
        this.defaultNum += this.defaultNum;
      } else if ((e.getKeyCode() == KeyEvent.VK_DOWN)
          || (e.getKeyCode() == KeyEvent.VK_KP_DOWN)) {
        this.defaultNum -= this.defaultNum;
      }
 }

  @Override
  public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void keyReleased(KeyEvent e) {
    // TODO Auto-generated method stub

  }

}