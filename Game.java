import java.awt.*;
import java.util.ArrayList;


class Game {
    Component container;
    private int ballscount;
    private Ball redBall;
    private ArrayList<Ball> balls;
    private Stage stage;
    private int bs, bl, s; //bs - шаров сбито, bl - шаров лежащих сбито, s - счет
    private int status;
    private Boolean p_lie;
    // p_sk - признак случайного количества шаров
    // p_sr - признак случайного радиуса
    // p_sx - признак случайного сдвига по х
    // p_sy - признак случайного сдвига по у
    // p_sc - признак случайного цвета
    // p_cb - цвет шара
    // p_cbb - цвет шаров
    // p_cs - цвет панели
    // p_lie - если упал - лежит
    // p_speed - скорость(низкая, средняя, высокая)
    // p_cf - цвет фона

    Game(int ballscount, Container container, Boolean p_sk, Boolean p_sr, Boolean p_sx, Boolean p_sy, Boolean p_sc,
         String p_cb, String p_cbb, String p_cs, Boolean p_lie, int p_speed, String p_cf) {
        Color color;
        try {color = Color.decode(p_cf);} catch (NumberFormatException e) {color = Color.decode("#FFFFFF");}
        container.setBackground(color);
        this.p_lie = p_lie;
        if (ballscount > 10000) ballscount = 10000;
        if (ballscount < 1) ballscount = 1;
        if (p_sk) ballscount = myrandom(1, ballscount);
        if (p_speed > 3 || p_speed < 1) p_speed = 1;
        this.ballscount = ballscount;

        this.container = container;
        int cw = container.getWidth();
        int ch = container.getHeight();
        int sx, sy, sw, sh; // for stage
        int bx, by, br, bdx, bdy; // for balls

        bs = 0; // initial values
        bl = 0;
        s = 0;
        status = 1;

        sx = 1;
        sy = (int) ch / 10 * 9;
        sw = (int) cw / 3;
        sh = (int) ch / 15;
        try {color = Color.decode(p_cs);} catch (NumberFormatException e) {color = Color.decode("#000000");}
        stage = new Stage(sx, sy, sw, sh, container, color);

        by = (int) ch / 10 * 8;
        br = (int) ch / 20;
        bx = br + 1;
        bdx = p_speed * myrandom(1, 2);
        bdy = p_speed * (-1) * myrandom(1, 5);
        try {color = Color.decode(p_cb);} catch (NumberFormatException e) {color = Color.decode("#000000");}
        redBall = new Ball(bx, by, bdx, bdy, br, container, color);

        balls = new ArrayList();
        br = br * 2;
        for (int i = 0; i < ballscount; i++) {
            if (p_sr) br = myrandom(5, ch / 3);
            bx = cw / ballscount / 2 + i * cw / ballscount;
            if (p_sx) bx = myrandom(bx - br, bx + br);
            by = br + 1;
            if (p_sy) by = myrandom(by, (int) ch / 10 * 6);
            int maska;
            try {
                color = Color.decode(p_cbb);
                maska = Integer.parseInt(p_cbb.replaceFirst("#", ""), 16);
            } catch (NumberFormatException e) {
                color = Color.decode("#000000");
                maska = 0xFFFFFF;
            }
            if (p_sc) color = new Color(myrandom(1, 0xFFFFFF) | maska);
            balls.add(new Ball(bx, by, 0, 1, br, container, color));
        }
    }

    public int getballscount() {
        return ballscount;
    }

    public int getbs() {
        return bs;
    }

    public int getbl() {
        return bl;
    }

    public int getcount() {
        return s;
    }

    //public void setStatus(int s) {
    //    status = s;
    //}

    public int getStatus() {
        return status;
    }

    public int myrandom(int x, int y) {
        return x + (int) (Math.random() * (y - x));
    }

    public void step() {
        //int st = redBall.step(p_lie);
        status = redBall.step(p_lie);
        redBall.skok(stage);
        int k;
        for (int i = 0; i < ballscount; i++) {
            balls.get(i).step(p_lie);
            k = balls.get(i).boom(redBall);
            balls.get(i).skok(stage);
            switch (k) {
                case 1:  // сбит лежачий
                    bs = bs + 1;
                    bl = bl + 1;
                    s = s + 5;
                    break;
                case 2:  // сбит в полете
                    bs = bs + 1;
                    s = s + 10;
                    break;
            }
        }
       // return st; // лег или не лег redBall
    }

    public void stagestep(int dx, int dy) {
        stage.step(dx, dy);
    }

    public void paint(Graphics g) {
        redBall.paint(g);
        for (int i = 0; i < ballscount; i++) {
            balls.get(i).paint(g);
        }
        stage.paint(g);
    }

    public class Ball {
        private int x, y;
        private int dx, dy;
        private int radius;
        private Component container;
        private Color color;
        private int status; // 1- fly, 0 - lie, -1 - boom

        public Ball(int x, int y, int dx, int dy, int radius, Component container, Color color) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.radius = radius;
            this.container = container;
            this.color = color;
            this.status = 1;
        }

        void paint(Graphics g) {
            if (status != -1) {
                g.setColor(Color.CYAN);
                g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
                g.setColor(color);
                g.fillOval(x - radius, y - radius, 2 * radius - 2, 2 * radius - 2);
            }
        }

        public int step(Boolean p_lie) {
            if (status == 1) {
                x += dx;
                y += dy;
                if (x >= container.getWidth() - radius) {
                    x = container.getWidth() - radius - 1;
                    dx = -dx;

                }
                if (y >= container.getHeight() - radius) {
                    if (!p_lie) {
                        if (dx != 0)  status = 0;
                        y = container.getHeight() - radius - 1;
                        dy = -dy;
                    }
                    else {status  = 0;}
                }
                if (x < radius) {
                    x = radius;
                    dx = -dx;
                }
                if (y < radius) {
                    y = radius;
                    dy = -dy;
                }
            }
            if (status == 0 && dx != 0) return 0; // если redBall лёг - return 0
            else return 1;
        }

        public int boom(Ball ball) {
            int s = 0; // не сбит
            if (Math.pow(Math.pow(this.x - ball.x, 2) + Math.pow(this.y - ball.y, 2), 0.5) <= this.radius + ball.radius) {
                switch (status) {
                    case 0: // сбит лежачий
                        s = 1;
                        break;
                    case 1: // сбит в полете
                        s = 2;
                        break;
                }
                status = -1;
            }
            return s;
        }

        public void skok(Stage stage) {
            if (status == 1) {
                if (this.y + this.radius >= stage.y) {
                    if (this.x >= stage.x && this.x <= stage.x + stage.w) {
                        y = stage.y - 2 * this.radius;
                        dy = -dy;
                    }
                }
            }
        }
    }

    public class Stage {
        private int x, y;
        private int dx, dy;
        private int h, w;
        private Component container;
        private Color color;

        public Stage(int x, int y, int w, int h, Component container, Color color) {
            this.x = x;
            this.y = y;
            this.h = h;
            this.w = w;
            this.container = container;
            this.color = color;
        }

        public void paint(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, w, h);
        }

        public void step(int dx, int dy) {
            if (x + w + dx <= container.getWidth() && x + dx >= 1) x += dx;
            if (y + h + dy <= container.getHeight() && y + dy >= 1) y += dy;
        }
    }
}
