package Game.GameUI;

import javax.swing.*;

import GameObjects.HerosAndClasses.ClassEnum;
import GameObjects.HerosAndClasses.Hero;
import GameObjects.HerosAndClasses.HeroFactory;
import GameObjects.MatchesAndSeasons.FightSimulation;
import GameObjects.MatchesAndSeasons.MatchHero;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class TeamfightWindow extends JFrame {
    private static final int WINDOW_WIDTH = 1600; 
    private static final int WINDOW_HEIGHT = 1200;
    private static final int CHARACTER_SIZE = 100;
    
    private BattlePanel battlePanel;
    private JButton nextRoundButton;
    private FightSimulation fightSimulation;
    private JLabel roundLabel;
    
    public TeamfightWindow(List<Hero> team1, List<Hero> team2) {
        fightSimulation = new FightSimulation(new HeroFactory(), team1, team2);
        initializeWindow();
        initializeGame();
    }
    
    private void initializeWindow() {
        setTitle("Auto Battler");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create battle panel
        battlePanel = new BattlePanel();
        add(battlePanel, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        nextRoundButton = new JButton("Start Next Round");
        roundLabel = new JLabel("Round: 1");
        roundLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        nextRoundButton.addActionListener(e -> startNextRound());
        
        controlPanel.add(roundLabel);
        controlPanel.add(nextRoundButton);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void initializeGame() {
        fightSimulation.generateMatchHeroes();
        battlePanel.updateTeams(fightSimulation.getTeam1MatchHeroes(), fightSimulation.getTeam2MatchHeroes());
        roundLabel.setText("Round: " + fightSimulation.getRound());
    }
    
    private void startNextRound() {
        nextRoundButton.setEnabled(false);
        Thread battleThread = new Thread(() -> {
            fightSimulation.simulateRound(battlePanel);
            /** 
            if (wonRound) {
                matchSimulation.generateEnemyTeam();
                battlePanel.updateTeams(matchSimulation.getTeam1MatchHeroes(), matchSimulation.getTeam2MatchHeroes());
                roundLabel.setText("Round: " + matchSimulation.getRound());
                nextRoundButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Game Over! You reached round " + matchSimulation.getRound(), 
                    "Game Over", 
                    JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
            */
            //System.exit(0);
        });
        battleThread.start();
    } 
    
    public class BattlePanel extends JPanel {
        private List<MatchHero> team1;
        private List<MatchHero> team2;
        private Map<MatchHero, Point> characterPositions;
        private Map<MatchHero, Color> characterColors;
        private List<DamageNumber> damageNumbers;
        private List<AttackLine> attackLines;
        private List<MatchHero> graveyardTeam1;
        private List<MatchHero> graveyardTeam2;
        private static final int DAMAGE_NUMBER_DURATION = 1000; 
        private static final int ATTACK_LINE_DURATION = 500; 
        private static final int GRAVEYARD_X = WINDOW_WIDTH - 400;  
        
        public BattlePanel() {
            characterPositions = new HashMap<>();
            characterColors = new HashMap<>();
            damageNumbers = new ArrayList<>();
            attackLines = new ArrayList<>();
            graveyardTeam1 = new ArrayList<>();
            graveyardTeam2 = new ArrayList<>();
            setBackground(new Color(240, 240, 240));
        }
        
        public void updateTeams(List<MatchHero> team1, List<MatchHero> team2) {
            this.team1 = team1;
            this.team2 = team2;
            updatePositions();
            repaint();
        }
        
        private void updatePositions() {
            Map<MatchHero, Point> newPositions = new HashMap<>();
            Map<MatchHero, Color> newColors = new HashMap<>();
            
            // Position player team on the left
            for (int i = 0; i < team1.size(); i++) {
                if (team1.get(i) != null) {
                    newColors.put(team1.get(i), getColorForType(team1.get(i).getType()));
                    if (team1.get(i).isAlive()) {
                        newPositions.put(team1.get(i), new Point(150, 200 + i * 180));
                    } else if (!graveyardTeam1.contains(team1.get(i))) {
                        graveyardTeam1.add(team1.get(i));
                    }
                }
            }
            
            // Position enemy team on the right
            for (int i = 0; i < team2.size(); i++) {
                if (team2.get(i) != null) {
                    newColors.put(team2.get(i), getColorForType(team2.get(i).getType()));
                    if (team2.get(i).isAlive()) {
                        newPositions.put(team2.get(i), new Point(WINDOW_WIDTH - 800, 200 + i * 180));
                    } else if (!graveyardTeam2.contains(team2.get(i))) {
                        graveyardTeam2.add(team2.get(i));
                    }
                }
            }
            
            // Update the positions and colors
            characterPositions = newPositions;
            characterColors = newColors;
        }
        
        private Color getColorForType(ClassEnum heroClass) {
            switch (heroClass) {
                case FIGHTER: return new Color(220, 50, 50);
                case TANK: return new Color(50, 180, 50);
                case MAGE: return new Color(50, 50, 220);
                default: return Color.GRAY;
            }
        }
        
        public void animateAttack(MatchHero attacker, MatchHero target, int actualDamage) {
            if (!characterPositions.containsKey(attacker) || !characterPositions.containsKey(target)) {
                return;
            }
            
            Point start = characterPositions.get(attacker);
            Point end = characterPositions.get(target);
            
            // Add attack line
            attackLines.add(new AttackLine(
                new Point(start.x + CHARACTER_SIZE/2, start.y + CHARACTER_SIZE/2),
                new Point(end.x + CHARACTER_SIZE/2, end.y + CHARACTER_SIZE/2),
                System.currentTimeMillis(),
                attacker.getType()
            ));
            
            // Add damage number
            int damage = attacker.getAttack();
            damageNumbers.add(new DamageNumber(damage, actualDamage, end.x, end.y, System.currentTimeMillis()));
            
            // Create attack animation
            Thread animationThread = new Thread(() -> {
                repaint();
                try {
                    Thread.sleep(ATTACK_LINE_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            
            animationThread.start();
            try {
                animationThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        private void drawCharacter(Graphics2D g2d, MatchHero character, Point position) {
            int x = position.x;
            int y = position.y;
            
            // Draw different shapes based on character type
            g2d.setColor(characterColors.get(character));
            switch (character.getType()) {
                case FIGHTER:
                    // Draw shield shape
                    Image fighterImage = new ImageIcon("src/main/java/testPlayground/testingDynamicPatching/fighter.png").getImage();
                    if (fighterImage == null) {
                        System.out.println("Failed to load fighter.png");
                    }
                    g2d.drawImage(fighterImage, x, y, CHARACTER_SIZE, CHARACTER_SIZE, null);
                    //g2d.fillRect(x, y, CHARACTER_SIZE, CHARACTER_SIZE);
                    //g2d.setColor(Color.BLACK);
                    //g2d.drawRect(x, y, CHARACTER_SIZE, CHARACTER_SIZE);
                    break;
                case TANK:
                    // Draw diamond shape
                    Image tankImage = new ImageIcon("src/main/java/testPlayground/testingDynamicPatching/tank.png").getImage();
                    if (tankImage == null) {
                        System.out.println("Failed to load tank.png");
                    }
                    g2d.drawImage(tankImage, x, y, CHARACTER_SIZE, CHARACTER_SIZE, null);
                    /* int[] xPoints = {x + CHARACTER_SIZE/2, x + CHARACTER_SIZE, x + CHARACTER_SIZE/2, x};
                    int[] yPoints = {y, y + CHARACTER_SIZE/2, y + CHARACTER_SIZE, y + CHARACTER_SIZE/2};
                    g2d.fillPolygon(xPoints, yPoints, 4);
                    g2d.setColor(Color.BLACK);
                    g2d.drawPolygon(xPoints, yPoints, 4); */
                    break;
                case MAGE:
                    // Draw circle shape
                    Image mageImage = new ImageIcon("src/main/java/testPlayground/testingDynamicPatching/mage.png").getImage();
                    if (mageImage == null) {
                        System.out.println("Failed to load mage.png");
                    }
                    g2d.drawImage(mageImage, x, y, CHARACTER_SIZE, CHARACTER_SIZE, null);
                    /* g2d.fillOval(x, y, CHARACTER_SIZE, CHARACTER_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(x, y, CHARACTER_SIZE, CHARACTER_SIZE); */
                    break;
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw team labels with background
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            
            // Your team label
            g2d.setColor(new Color(220, 230, 255));
            g2d.fillRoundRect(40, 20, 200, 40, 10, 10);
            g2d.setColor(new Color(0, 100, 200));
            g2d.drawString("TEAM 1", 50, 50);
            
            // Enemy team label
            g2d.setColor(new Color(255, 220, 220));
            g2d.fillRoundRect(WINDOW_WIDTH - 400, 20, 200, 40, 10, 10);
            g2d.setColor(new Color(200, 0, 0));
            g2d.drawString("TEAM 2", WINDOW_WIDTH - 390, 50);
            
            // Draw vertical separator for graveyard
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawLine(GRAVEYARD_X - 30, 70, GRAVEYARD_X - 30, WINDOW_HEIGHT);
            
            // Graveyard label with background
            g2d.setColor(new Color(220, 220, 220));
            g2d.fillRoundRect(GRAVEYARD_X - 20, 20, 180, 40, 10, 10);
            g2d.setColor(new Color(80, 80, 80));
            g2d.drawString("GRAVEYARD", GRAVEYARD_X, 50);
            
            // Draw attack lines
            long currentTime = System.currentTimeMillis();
            Iterator<AttackLine> lineIterator = attackLines.iterator();
            while (lineIterator.hasNext()) {
                AttackLine line = lineIterator.next();
                if (currentTime - line.startTime > ATTACK_LINE_DURATION) {
                    lineIterator.remove();
                } else {
                    float alpha = 1.0f - (currentTime - line.startTime) / (float)ATTACK_LINE_DURATION;
                    g2d.setStroke(new BasicStroke(3.0f));
                    Color lineColor = getColorForType(line.attackerType);
                    g2d.setColor(new Color(
                        lineColor.getRed()/255f,
                        lineColor.getGreen()/255f,
                        lineColor.getBlue()/255f,
                        alpha
                    ));
                    g2d.drawLine(line.start.x, line.start.y, line.end.x, line.end.y);
                }
            }
            
            // Draw living characters
            characterPositions.forEach((character, position) -> {
                if (character.isAlive()) {
                    drawCharacter(g2d, character, position);
                    
                    // Draw HP bar with divisions
                    int hpBarWidth = 120;
                    int hpBarHeight = 15;
                    double hpRatio = (double) character.getHp() / character.getMaxHP();
                    
                    // Draw border
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(position.x - 25, position.y - 25, hpBarWidth + 2, hpBarHeight + 2);
                    
                    // Draw HP bar background
                    g2d.setColor(new Color(180, 0, 0));
                    g2d.fillRect(position.x - 24, position.y - 24, hpBarWidth, hpBarHeight);
                    
                    // Draw current HP
                    g2d.setColor(new Color(0, 180, 0));
                    g2d.fillRect(position.x - 24, position.y - 24, 
                        (int)(hpBarWidth * hpRatio), hpBarHeight);
                    
                    // Draw HP divisions
                    g2d.setColor(new Color(0, 0, 0, 80));
                    int divisionWidth = hpBarWidth / character.getMaxHP();
                    for (int i = 1; i < character.getMaxHP(); i++) {
                        int x = position.x - 24 + i * divisionWidth;
                        g2d.drawLine(x, position.y - 24, x, position.y - 24 + hpBarHeight);
                    }
                    
                    // Draw HP numbers
                    g2d.setFont(new Font("Arial", Font.BOLD, 14));
                    g2d.setColor(Color.WHITE);
                    String hpText = character.getHp() + "/" + character.getMaxHP();
                    int textWidth = g2d.getFontMetrics().stringWidth(hpText);
                    g2d.drawString(hpText, position.x - 24 + (hpBarWidth - textWidth)/2, 
                        position.y - 24 + hpBarHeight - 2);
                    
                    // Draw character type and stats
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    g2d.setColor(Color.BLACK);
                    String statsText = character.getType().toString() + " (ATK: " + character.getAttack() + ")";
                    g2d.drawString(statsText, position.x - 20, position.y + CHARACTER_SIZE + 25);
                }
            });
            
            // Draw graveyard characters
            int graveyardY = 100;
            int smallSize = CHARACTER_SIZE / 2;
            
            // Draw graveyard sections
            g2d.setColor(new Color(220, 230, 255, 50));
            g2d.fillRect(GRAVEYARD_X - 20, 80, 180, WINDOW_HEIGHT/2 - 80);
            g2d.setColor(new Color(0, 100, 200));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("Team 1 Fallen Heroes", GRAVEYARD_X - 10, 95);
            
            g2d.setColor(new Color(255, 220, 220, 50));
            g2d.fillRect(GRAVEYARD_X - 20, WINDOW_HEIGHT/2, 180, WINDOW_HEIGHT/2);
            g2d.setColor(new Color(200, 0, 0));
            g2d.drawString("Team 2 Fallen Heroes", GRAVEYARD_X - 10, WINDOW_HEIGHT/2 + 15);
            
            // Sort graveyard characters by team
            for (int i = 0; i < graveyardTeam1.size(); i++) {
                MatchHero deadCharacter = graveyardTeam1.get(i);
                int yPos = graveyardY + i * (20+smallSize);
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                g2d.setColor(characterColors.get(deadCharacter));
                
                switch (deadCharacter.getType()) {
                    case FIGHTER:
                        g2d.fillRect(GRAVEYARD_X, yPos, smallSize, smallSize);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(GRAVEYARD_X, yPos, smallSize, smallSize);
                        break;
                    case TANK:
                        int[] xPoints = {GRAVEYARD_X + smallSize/2, GRAVEYARD_X + smallSize, 
                            GRAVEYARD_X + smallSize/2, GRAVEYARD_X};
                        int[] yPoints = {yPos, yPos + smallSize/2, 
                            yPos + smallSize, yPos + smallSize/2};
                        g2d.fillPolygon(xPoints, yPoints, 4);
                        g2d.setColor(Color.BLACK);
                        g2d.drawPolygon(xPoints, yPoints, 4);
                        break;
                    case MAGE:
                        g2d.fillOval(GRAVEYARD_X, yPos, smallSize, smallSize);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(GRAVEYARD_X, yPos, smallSize, smallSize);
                        break;
                }
                
                // Draw character type next to the icon
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString(deadCharacter.getType().toString(), 
                    GRAVEYARD_X + smallSize + 10, yPos + smallSize/2 + 5);
            }

            for (int i = 0; i < graveyardTeam2.size(); i++) {
                MatchHero deadCharacter = graveyardTeam2.get(i);
            
                int yPos = WINDOW_HEIGHT/2 + 15 + graveyardY + i * (20+smallSize);
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                g2d.setColor(characterColors.get(deadCharacter));
                
                switch (deadCharacter.getType()) {
                    case FIGHTER:
                        g2d.fillRect(GRAVEYARD_X, yPos, smallSize, smallSize);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(GRAVEYARD_X, yPos, smallSize, smallSize);
                        break;
                    case TANK:
                        int[] xPoints = {GRAVEYARD_X + smallSize/2, GRAVEYARD_X + smallSize, 
                            GRAVEYARD_X + smallSize/2, GRAVEYARD_X};
                        int[] yPoints = {yPos, yPos + smallSize/2, 
                            yPos + smallSize, yPos + smallSize/2};
                        g2d.fillPolygon(xPoints, yPoints, 4);
                        g2d.setColor(Color.BLACK);
                        g2d.drawPolygon(xPoints, yPoints, 4);
                        break;
                    case MAGE:
                        g2d.fillOval(GRAVEYARD_X, yPos, smallSize, smallSize);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(GRAVEYARD_X, yPos, smallSize, smallSize);
                        break;
                }
                
                // Draw character type next to the icon
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString(deadCharacter.getType().toString(), 
                    GRAVEYARD_X + smallSize + 10, yPos + smallSize/2 + 5);
                
                if (deadCharacter.getTeam() == 1) {
                    graveyardY += smallSize + 20;
                }
            }

            
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            
            // Draw damage numbers with actual damage
            Iterator<DamageNumber> damageIterator = damageNumbers.iterator();
            while (damageIterator.hasNext()) {
                DamageNumber damageNumber = damageIterator.next();
                if (currentTime - damageNumber.startTime > DAMAGE_NUMBER_DURATION) {
                    damageIterator.remove();
                } else {
                    float alpha = 1.0f - (currentTime - damageNumber.startTime) / (float)DAMAGE_NUMBER_DURATION;
                    g2d.setFont(new Font("Arial", Font.BOLD, 24));
                    g2d.setColor(new Color(1.0f, 0.0f, 0.0f, alpha));
                    g2d.drawString("-" + damageNumber.damage + " (" + damageNumber.actualDamage + ")", 
                        damageNumber.x + CHARACTER_SIZE/2, 
                        damageNumber.y - 20 - (int)((1-alpha) * 30));
                }
            }
        }
        
        private class DamageNumber {
            int damage;
            int actualDamage;
            int x, y;
            long startTime;
            
            DamageNumber(int damage, int actualDamage, int x, int y, long startTime) {
                this.damage = damage;
                this.actualDamage = actualDamage;
                this.x = x;
                this.y = y;
                this.startTime = startTime;
            }
        }
        
        private class AttackLine {
            Point start;
            Point end;
            long startTime;
            ClassEnum attackerType;
            
            AttackLine(Point start, Point end, long startTime, ClassEnum attackerType) {
                this.start = start;
                this.end = end;
                this.startTime = startTime;
                this.attackerType = attackerType;
            }
        }
    }
}
