package schmitt.joao.aco;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Visualizer extends JFrame {

    int viewWidth;
    int viewHeight;
    int width;
    int height;
    double scaleW;
    double scaleH;

    ArrayList<Node> nodes;
    ArrayList<edge> edges;

    double[][] coordinates;

    public Visualizer(double[][] coordinates) {
        super();
        this.coordinates = coordinates;
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nodes = new ArrayList<Node>();
        edges = new ArrayList<edge>();
        viewWidth = 1200;
        viewHeight = 700;
        width = 1;
        height = 1;
        for(int i = 0; i < coordinates.length; i++) {
            if(coordinates[i][0] > scaleW) scaleW = (int) coordinates[i][0];
            if(coordinates[i][1] > scaleH) scaleH = (int) coordinates[i][1];
        }
        scaleW = viewWidth / scaleW;
        scaleH = viewHeight / scaleH;
        scaleW *= .9;
        scaleH *= .9;
        this.setSize(viewWidth + (int) scaleW, viewHeight + (int) scaleH);
        this.setVisible(true);
    }

    public void draw(int[] tour) {
        this.nodes.clear();
        this.edges.clear();
        for(int i = 0; i < coordinates.length; i++) {
            int x = (int) (coordinates[i][0] * scaleW);
            int y = (int) (coordinates[i][1] * scaleH);
            this.addNode(String.valueOf(i), x, y);
        }
        for(int i = 0; i < tour.length - 1; i++) {
            this.addEdge(tour[i], tour[i + 1]);
        }
        this.repaint();
    }

    class Node {
        int x, y;
        String name;

        public Node(String myName, int myX, int myY) {
            x = myX;
            y = myY;
            name = myName;
        }
    }

    class edge {
        int i,j;

        public edge(int ii, int jj) {
            i = ii;
            j = jj;
        }
    }

    public void addNode(String name, int x, int y) {
        //add a node at pixel (x,y)
        nodes.add(new Node(name,x,y));
    }
    public void addEdge(int i, int j) {
        //add an edge between nodes i and j
        edges.add(new edge(i,j));
    }

    public void paint(Graphics g) { // draw the nodes and edges
        super.paint(g);
        FontMetrics f = g.getFontMetrics();
        int nodeHeight = Math.max(height, f.getHeight());

        g.setColor(Color.black);
        for (edge e : edges) {
            g.drawLine(nodes.get(e.i).x, nodes.get(e.i).y,
                    nodes.get(e.j).x, nodes.get(e.j).y);
        }

        for (Node n : nodes) {
            int nodeWidth = Math.max(width, f.stringWidth(n.name)+width/2);
            g.setColor(Color.white);
            g.fillOval(n.x-nodeWidth/2, n.y-nodeHeight/2,
                    nodeWidth, nodeHeight);
            g.setColor(Color.black);
            g.drawOval(n.x-nodeWidth/2, n.y-nodeHeight/2,
                    nodeWidth, nodeHeight);

            g.drawString(n.name, n.x-f.stringWidth(n.name)/2,
                    n.y+f.getHeight()/2);
        }
    }
}
