package ru.yuriyyurinskiy.perceptron.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import ru.yuriyyurinskiy.perceptron.Entity.Line;
import ru.yuriyyurinskiy.perceptron.Entity.Point;

/**
 * 29.11.2017  19:21
 *
 * @author Yuriy Yurinskiy
 */
public class DrawPanel extends JPanel {

    private int width = 580,
            height = 380,
            width_0 = 20,
            height_0 = 20,
            width_1 = width - width_0,
            height_1 = height + height_0,

            x_0 = width / 2,
            y_0 = height / 2 + height_0,
            tab = 20;

    private List<Point> points;
    private List<Line> lines = new ArrayList<>();

    public DrawPanel() {
        this(new ArrayList<>());
        setBorder(BorderFactory.createTitledBorder("Графическое предстваление"));
        setPreferredSize(new Dimension(width,height));
    }

    private DrawPanel(List<Point> points) {
        this.points = points;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        drawBorder(g);
        drawCoordinates(g);

        for (int i = 0; i < points.size(); i++) {
            drawPoint(g, points.get(i));
        }

        for (int i = 0; i < lines.size(); i++) {
            drawLine(g, lines.get(i));
        }
    }

    public void addPoint(Point point) {
        points.add(point);
        repaint();
    }

    public void addAllPoints(List<Point> points) {
        this.points.addAll(points);
        repaint();
    }

    public void clearPoints() {
        points.clear();
        repaint();
    }

    public void addLine(Line line) {
        this.lines.add(line);
        repaint();
    }

    public void addAllLines(List<Line> lines) {
        this.lines.addAll(lines);
        repaint();
    }

    public void clearLines() {
        lines.clear();
        repaint();
    }

    public double getBorderX() {
        return (width_1 - x_0) / tab;
    }

    public double getBorderY() {
        return (height_1 - y_0) / tab;
    }

    private void drawBorder(Graphics g) {
        g.drawLine(width_0, height_0, width_0, height_1);
        g.drawLine(width_0, height_0, width_1, height_0);
        g.drawLine(width_0, height_1, width_1, height_1);
        g.drawLine(width_1, height_0, width_1, height_1);
    }

    private void drawCoordinates(Graphics g) {
        g.drawLine(x_0, height_0, x_0, height_1);
        g.drawLine(width_0, y_0, width_1, y_0);

        int temp = 0;
        while (width_1 >= x_0 + tab * temp) {
            g.drawLine(x_0 + tab * temp, y_0 - 2, x_0 + tab * temp, y_0 + 2);
            g.drawLine(x_0 - tab * temp, y_0 - 2, x_0 - tab * temp, y_0 + 2);
            temp++;
        }

        temp = 0;
        while (height_1 >= y_0 + tab * temp) {
            g.drawLine(x_0 - 2, y_0  + tab * temp, x_0 + 2, y_0 + tab * temp);
            g.drawLine(x_0 - 2, y_0  - tab * temp, x_0 + 2, y_0 - tab * temp);
            temp++;
        }
    }

    private void drawPoint(Graphics g, Point point) {
        setDrawColor(g,point.getType());

        g.fillOval(
                (int) (x_0 + tab * point.getX()) - 2,
                (int) (y_0 - tab * point.getY()) - 2,
                4,
                4
        );
        g.drawOval(
                (int) (x_0 + tab * point.getX()) - 2,
                (int) (y_0 - tab * point.getY()) - 2,
                4,
                4
        );
    }

    private void drawLine(Graphics g, Line line) {
        setDrawColor (g,line.getType());

        g.drawLine(
                (int) (x_0 + tab * line.getX_1()),
                (int) (y_0 - tab * line.getY_1()),
                (int) (x_0 + tab * line.getX_2()),
                (int) (y_0 - tab * line.getY_2())
        );
    }

    private void setDrawColor(Graphics g, int type) {
        switch (type) {
            case 0:
                g.setColor(Color.BLUE);
                break;
            case 1:
                g.setColor(Color.RED);
                break;
            case 2:
                g.setColor(Color.MAGENTA);
                break;
            default:
                g.setColor(Color.GREEN);
                break;
        }
    }
}
