package tetris;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Tetris extends JFrame implements Runnable {
    
    static final int numRows = 20;
    static final int numColumns = 10;
    static final int XBORDER = 40;
    static final int YBORDER = 60;
    static final int YTITLE = 30;
    static final int WINDOW_BORDER = 8;
    static final int WINDOW_WIDTH = 2*(WINDOW_BORDER + XBORDER) + numColumns*30;
    //// day 1 ^^^
    static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 2 * YBORDER + numRows*30;
    static final int INFO = (int)(WINDOW_WIDTH*.3787878787878);
    
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    final int EMPTY = 0;
    int board[][];
    
    // shape positions
    int[] SX = new int[4];
    int[] SY = new int[4];
    int[] saves = new int[4];
    //h1 post 2 ^^
    static final int DOWN = 1;
    int shape = 0;
    int nextShape = (int)(Math.random()*7)+1;
    int xdir = 0;
    int fell = 0;
    int RX = 0;
    int RY = 0;

    //end
    boolean end = true;
    boolean nextshape = false;
    
    //time
    int Time = 0;

    //score
    int score = 0;
    int highScore = 0;
    //h1 3 ^^
    int DS = 0;


    int NSX[] = new int[4];
    int NSY[] = new int[4];

    Color DCOLOR[][] = new Color[numRows][numColumns];

    double FPS = 5;


    //shape color
    Color curshapecolor = Color.gray;
    Color prevshapecolor = Color.gray;


    static Tetris frame;
    public static void main(String[] args) {
        frame = new Tetris();
        frame.setSize(WINDOW_WIDTH+INFO, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    //h1 4/7 ^^

    public Tetris() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
        //d1 5/7
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_DOWN == e.getKeyCode()) {
                    moveDown();
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    //d1 6/7 ^^
                    xdir=-1;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    xdir=1;
                } else if (e.VK_Q == e.getKeyCode()) {
                    CW();
                } else if (e.VK_E == e.getKeyCode()) {
                    CCW();
                } else if (e.VK_S == e.getKeyCode()) {
                    moveDown();
                } else if (e.VK_A == e.getKeyCode()) {
                    xdir=-1;
                } else if (e.VK_D == e.getKeyCode()) {
                    //d1 7/7 ^^
                    xdir=1;
                } else if (e.VK_W == e.getKeyCode()) {
                    CW();
                }

                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void CCW()
    {
        boolean DR = false;
        for (int i = 0; i < SY.length; i++) {
            //rotate
            int xx = 0;
            int yy = 0;
            RX = SX[1];
            RY = SY[1];

            saves[i]=SY[i];
            yy = RY-(SX[i]-RX);
            xx = RX+(saves[i]-RY);
            try
                {
                if (yy < 0 || yy > numRows || xx < 0 || xx > numColumns || board[yy][xx] == DOWN)
                {
                    DR = true;
                }
            } catch(Exception e) {DR = true;}
        }
        if (!DR)
        {
            for (int i = 0; i < SY.length; i++) {
                //rotate
                RX = SX[1];
                RY = SY[1];

                saves[i]=SY[i];
                SY[i] = RY-(SX[i]-RX);
                SX[i] = RX+(saves[i]-RY);
            }
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void CW()
    {
        boolean DR = false;
        for (int i = 0; i < SY.length; i++) {
            //rotate
            int xx = 0;
            int yy = 0;
            RX = SX[1];
            RY = SY[1];

            saves[i]=SY[i];
            yy = RY+(SX[i]-RX);
            xx = RX-(saves[i]-RY);
            try
                {
                if (yy < 0 || yy > numRows || xx < 0 || xx > numColumns || board[yy][xx] == DOWN)
                {
                    DR = true;
                }
            } catch(Exception e) {DR = true;}
        }
        if (!DR)
        {
            for (int i = 0; i < SY.length; i++) {
                RX = SX[1];
                RY = SY[1];

                saves[i]=SY[i];
                SY[i] = RY+(SX[i]-RX);
                SX[i] = RX-(saves[i]-RY);
            }
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }

 

////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            xsize -= INFO;
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.black);
        g.fillRect(0, 0, xsize+INFO, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);

//Fill 2nd part

        //g.drawString("Score: " + score, (int)((getWidth2()+INFO/2)/xs), getHeight2()/2);
        //g.drawString("HighScore: " + highScore, (int)((getWidth2()+INFO/2)/xs), getHeight2()/2+(int)(xy*10));

        int x2[] = {getX(getWidth2()+INFO/10), getX(getWidth2()+INFO/10+4*getWidth2()/numColumns), getX(getWidth2()+INFO/10+4*getWidth2()/numColumns), getX(getWidth2()+INFO/10), getX(getWidth2()+INFO/10)};
        int y2[] = {getY(0), getY(0), getY((0)+4*getHeight2()/numRows ), getY((0)+4*getHeight2()/numRows ), getY(0)};
        g.fillPolygon(x2,y2,4);
        g.setColor(Color.red);
        g.drawPolyline(x2, y2, 5);

// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        double xs = 1.5;
        double xy = 1.5;
        g.setColor(Color.gray);
        g.scale(xs,xy);
        g.drawString("Score: " + score, (int)((getWidth2()+INFO/2)/xs), getHeight2()/2);
        g.drawString("HighScore: " + highScore, (int)((getWidth2()+INFO/2)/xs), getHeight2()/2+(int)(xy*10));
        g.scale(1/xs,1/xy);
        
        g.setColor(Color.black);
//horizontal lines
        for (int zi=1;zi<numRows;zi++)
        {
            g.drawLine(getX(0) ,getY(0)+zi*getHeight2()/numRows ,
            getX(getWidth2()) ,getY(0)+zi*getHeight2()/numRows );
        }
        //
        for (int zi=1;zi<4;zi++)
        {
            g.drawLine(getX(getWidth2()+INFO/10) ,getY(0)+zi*getHeight2()/numRows ,
            getX(getWidth2()+INFO/10+4*getWidth2()/numColumns) ,getY(0)+zi*getHeight2()/numRows );
        }
//vertical lines
        for (int zi=1;zi<numColumns;zi++)
        {
            g.drawLine(getX(0)+zi*getWidth2()/numColumns ,getY(0) ,
            getX(0)+zi*getWidth2()/numColumns,getY(getHeight2())  );
        }
        //
        for (int zi=1;zi<4;zi++)
        {
            g.drawLine(getX(getWidth2()+INFO/10)+zi*getWidth2()/numColumns ,getY(0) ,
            getX(getWidth2()+INFO/10)+zi*getWidth2()/numColumns,getY((0)+4*getHeight2()/numRows)  );
        }
        
//Display the objects of the board
        for (int zrow=0;zrow<numRows;zrow++)
        {
            for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
            {
                for (int i = 0; i < SX.length; i++)
                {
                    g.setColor(curshapecolor);
                    g.fillRect(getX(1)+SX[i]*getWidth2()/numColumns,
                    getY(2)+SY[i]*getHeight2()/numRows-1,
                    getWidth2()/numColumns-1,
                    getHeight2()/numRows-1); 
                }
                if (board[zrow][zcolumn] == DOWN)
                {
                    g.setColor(DCOLOR[zrow][zcolumn]);
                    g.fillRect(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                }
                g.setColor(prevshapecolor);
                for (int i = 0; i < NSX.length;i++) {
                    g.fillRect(getX(getWidth2() + INFO / 10 + NSX[i] * getWidth2() / numColumns+1),
                    getY((1) + NSY[i] * getHeight2() / numRows),
                    getWidth2() / numColumns-1,
                    getHeight2() / numRows-1);
                }
            }
        }
        
        if (end)
        {
            g.setColor(Color.black);
            g.fillRect(0,
            0,
            xsize+INFO,
            ysize); 
            g.setColor(Color.red);
            g.drawString("END!", (getWidth2()+INFO)/2, getHeight2()/2);
            g.drawString("Score: " + score, (getWidth2()+INFO)/2, getHeight2()/2 - 20);
        }

        if (highScore < score)
        {
            highScore = score;
        }

        gOld.drawImage(image, 0, 0, null);
    }

////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            
            
            double seconds = 1/FPS;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

        nextshape = false;

        score = 0;
//Allocate memory for the 2D array that represents the board.
        board = new int[numRows][numColumns];
//Initialize the board to be empty.
        for (int zrow = 0;zrow < numRows;zrow++)
        {
            for (int zcolumn = 0;zcolumn < numColumns;zcolumn++)
                board[zrow][zcolumn] = EMPTY;
        }

        end = false;
        
        newshape();
        
        //board[8][1] = DOWN;
        
        Time = 0;

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            reset();
        }

        FPS = 5+(score/100);

        //make shape move
        move();
        
        int CC = 0;
        boolean fall = false;
        for (int zrow=0;zrow<numRows;zrow++)
        {
            for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
            {
                if (board[1][zcolumn] == DOWN)
                {
                    end = true;
                }
                if (board[zrow][zcolumn] == DOWN)
                {
                    CC++;
                }
            }
            if (CC >= numColumns)
            {
                fall = true;
                for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
                {
                    board[zrow][zcolumn] = EMPTY;
                }
            }
            CC = 0;
            if (fall)
            {
                score += 50;
                for (int ii=0;ii<numRows;ii++)
                {
                    for (int zrow2=0;zrow2<numRows;zrow2++)
                    {
                        for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
                        {
                            try
                            {
                                if (board[zrow2][zcolumn] == DOWN && board[zrow2+1][zcolumn] == EMPTY)
                                {
                                    board[zrow2+1][zcolumn] = DOWN;
                                    board[zrow2][zcolumn] = EMPTY;
                                    DCOLOR[zrow2+1][zcolumn] = DCOLOR[zrow2][zcolumn];
                                }
                            } catch (Exception e){}
                        }
                    }
                }
            }
        }
        
        //incrament time
        xdir = 0;
        Time++;
    }

////////////////////////////////////////////////////////////////////////////
    public void move() {
        boolean DR = false;
        for (int i = 0; i < SY.length; i++) {
            //every .2 seconds, check if moving will result in a collision
            try {
                if (board[SY[i]][SX[i]+xdir] == DOWN) {
                    //solid();
                    DR = true;
                }
            } catch (Exception e) {
                DR = true;
            }
        }
        //every .2 seconds, move down (5 fps)
        if (!DR)
        {
            for (int i = 0; i < SY.length; i++) {
                SX[i]+=xdir;
            }
        }
        if (Time % 5 == 4) {
            moveDown();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void moveDown() {
        for (int i = 0; i < SY.length; i++) {
        //every 1 second, check if moving down will result in a collision

            try {
                if (board[SY[i] + 1+DS][SX[i]] == DOWN) {
                    solid();
                    break;
                }
            } catch (Exception e) {
                if (SY[i] + 1+DS >= numRows)
                {
                    solid();
                    break;
                }
            }
        }
        if (DS>0)
        {
            DS=0;
        }

        //every 1 second, move down (we have 5 fps) (this makes it 1FPS)
        for (int i = 0; i < SY.length; i++) {
            SY[i]+=1;
        }
        fell++;
    }
////////////////////////////////////////////////////////////////////////////
    public void solid(){
        fell=0;
        //make the shape solid
        for (int i = 0; i < SY.length; i++)
        {
            board[SY[i]][SX[i]] = DOWN;
            DCOLOR[SY[i]][SX[i]] = curshapecolor;
            SX[i] = 0;
            SY[i] = 0;
        }
        newshape();
    }
////////////////////////////////////////////////////////////////////////////
    public void  newshape() {
        shape = nextShape;
        nextShape = (int)(Math.random()*7)+1;
        switch (shape)
        {
            case 1:
                s1();
                break;
            case 2:
                s2();
                break;
            case 3:
                s3();
                break;
            case 4:
                s4();
                break;
            case 5:
                s5();
                break;
            case 6:
                s6();
                break;
            case 7:
                s7();
                break;

        }
        nextshape = true;
        switch (nextShape)
        {
            case 1:
                s1();
                break;
            case 2:
                s2();
                break;
            case 3:
                s3();
                break;
            case 4:
                s4();
                break;
            case 5:
                s5();
                break;
            case 6:
                s6();
                break;
            case 7:
                s7();
                break;

        }
        nextshape = false;
    }
/////////////////////////////////////////////////////////////////////////////
    public void s1(){ //make into new method


        if (!nextshape) {
            curshapecolor = Color.red;
            SX[0] = 4;
            SX[2] = 5;
            SX[1] = 5;
            SX[3] = 6;

            SY[0] = 1;
            SY[2] = 1;
            SY[1] = 0;
            SY[3] = 0;
        }
        else
        {
            NSX[0] = 1;
            NSX[2] = 2;
            NSX[1] = 2;
            NSX[3] = 3;

            NSY[0] = 2;
            NSY[2] = 2;
            NSY[1] = 1;
            NSY[3] = 1;
            prevshapecolor = Color.red;
        }
    }
    public void s2(){
        if (!nextshape) {
            curshapecolor = Color.cyan;
            SX[0] = 3;
            SX[2] = 4;
            SX[1] = 5;
            SX[3] = 6;

            SY[0] = 0;
            SY[2] = 0;
            SY[1] = 0;
            SY[3] = 0;
        }
        else {
            NSX[0] = 0;
            NSX[2] = 1;
            NSX[1] = 2;
            NSX[3] = 3;

            NSY[0] = 0;
            NSY[2] = 0;
            NSY[1] = 0;
            NSY[3] = 0;
            prevshapecolor = Color.cyan;
        }
    }
    public void s3(){
        if (!nextshape) {

            curshapecolor = Color.orange;
            SX[0] = 3;
            SX[1] = 4;
            SX[2] = 5;
            SX[3] = 5;

            SY[0] = 0;
            SY[1] = 0;
            SY[2] = 0;
            SY[3] = 1;
        }
        else {
            NSX[0] = 0;
            NSX[1] = 1;
            NSX[2] = 2;
            NSX[3] = 2;

            NSY[0] = 0;
            NSY[1] = 0;
            NSY[2] = 0;
            NSY[3] = 1;
            prevshapecolor = Color.orange;
        }
    }
    public void s4(){
        if (!nextshape) {

            curshapecolor = Color.blue;
            SX[0] = 4;
            SX[2] = 4;
            SX[1] = 5;
            SX[3] = 6;

            SY[0] = 1;
            SY[2] = 0;
            SY[1] = 0;
            SY[3] = 0;
        }
        else {
            NSX[0] = 1;
            NSX[2] = 1;
            NSX[1] = 2;
            NSX[3] = 3;

            NSY[0] = 1;
            NSY[2] = 0;
            NSY[1] = 0;
            NSY[3] = 0;
            prevshapecolor = Color.blue;
        }
    }
    public void s5(){
        if (!nextshape) {

            curshapecolor = Color.green;
            SX[0] = 4;
            SX[1] = 5;
            SX[2] = 5;
            SX[3] = 6;

            SY[0] = 0;
            SY[1] = 0;
            SY[2] = 1;
            SY[3] = 1;
        }
        else {
            NSX[0] = 1;
            NSX[1] = 2;
            NSX[2] = 2;
            NSX[3] = 3;

            NSY[0] = 1;
            NSY[1] = 1;
            NSY[2] = 2;
            NSY[3] = 2;
            prevshapecolor = Color.green;
        }
    }
    public void s6(){
        if (!nextshape) {

            curshapecolor = new Color(128,0,128);
            SX[0] = 4;
            SX[1] = 5;
            SX[2] = 6;
            SX[3] = 5;

            SY[0] = 0;
            SY[1] = 0;
            SY[2] = 0;
            SY[3] = 1;
        }
        else {
            NSX[0] = 1;
            NSX[1] = 2;
            NSX[2] = 3;
            NSX[3] = 2;

            NSY[0] = 0;
            NSY[1] = 0;
            NSY[2] = 0;
            NSY[3] = 1;
            prevshapecolor = new Color(128,0,128);
        }
    }
    public void s7(){
        if (!nextshape) {

            curshapecolor = Color.yellow;
            SX[0] = 4;
            SX[1] = 5;
            SX[2] = 4;
            SX[3] = 5;

            SY[0] = 0;
            SY[1] = 0;
            SY[2] = 1;
            SY[3] = 1;
        }
        else {
            NSX[0] = 1;
            NSX[1] = 2;
            NSX[2] = 1;
            NSX[3] = 2;

            NSY[0] = 1;
            NSY[1] = 1;
            NSY[2] = 2;
            NSY[3] = 2;
            prevshapecolor = Color.yellow;
        }
    }
/////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }


/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER + WINDOW_BORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE );
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    public int getWidth2() {
        return (xsize - 2 * (XBORDER + WINDOW_BORDER));
    }

    public int getHeight2() {
        return (ysize - 2 * YBORDER - WINDOW_BORDER - YTITLE);
    }
}
