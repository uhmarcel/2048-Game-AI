
import com.sun.glass.events.KeyEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;

public class AI extends Timer {

    public AI(ActionListener al) {
        super(100, al);
    }
    
    public void start() {
        this.setInitialDelay(500);
        super.start();
    }
    
    private int countTiles(int[] board) {
        int out = 0;
        for (int i : board) { if (i!=0) out++; }
        return out;
    }
    
    private int largestTile(int[] board) {
        int largest = 0;
        int index = 0;
        for (int i=0; i<16; i++) {
            if (board[i] > largest) {
                largest = board[i];
                index = i;
            }
        }
        return index;
    }
    
    private boolean isLargestAtCorner(int index) {
        switch (index) {
            case 0:
            case 3:
            case 12:
            case 15:
                return true;
        }
        return false;
    }
    
    
  
    public int generateMove(int[] board) {
        Engine test = new Engine(false);
        
        int bestOption = 0;
        double bestWeight = 0;
        double weight;
        
        for (int i=0; i<4; i++)  {
            test.setValues(board);
            if (!test.interact(37 + i))
                continue;            
            weight = test.weight();
            
            
            double averageLayerWeight = 0;
            int counter = 0;
            double weight2 = 0;
            
            for (int[] current : test.getLayer()) {
                weight2 = 0;
                counter++;
                Engine test2 = new Engine();
                int elements = 4;
                for (int j=0; j<4; j++)  {
                    test2.setValues(current);
                    if (!test.interact(37 + j)) {
                        elements--;
                        continue;           
                    }
                    weight2 += test2.weight();
                }
                averageLayerWeight += weight2  / (double) elements;
            }
            averageLayerWeight = averageLayerWeight / counter;
            
            if ((averageLayerWeight + weight) / 2.0 > bestWeight) {
                bestOption = 37 + i;
                bestWeight = (averageLayerWeight + weight) / 2.0 ;
            }
        }
        if (bestOption != 0) {
            System.out.println(" = "+bestWeight);
            return bestOption;
        }
        this.stop();
        return 0;
    }
    
    
    
}


