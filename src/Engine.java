
import libraries.HSLColor;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


class Engine {
    
    private static final String SAVEFILE = "data.sav";
    public static final int SCORE = 16;
    
    private int[][] leaderboard;
    private int[] vcopy;
    private int scopy;
    private int[] values;
    private int score;
    
    private boolean game;
    private boolean savedstate;
    private boolean savable;
    
    public Engine() {
        values = new int[16];  
        score = 0;
        leaderboard = new int[5][17];
        game = true;
        savedstate = false;
        savable = true;
        try {
            loadData();
        }
        catch (IOException e) {
            generateTile();
            generateTile();
        }
    }
    
    public Engine(boolean a) {
        this();
        savable = false;
    }
    
    public void generateTile() {
        ArrayList<Integer> emptyspaces = new ArrayList<>();
        for (int i=0; i<16; i++) {
            if (values[i] == 0)
                emptyspaces.add(i);
        }
        Random r = new Random();
        int index = r.nextInt(emptyspaces.size());
        values[emptyspaces.get(index)] = (r.nextInt(2)+1)*2;
        if (emptyspaces.size() == 1) { 
            if (checkGameover())
                endgame();
        }
    }
    
    public int[] getValues() {
        return values.clone();
    }
    
    public void setValues(int[] board) {
        this.values = board.clone();
    }
    
    public int getTileValue(int i) {
        return values[i];
    }
    
    public Color getTileColor(int i) {
        int n = values[i];
        int p = 0;
        while (n!=1) {
            n = n / 2;
            p++;
        }
        int base = 6;
        if (p<3) return new HSLColor(20,100,100).getRGB(); 
        if (p<7) return new HSLColor(20,100,100-(p-2)*5 - base).getRGB(); 
        if (p<9) return new HSLColor(50,100,(90-((p-6)%4)*6) - base).getRGB();
        if (p<10) return  new HSLColor(20+((p-7))*30 ,100,85 - base).getRGB();  
        if (p<12) return new HSLColor(240 ,60-(30*(p-10)),80-(15*(p-10)) - base).getRGB();        
        return new HSLColor(220 ,3 ,90-(10*(p-10)) - base).getRGB();   
    }   
    
    public Font getFont(int i) {
        return new Font(null,0,18);
    }
    
    public int getScore() {
        return score;
    }
    
    public int getBestScore() {
        return leaderboard[0][16];
    }
    
    public boolean interact(int direction) {
        if (game) {
            int[] old = Arrays.copyOf(values, values.length);
            switch (direction) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    for (int n=0; n<2; n++) {
                        for (int i=4; i<16; i++) {
                            if (values[i] != 0 && values[i-4] == 0) {
                                values[i-4] = values[i];
                                values[i] = 0;
                            }
                        }
                    }
                    for (int i=0; i<8; i++) {
                        if (values[i] != 0) {
                            if (values[i] == values[i+4]) {
                                values[i] *= 2;
                                values[i+4] = 0;
                                score += values[i]; 
                            }
                        }
                    }
                    for (int i=4; i<16; i++) {
                        if (values[i] != 0 && values[i-4] == 0) {
                            values[i-4] = values[i];
                            values[i] = 0;
                        }
                    }
                    break;            
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    for (int n=0; n<2; n++) {
                        for (int i=11; i>=0; i--) {
                            if (values[i] != 0 && values[i+4] == 0) {
                                values[i+4] = values[i];
                                values[i] = 0;
                            }
                        }
                    }
                    for (int i=15; i>=4; i--) {
                        if (values[i] != 0) {
                            if (values[i] == values[i-4]) {
                                values[i] *= 2;
                                values[i-4] = 0;
                                score += values[i]; 
                            }
                        }
                    }
                     for (int i=11; i>=0; i--) {
                        if (values[i] != 0 && values[i+4] == 0) {
                            values[i+4] = values[i];
                            values[i] = 0;
                        }
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:  
                    for (int n=0; n<3; n++) {
                        for (int i=15; i>=0; i--) {
                            if (i%4 != 3) {
                                if (values[i] != 0 && values[i+1] == 0) {
                                    values[i+1] = values[i];
                                    values[i] = 0;
                                }
                            }
                        }
                    }                    
                    for (int i=15; i>=0; i--) {
                            if (i%4 != 0 && values[i] != 0) {
                                if (values[i] == values[i-1]) {
                                    values[i] *= 2;
                                    values[i-1] = 0;
                                    score += values[i]; 
                                }
                            }
                        }
                    for (int i=15; i>=0; i--) {
                        if (i%4 != 3) {
                            if (values[i] != 0 && values[i+1] == 0) {
                                values[i+1] = values[i];
                                values[i] = 0;
                            }
                        }
                    }
                    break;   
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:  
                    for (int n=0; n<3; n++) {
                        for (int i=0; i<16; i++) {
                            if (i%4 != 0) {
                                if (values[i] != 0 && values[i-1] == 0) {
                                    values[i-1] = values[i];
                                    values[i] = 0;
                                }
                            }
                        }
                    }                    
                    for (int i=0; i<16; i++) {
                            if (i%4 != 3 && values[i] != 0) {
                                if (values[i] == values[i+1]) {
                                    values[i] *= 2;
                                    values[i+1] = 0;
                                    score += values[i]; 
                                }
                            }
                        }
                    for (int i=0; i<16; i++) {
                        if (i%4 != 0) {
                            if (values[i] != 0 && values[i-1] == 0) {
                                values[i-1] = values[i];
                                values[i] = 0;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }    

            if (!Arrays.equals(old, values)) {
                if (savable) {
                    generateTile();        
                    saveData();
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean checkGameover() {
        
        for (int i=0; i<16; i++) {
            if (i%4 != 3 && values[i] != 0) {
                if (values[i] == values[i+1]) {
                    return false;
                }
            }
                
        }
        for (int i=15; i>=0; i--) {
            if (i%4 != 0 && values[i] != 0) {
                if (values[i] == values[i-1]) {
                    values[i] *= 2;
                    values[i-1] = 0;
                    score += values[i]; 
                }
            }
        }
        for (int i=15; i>=0; i--) {
            if (i%4 != 0 && values[i] != 0) {
                if (values[i] == values[i-1]) {
                    return false;
                }
            }
        }
        for (int i=0; i<8; i++) {
            if (values[i] != 0) {
                if (values[i] == values[i+4]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void endgame() {
        if (game == true) {
            if (score > leaderboard[4][SCORE])
                setLeaderboard();
            saveData();
            game = false;
        }
    }
    
    public void setLeaderboard() {
        int[][] newLeaderboard = new int[5][17];
        boolean flag = true;
        int index = 0;
        for (int n=0; n<5; n++) {
            if (score > leaderboard[n][SCORE] && flag) {
                for (int i=0; i<16; i++)
                    newLeaderboard[n][i] = values[i];
                newLeaderboard[n][SCORE] = score;
                flag = false;
            }
            else {
                newLeaderboard[n] = leaderboard[index];
                index++;
            }   
        }
        leaderboard = newLeaderboard;
    }
    
    public int[] getHighscoreValues(int i) {
        return leaderboard[i];
    }
    
    public int getAmountOfScoresSaved() {
        int out = 0;
        for (int i=0; i<5; i++) {
            if (leaderboard[i][SCORE] != 0)
                out++;
        }
        return out;
    }
    
    public void showHighscore(int n) {
        if (savedstate == false) {
            savedstate = true;
            vcopy = values.clone();
            scopy = score;
        }
        for (int i=0; i<16; i++) 
            values[i] = leaderboard[n-1][i];
        score = leaderboard[n-1][SCORE];
    }
    
    public void showOriginal() {
        if (savedstate == true) {
            savedstate = false;
            values = vcopy;
            score = scopy;
        }
    }
    
    public boolean isGameActive() {
        return game;
    }
    
    public void loadData() throws IOException {
        Scanner scanner = new Scanner(new File(SAVEFILE));
        score = scanner.nextInt();
        for (int i=0; i<16; i++)
            values[i] = scanner.nextInt();
        for (int n=0; n<5; n++) {
            int[] hs = new int[17];
            for (int i=0; i<17; i++)
                hs[i] = scanner.nextInt();
            leaderboard[n]=hs;
        }
        scanner.close();
    }
   
    public void saveData() {
        if (game) {
            try {
                PrintWriter writer = new PrintWriter(SAVEFILE);
                writer.println(score);
                for (int i=0; i<16; i++) 
                    writer.println(values[i]);
                for (int n=0; n<5; n++) {
                    for (int i=0; i<17; i++)
                        writer.println(leaderboard[n][i]);
                }
                writer.close();
            }
            catch(IOException e) {
                System.out.println("Couldn't save data.");
            } 
        }
    }
    
    public void reset() {
        values = new int[16];
        score = 0;
        game = true;
        generateTile();
        generateTile();
    }
    
    public double monotonicity() {
        int monotonicity = 0;
        for (int n=0; n<4; n++) {
            if (values[(4*n)] < values[(4*n)+1]) {
                for (int i=4*n+1; i<4*n+3; i++) {
                    if (values[i] < values[i+1])
                        monotonicity++;
                }
            } 
            else {
                for (int i=4*n+1; i<4*n+3; i++) {
                    if (values[i] > values[i+1])
                        monotonicity++;
                }
            }
        }
        for (int n=0; n<4; n++) {
            if (values[n] < values[n+4]) {
                for (int i=n+4; i<12; i+=4) {
                    if (values[i] < values[i+4])
                        monotonicity++;
                }
            } 
            else {
                for (int i=n+4; i<12; i+=4) {
                    if (values[i] > values[i+4])
                        monotonicity++;
                }
            }
        }
        return map((double)monotonicity, 0.0, 16.0, 0.0, 1.0);
    }
    
    public double smoothness() {
        int smoothness = 0;
        int elements1 = 16;
        for (int n=0; n<16; n++) {
            if (values[n] == 0) {
                elements1--;
                continue;
            }
            double average = 0;
            int elements2 = 4;
            int aux = log2(values[n]);
            if (n+1 >= 0 && n+1 < 16) average += Math.abs(aux - log2(values[n+1])); else elements2--;
            if (n-1 >= 0 && n-1 < 16) average += Math.abs(aux - log2(values[n-1])); else elements2--;
            if (n+4 >= 0 && n+4 < 16) average += Math.abs(aux - log2(values[n+4])); else elements2--;
            if (n-4 >= 0 && n-4 < 16) average += Math.abs(aux - log2(values[n-4])); else elements2--;
            

            smoothness += average / (double) elements2;
        }
        double out = smoothness / (double) elements1;
        return map(out, 0.0, 4.0, 0.0, 1.0);
    }
    
    public double availability() {
        int count = 0;
        for (int i : values) {
            if (i!=0) {
                if (count>8)
                    count++;
                count++;
            }
        }
        return map((double)count, 0.0, 22.0, 0.0, 1.0);
    }
    
    private int log2(int input) {
        int output = 0;
        while(input > 1) {
            input = input/2;
            output++;
        }
        return output;
    }
    
    public static double map(double valueCoord1, double startCoord1, double endCoord1, double startCoord2, double endCoord2) {
        double EPSILON = 1e-12;
        if (Math.abs(endCoord1 - startCoord1) < EPSILON) 
            throw new ArithmeticException("/ 0");
        double offset = startCoord2;
        double ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1);
        return ratio * (valueCoord1 - startCoord1) + offset;
    }
    
    public double weight() {
        final double M = 3;
        final double S = 2;
        final double A = 4;
        
        double weight = 0;
            weight += this.monotonicity()*M;
            weight -= this.smoothness()*S;
            weight -= this.availability()*A;
        return map (weight, (-S)+(-A), M, 0.0, 1.0);    
    }
    
    public int[][] getLayer() {
        ArrayList<Integer> emptyspaces = new ArrayList<>();
        for (int i=0; i<16; i++) {
            if (values[i] == 0)
                emptyspaces.add(i);
        }
        
        int[][] out = new int[emptyspaces.size()*2][16];
        for (int i=0; i<emptyspaces.size(); i++) {
            int[] board = values.clone();
            board[emptyspaces.get(i)] = 4;
            out[i]= board;
        }
        for (int i=0; i<emptyspaces.size(); i++) {
            int[] board = values.clone();
            board[emptyspaces.get(i)] = 2;
            out[i+emptyspaces.size()] = board;
        }
        return out;
    }
}
