
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;



public class GUI extends JFrame implements KeyListener, ActionListener {
    
    private static final boolean DEFAULT = false;
    private static final boolean AI_GAME = true;

    private Engine engine;
    private MenuPanel menuPanel;
    private BoardPanel boardPanel;
    private OptionBar optionBar;
    
    private AI ai;
    private boolean mode;
    
    public GUI() {
        
        super("2048 Game");
        this.setSize(299,400);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        this.setLayout(new BorderLayout());
        
        engine = new Engine();
        menuPanel = new MenuPanel();
        boardPanel = new BoardPanel();
        optionBar = new OptionBar();

        ai = new AI(this);
        mode = DEFAULT;
        
        this.setJMenuBar(optionBar);
        this.add(boardPanel, BorderLayout.CENTER);
        this.add(menuPanel, BorderLayout.SOUTH);

        updateAll();
        this.setIconImage(getImage());
        this.setFocusable(true);
        this.setVisible(true);  
    }
    
    private void updateAll() {
        boardPanel.update();
        menuPanel.update();
        optionBar.update();
    }
    
    private Image getImage() {    
        return null; //new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/icon2.png"))).getImage();
    }

    
    
    private class BoardPanel extends JPanel {

        private final int GAP = 3;  
        private JButton[] tiles;
        
        public BoardPanel() {
            super();
            this.setBorder(BorderFactory.createEmptyBorder(GAP,GAP,GAP,GAP));
            this.setLayout(new GridLayout(4,4,GAP,GAP));
            tiles = new JButton[16];
            for (int i=0; i<16; i++) {
                tiles[i] = new JButton();
                tiles[i].setMargin(new Insets(0,0,0,0));
                tiles[i].setFocusable(false);
                this.add(tiles[i]);
            }
        }
        
        public void update() {
            for (int i=0; i<16; i++) {
                if (engine.getTileValue(i) != 0) {
                    tiles[i].setText(Integer.toString(engine.getTileValue(i)));
                    tiles[i].setBackground(engine.getTileColor(i));
                    tiles[i].setFont(engine.getFont(i));
                    tiles[i].setEnabled(true);
                }
                else {
                    tiles[i].setText(null);
                    tiles[i].setBackground(null);
                    tiles[i].setEnabled(false);
                }
            }
        }
        
    }
    
    
    private class MenuPanel extends JPanel  implements ActionListener {

        private JPanel messagePanel;
        private JTextArea messageText;
        private JPanel buttonsPanel;
        private JButton multiButton;
        private JButton scoreButton;
        
        public MenuPanel() {
            super();
            this.setLayout(new GridBagLayout());
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setBorder(BorderFactory.createEtchedBorder());
            messageText = new JTextArea();
            messageText.setFont(new Font(null,Font.ITALIC,11));
            messageText.setBackground(null);
            messageText.setEditable(false);
            messagePanel.add(messageText, BorderLayout.CENTER);
            
            buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new GridLayout(2,1));
            buttonsPanel.setBorder(BorderFactory.createEtchedBorder());
            multiButton = new JButton("New game");
            multiButton.addActionListener(this);
            multiButton.setFocusable(false);
            multiButton.setFont(new Font(null,0,11));
            scoreButton = new JButton("Score: 0");
            scoreButton.setFont(new Font(null,0,11));
            scoreButton.setMargin(new Insets(0,0,0,0));
            scoreButton.setEnabled(false);
            buttonsPanel.add(multiButton);
            buttonsPanel.add(scoreButton);
            
            this.add(messagePanel,setConstraints(0,0,3,0,GridBagConstraints.BOTH,0));
            this.add(buttonsPanel,setConstraints(3,0,1,0,GridBagConstraints.BOTH,0));           
        }        
        
        public void update() {
            scoreButton.setText("Score: " + engine.getScore());
            setGameMessage();
            setMultiButton();
        }
        
        public void actionPerformed(ActionEvent ae) {
            switch(multiButton.getText()) {
                case "New Game": engine.reset(); break;
                case "Start": engine.reset(); break;
                case "Pause": ai.stop(); break;
                case "Continue": ai.start(); break;
            }
                    updateAll();
        }
        
        private void setMultiButton() {
            if (mode == DEFAULT)
                multiButton.setText("New Game");
            else {
                if (ai.isRunning()) multiButton.setText("Pause");
                else multiButton.setText("Continue");                    
            }
        }
        
        private void setGameMessage() {
            if (engine.isGameActive()) {
                messageText.setText(" Use your arrow keys to move the tiles.\n"
                    + " When two tiles with the same number \n"
                    + " touch, they merge into one.");
            }
            else {
                messageText.setText(" Game over. \n You scored "
                        + engine.getScore() + " points. \n"
                        + " Play again?");
            }
        }
        
        public GridBagConstraints setConstraints(int gx, int gy, int gw, int gh, int fill, int i) {
            GridBagConstraints out = new GridBagConstraints();
            out.fill = fill;
            out.gridx = gx;
            out.gridy = gy;
            out.gridwidth = gw;
            out.gridheight = gh;
            out.weightx = 0.5;
            out.insets = new Insets(i,i,i,i);
            return out;
        }    

    }
    
    
    private class OptionBar extends JMenuBar implements ItemListener, MouseListener {
        
        private JMenu optionsMenu;
        private JMenu highscoresMenu;
        private JMenuItem[] highscoresList;
        private JMenu modeMenu;
        private JMenu bestScoreDisplay;
        private JRadioButtonMenuItem defItem;
        private JRadioButtonMenuItem aiItem;
        private JMenuItem resetItem;
        private JMenuItem exitItem;
        
        
        public OptionBar() {
            super();
            optionsMenu = new JMenu("Options");
            optionsMenu.setFont(new Font(null,1,11));
            buildHighscoreMenu();
            resetItem = new JMenuItem("New Game");
            exitItem = new JMenuItem("Exit");
            
            modeMenu = new JMenu("Mode");
            modeMenu.setFont(new Font(null,1,11));
            defItem = new JRadioButtonMenuItem("Default Gameplay");
            aiItem = new JRadioButtonMenuItem("Artifitial Inteligence");
            
            ButtonGroup mode = new ButtonGroup();
            defItem.setSelected(true);
            aiItem.addItemListener(this);
            mode.add(defItem);
            mode.add(aiItem);
            
            resetItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    engine.reset();
                    menuPanel.update();
                    boardPanel.update();
                    optionBar.update();
                }});
            
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    System.exit(0);
                }});
            
            optionsMenu.add(highscoresMenu);
            optionsMenu.addSeparator();
            optionsMenu.add(resetItem);
            optionsMenu.add(exitItem);
            
            modeMenu.add(defItem);
            modeMenu.add(aiItem);
            
            bestScoreDisplay = new JMenu("Best: " + engine.getScore());
            bestScoreDisplay.setFont(new Font(null,0,11));
            bestScoreDisplay.setEnabled(false);
            
            this.add(optionsMenu);
            this.add(modeMenu);
            this.add(Box.createHorizontalGlue());
            this.add(bestScoreDisplay);
            
            update();
                    
        }
        
        private void buildHighscoreMenu() {
            highscoresMenu = new JMenu("Highscores");
            int size = engine.getAmountOfScoresSaved();
            highscoresList = new JMenuItem[size];
            for (int i=0; i<size; i++) {
                int[] hs = engine.getHighscoreValues(i);
                String entry = (i+1)+"-";
                entry += String.format("%8s", Integer.toString(hs[engine.SCORE]));
                highscoresList[i] = new JMenuItem(entry);
                highscoresList[i].setFont(new Font(null,Font.BOLD,10));
                highscoresList[i].addMouseListener(this);
                highscoresMenu.add(highscoresList[i]);
            }       
        }
        
        public void update() {
            bestScoreDisplay.setText("Best: "+engine.getBestScore());
        }
        
        public void mouseEntered(MouseEvent me) {
            engine.showHighscore(Character.getNumericValue(((JMenuItem)me.getSource()).getText().charAt(0)));
            updateAll();
        }
        
        public void mouseExited(MouseEvent me) {
            engine.showOriginal();
            updateAll();
        }
        
        public void itemStateChanged(ItemEvent ie) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                mode = AI_GAME;
                engine.reset();
                ai.start();
            }
            else{
                mode = DEFAULT; ai.stop();
            }
            updateAll();
        }
        
        public void mouseClicked(MouseEvent me) {}
        public void mousePressed(MouseEvent me) {}
        public void mouseReleased(MouseEvent me) {}

    }
   
    
    public void keyReleased(KeyEvent ke) {
        if (mode == DEFAULT) {
            engine.interact(ke.getKeyCode());
            boardPanel.update();
            menuPanel.update();
            optionBar.update();
        }
    }   
    
    public void actionPerformed(ActionEvent ae) {
        engine.interact(ai.generateMove(engine.getValues()));
        updateAll();
    }
    
    public void keyTyped(KeyEvent ke) {}
    public void keyPressed(KeyEvent ke) {}
    
}
