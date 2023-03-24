package main;

import entity.Player;
import object.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3; // scaling the tile
    public final int tileSize = originalTileSize * scale; // 16 x 3 = 48x48 tile size
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 48x16 = 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 48x12 = 576 pixels

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    int FPS = 60;

    // SYSTEM
    Thread gameThread;
    KeyHandler keyH = new KeyHandler();
    TileManager tileM = new TileManager(this);
    Sound music = new Sound();
    Sound soundEffect = new Sound();
    public UI ui = new UI(this);

    // ENTITY AND OBJECTS
    public Player player = new Player(this,keyH);
    public Collision collision = new Collision(this);
    public SuperObject[] obj = new SuperObject[10];
    public AssetSetter assetSetter = new AssetSetter(this);

    // GAME STATE
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;

    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // all the drawings will be done in an offscreen painting buffer (+performance)
        this.addKeyListener(keyH); // can recognize the key input
        this.setFocusable(true); // this main.GamePanel can be focused to receive key inputs.
    }

    public void setupGameObject() {

        assetSetter.setObject();

        playMusic(0);
        gameState = playState;
    }

    public void startGameThread(){

        gameThread = new Thread(this); // this -> this class (main.GamePanel)
        gameThread.start();
    }

    // GAME LOOP
    @Override
    public void run() {

        double drawInterval = 1000/FPS; // 0.016 seconds
        double nextDrawTime = System.currentTimeMillis() + drawInterval;

        while(gameThread != null) {

            // 1 UPDATE: update information (character position)
            update();

            // 2 DRAW: draw the screen with the updated information
            repaint(); // call the paintComponent() method

            try {
                double remainingTime = nextDrawTime - System.currentTimeMillis();

                if (remainingTime < 0) { remainingTime = 0; }

                Thread.sleep((long)remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void update() {

            player.update();
    }

    // build in function
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        long drawStart = 0;
        if (keyH.checkDrawTime) {
            drawStart = System.nanoTime();
        }

        tileM.draw(g2);

        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2, this);
            }
        }

        player.draw(g2);

        ui.draw(g2);

        if (keyH.checkDrawTime) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.WHITE);
            g2.drawString("Passed: " + passed, 10, 400);
            System.out.println("Passed: " + passed);
        }

        g2.dispose(); // dispose of this graphics content and release any system resource that is using it.
    }

    public void playMusic(int i) {

        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSoundEffect(int i) {
        soundEffect.setFile(i);
        soundEffect.play();
    }
}
