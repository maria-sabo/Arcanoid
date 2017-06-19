import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

class Main {
    public static class MainFrame extends JFrame {
        private String p_kb = "15", p_cf = "#66CDAA", p_cb = "#DC143C", p_cs = "#CD853F", p_cbb = "#FFEFD5";
        private Boolean p_sk = false, p_sx = true, p_sy = true, p_sr = false, p_sc = false, p_lie = false;
        private String spi = "15, #FFEFD5, #DC143C, #CD853F, #66CDAA, false, true, true, false, false, false, 2";
        private int p_speed = 2;
        ParamFrame paramFrame;
        MainPanel pCenter;
        PanelNorth pNorth;
        PanelSouth pSouth;
        public MainFrame(String s) {
            super(s);
            Toolkit kit = Toolkit.getDefaultToolkit();
            Dimension screenSize = kit.getScreenSize();
            int screenWidth = screenSize.width;
            int screenHeight = screenSize.height;
            //int WIDTH = 600;
            //int HEIGHT = 470;
            int WIDTH = screenWidth / 2;
            int HEIGHT = screenHeight / 4 * 3;
            setSize(WIDTH, HEIGHT);

            paramFrame = new ParamFrame("Options");
            readparam();
            pCenter = new MainPanel();
            pNorth = new PanelNorth();
            pSouth = new PanelSouth();
            JMenuBar menuBar = new JMenuBar();
            JMenu iMenu = new JMenu("Game");

            JMenuItem playItem = new JMenuItem("New game");
            iMenu.add(playItem);
            iMenu.addSeparator();
            JMenuItem exitItem = new JMenuItem("Exit");
            iMenu.add(exitItem);

            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            playItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    startNewGame(paramFrame, pNorth, pCenter);
                }
            });

            JMenu hMenu = new JMenu("Options");
            JMenuItem helpItem = new JMenuItem("Help");
            hMenu.add(helpItem);
            hMenu.addSeparator();
            JMenuItem paramItem = new JMenuItem("Options");
            hMenu.add(paramItem);

            helpItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        new helpFrame("Game rules");
                    } catch (FileNotFoundException e1) {
                        JOptionPane.showConfirmDialog(helpItem, e1.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

                    }
                }
            });
            paramItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    paramFrame.setVisible(true);
                }
            });

            menuBar.add(iMenu);
            menuBar.add(hMenu);
            setJMenuBar(menuBar);

            ActionListener buttonListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    switch (e.getActionCommand()) {
                        case "Left":
                            pCenter.pstep(-30 * p_speed, 0);
                            break;
                        case "Right":
                            pCenter.pstep(30 * p_speed, 0);
                            break;
                        case "Up":
                            pCenter.pstep(0, -30 * p_speed);
                            break;
                        case "Down":
                            pCenter.pstep(0, 30 * p_speed);
                            break;
                        case "Play":
                            pSouth.b_stop.setText("Stop");
                            pCenter.timer.start();
                            pNorth.timer.start();
                            break;
                        case "Stop":
                            pSouth.b_stop.setText("Play");
                            pCenter.timer.stop();
                            pNorth.timer.stop();
                            break;
                        case "New Game":
                            startNewGame(paramFrame, pNorth, pCenter);
                    }
                }
            };
            KeyListener Kl = new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        pCenter.pstep(10 * p_speed, 0);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        pCenter.pstep(-10 * p_speed, 0);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        pCenter.pstep(0, -10 * p_speed);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        pCenter.pstep(0, 10 * p_speed);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        System.exit(0);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
                        startNewGame(paramFrame, pNorth, pCenter);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (pSouth.b_stop.getText()== "Play") {
                            pSouth.b_stop.setText("Stop");
                            pCenter.timer.start();
                            if (pCenter.game.getStatus() != 0) pNorth.timer.start();
                            e.setKeyCode(0);
                        }
                        else
                        if (pSouth.b_stop.getText() ==  "Stop") {
                            pSouth.b_stop.setText("Play");
                            pCenter.timer.stop();
                            pNorth.timer.stop();
                            e.setKeyCode(0);
                        }
                    }
                }

                public void keyReleased(KeyEvent e) {
                }

                public void keyTyped(KeyEvent e) {
                }

            };
            this.addKeyListener(Kl);
            pNorth.b.addKeyListener(Kl);
            pSouth.b_stop.addKeyListener(Kl);
            pSouth.b_game.addKeyListener(Kl);
            pSouth.b_left.addKeyListener(Kl);
            pSouth.b_right.addKeyListener(Kl);
            pSouth.b_up.addKeyListener(Kl);
            pSouth.b_down.addKeyListener(Kl);

            pSouth.b_stop.addActionListener(buttonListener);
            pSouth.b_game.addActionListener(buttonListener);
            pSouth.b_left.addActionListener(buttonListener);
            pSouth.b_right.addActionListener(buttonListener);
            pSouth.b_up.addActionListener(buttonListener);
            pSouth.b_down.addActionListener(buttonListener);

            add(pNorth, BorderLayout.NORTH);
            add(pSouth, BorderLayout.SOUTH);
            add(pCenter, BorderLayout.CENTER);

            setVisible(true);
            startNewGame(paramFrame, pNorth, pCenter);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        }
        public String readparam() {
            BufferedReader reader = null;
            String line = "";
            try {
                reader = new BufferedReader(new FileReader("arcparam.txt"));
                try {
                    line = reader.readLine();
                } catch (IOException e) {line = spi;}
            } catch (FileNotFoundException e) {line = spi;}
            ArrayList<String> pl = new ArrayList<>(Arrays.asList(line.split(", ")));

            try {
                p_kb = (pl.get(0));
                p_cbb = (pl.get(1));
                p_cb = (pl.get(2));
                p_cs = (pl.get(3));
                p_cf = (pl.get(4));

                p_sk = (Boolean.valueOf(pl.get(5)));
                p_sx = (Boolean.valueOf(pl.get(6)));
                p_sy = (Boolean.valueOf(pl.get(7)));
                p_sr = (Boolean.valueOf(pl.get(8)));
                p_sc = (Boolean.valueOf(pl.get(9)));
                p_lie = (Boolean.valueOf(pl.get(10)));
                p_speed = (Integer.valueOf(pl.get(11)));
            } catch (IndexOutOfBoundsException er) {}
            catch (NumberFormatException er) {}

            paramFrame.jtf1.setText(p_kb);
            paramFrame.jtf2.setText(p_cbb);
            paramFrame.jtf3.setText(p_cb);
            paramFrame.jtf4.setText(p_cs);
            paramFrame.jtf5.setText(p_cf);

            paramFrame.jcb1.setSelected(p_sk);
            paramFrame.jcb2.setSelected(p_sr);
            paramFrame.jcb3.setSelected(p_sx);
            paramFrame.jcb4.setSelected(p_sy);
            paramFrame.jcb5.setSelected(p_sc);
            paramFrame.jcb6.setSelected(p_lie);

            if (p_speed == 1) paramFrame.b1.setSelected(true);
            if (p_speed == 2) paramFrame.b2.setSelected(true);
            if (p_speed == 3) paramFrame.b3.setSelected(true);

            return "";
        }
        public void startNewGame (ParamFrame paramFrame, PanelNorth pNorth, MainPanel pCenter) {
            p_kb = paramFrame.getkb();
            int ikb;
            try {ikb = Integer.valueOf(p_kb);}catch (NumberFormatException e2) {ikb = 1;}
            p_cf = paramFrame.getcf();
            p_cb = paramFrame.getcb();
            p_cs = paramFrame.getcs();
            p_cbb = paramFrame.getcbb();

            p_sk = paramFrame.getsb();
            p_sx = paramFrame.getsx();
            p_sy = paramFrame.getsy();
            p_sr = paramFrame.getsr();
            p_sc = paramFrame.getsc();
            p_lie = paramFrame.getlie();
            p_speed = paramFrame.getradio();
            pCenter.game = new Game(ikb, pCenter, p_sk, p_sr, p_sx, p_sy, p_sc, p_cb, p_cbb, p_cs, p_lie, p_speed, p_cf);
            pCenter.timer.start();
            pNorth.iniT();
            pNorth.timer.start();
        }

        public class PanelSouth extends JPanel {
            JButton b_game;
            JButton b_stop;
            JButton b_left;
            JButton b_right;
            JButton b_up;
            JButton b_down;

            public PanelSouth() {
                super();
                b_game  = new JButton ("New Game");
                b_stop = new JButton("Stop");
                b_left = new JButton("Left");
                b_right = new JButton("Right");
                b_up = new JButton("Up");
                b_down = new JButton("Down");
                add(b_game);
                add(b_stop);
                add(b_left);
                add(b_right);
                add(b_up);
                add(b_down);
            }
        }

        public class PanelNorth extends JPanel {
            private Timer timer;
            long t = 0;
            ActionListener timerListener;
            JLabel b = new JLabel("");

            public PanelNorth() {
                super();
                timerListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        t = t + 10;
                        int ba, bs, bl, s, pr;

                        ba = pCenter.game.getballscount();
                        bs = pCenter.game.getbs();
                        bl = pCenter.game.getbl();
                        s = pCenter.game.getcount();
                        pr = ((((bs - bl) * 2 + bl) * 50) / ba);

                        b.setText(String.format("Balls %d        Shot balls - %d (lying %d)        Score - %d         Percent - %d       Time - %tM:%tS:%tL", ba, bs, bl, s, pr, t, t, t));
                        if ((pCenter.game.getStatus() == 0) || (ba == bs))
                            timer.stop();
                    }
                };
                timer = new Timer(10, timerListener);
                add(b);
            }

            public void iniT() {
                t = 0;
            }
        }

        public class MainPanel extends JPanel {
            private Timer timer;
            ActionListener timerListener;
            KeyListener Kl;
            long ts;
            Game game;

            public MainPanel() {
                super();
                timerListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                       // game.setStatus(game.step());
                        game.step();
                        repaint();
                    }
                };
                timer = new Timer(10, timerListener);
            }

            public void pstep(int dx, int dy) {
                game.stagestep(dx, dy);
            }

            public void paint(Graphics g) {
                super.paint(g);
                game.paint(g);
            }
        }

    }

    public static class helpFrame extends JFrame {
        JTextArea textArea;

        public helpFrame(String s) throws FileNotFoundException {
            super(s);
            // setSize(300, 400);
            FileReaderClass myIzfile1 = new FileReaderClass("archelp.txt");
            textArea = new JTextArea();
            //textArea.setBounds(1, 1, 200, 300);
            textArea.setEditable(false);
            textArea.append(myIzfile1.read());
            add(textArea);
            pack();
            setVisible(true);
            // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        public class FileReaderClass {
            FileReader fr;

            public FileReaderClass(String s) throws FileNotFoundException {
                fr = new FileReader(s);
            }

            public String read() {
                StringBuilder sb = new StringBuilder();
                try {
                    String s;
                    BufferedReader br = new BufferedReader(fr);
                    LineNumberReader lr = new LineNumberReader(br);
                    while ((s = lr.readLine()) != null) {
                        sb.append(s);
                        sb.append("\n");
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                return sb.toString();
            }
        }
    }

    public static class ParamFrame  extends JFrame {
        private JTextField jtf1, jtf2, jtf3, jtf4, jtf5;
        private JCheckBox jcb1, jcb2, jcb3, jcb4, jcb5, jcb6;
        private JRadioButton b1,b2,b3;
        public ParamFrame(String s) {
            super(s);
            JPanel jp = new JPanel();
            JPanel jn = new JPanel();
            JPanel js = new JPanel();

            add(jn, BorderLayout.NORTH);
            add(js, BorderLayout.SOUTH);
            add(jp, BorderLayout.CENTER);

            JButton jb = new JButton("Save to file");
            jb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    savetofile();
                    setVisible(false);
                }
            });

            JButton jbc = new JButton("Close");
            jbc.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });

            ButtonGroup bg = new ButtonGroup();
            b1 = new JRadioButton("Low", true);
            b2 = new JRadioButton("Average", false);
            b3 = new JRadioButton("High", false);
            bg.add(b1);
            bg.add(b2);
            bg.add(b3);

            JLabel jl1 = new JLabel("  Number of balls");
            JLabel jl2 = new JLabel("  Color of balls (HEX)");
            JLabel jl3 = new JLabel("  Color of the ball (HEX)");
            JLabel jl4 = new JLabel("  Color of the stage (HEX)");
            JLabel jl5 = new JLabel("  Background color (HEX)");

            JLabel jl6 = new JLabel(" ");
            JLabel jl7 = new JLabel("Speed:");
            JLabel jl8 = new JLabel(" ");

            JLabel jl9 = new JLabel(" ");
            JLabel jl10 = new JLabel(" ");

            JLabel jl11 = new JLabel(" ");
            JLabel jl12 = new JLabel(" ");

            jtf1 = new JTextField();
            jtf2 = new JTextField();
            jtf3 = new JTextField();
            jtf4 = new JTextField();
            jtf5 = new JTextField();

            jcb1 = new JCheckBox("  Random number of balls");
            jcb2 = new JCheckBox("  Random radius");
            jcb3 = new JCheckBox("  Random shift along Х");
            jcb4 = new JCheckBox("  Random shift along Y");
            jcb5 = new JCheckBox("  Random color");
            jcb6 = new JCheckBox("  If it falls it lies");

            jp.setLayout(new GridLayout(10, 2, 1, 1));
            jp.add(jl9);
            jp.add(jl10);
            jp.add(jl1);
            jp.add(jtf1);
            jp.add(jl2);
            jp.add(jtf2);
            jp.add(jl3);
            jp.add(jtf3);
            jp.add(jl4);
            jp.add(jtf4);
            jp.add(jl5);
            jp.add(jtf5);
            jp.add(jcb1);
            jp.add(jcb2);
            jp.add(jcb3);
            jp.add(jcb4);
            jp.add(jcb5);
            jp.add(jcb6);

            jn.setLayout(new GridLayout(2,3, 1, 1));
            jn.add(jl6);
            jn.add(jl7);
            jn.add(jl8);
            jn.add(b1);
            jn.add(b2);
            jn.add(b3);

            js.setLayout(new GridLayout(2,2, 5, 10));
            js.add(jb);
            js.add(jbc);
            js.add(jl11);
            js.add(jl12);

            pack();

        }
        public String getkb() { return jtf1.getText(); }
        public String getcbb() { return jtf2.getText(); }
        public String getcb() { return jtf3.getText(); }
        public String getcs() { return jtf4.getText(); }
        public String getcf() { return jtf5.getText(); }
        public Boolean getsb() { return jcb1.isSelected(); }
        public Boolean getsr() { return jcb2.isSelected(); }
        public Boolean getsx() { return jcb3.isSelected(); }
        public Boolean getsy() { return jcb4.isSelected(); }
        public Boolean getsc() { return jcb5.isSelected(); }
        public Boolean getlie() { return jcb6.isSelected(); }
        public int getradio() {
            if (b1.isSelected()) return 1;
            if (b2.isSelected()) return 2;
            if (b3.isSelected()) return 3;
            return 1;
        }

        public void savetofile() {
            ArrayList<String> pl;
            ArrayList<String> paramList = new ArrayList();
            paramList.add(getkb());
            paramList.add(getcbb());
            paramList.add(getcb());
            paramList.add(getcs());
            paramList.add(getcf());

            paramList.add(String.valueOf(getsb()));
            paramList.add(String.valueOf(getsr()));
            paramList.add(String.valueOf(getsx()));
            paramList.add(String.valueOf(getsy()));
            paramList.add(String.valueOf(getsc()));
            paramList.add(String.valueOf(getlie()));
            paramList.add(String.valueOf(getradio()));

            String s = paramList.toString();
            s = s.replace("[", "");
            s = s.replace("]", "");
            //pl = new ArrayList<String>(Arrays.asList(s.split(",")));;

            FileWriter writeFile = null;
            try {
                File logFile = new File("arcparam.txt");
                writeFile = new FileWriter(logFile);
                writeFile.write(s + "\n");
                JOptionPane.showConfirmDialog(this, "Save " + s, "Message box", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //**********************************************************
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame("Arcanoid (Bubble)");
            }
        });
    }
}
