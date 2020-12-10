import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;

public class GraphicsDisplay extends JPanel {

    private ArrayList<Double[]> graphicsData;
    private ArrayList<Double[]> originalData;
    private int selectedMarker = -1;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scaleX;
    private double scaleY;
    private double[][] viewport = new double[2][2];
    private ArrayList<double[][]> undoHistory = new ArrayList<>(5);
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private Font axisFont;
    private Font labelsFont;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke gridStroke;
    private BasicStroke selectionStroke;
    private static DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance();
    private boolean scaleMode = false;
    private boolean changeMode = false;
    private double[] originalPoint = new double[2];
    private java.awt.geom.Rectangle2D.Double selectionRect = new java.awt.geom.Rectangle2D.Double();

    public GraphicsDisplay() {
        this.setBackground(Color.WHITE);
        this.axisStroke = new BasicStroke(2.0F, 0, 1, 10.0F, null, 0.0F);
        this.markerStroke = new BasicStroke(1.5F, 0, 1, 5.0F, null, 0.0F);
        this.selectionStroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[]{10.0F, 10.0F}, 0.0F);

        this.gridStroke = new BasicStroke(0.5F, 0, 1, 5.0F, new float[]{5.0F, 5.0F}, 2.0F);
        this.axisFont = new Font("Serif", 1, 36);
        this.labelsFont = new Font("Serif", 0, 10);
        this.addMouseMotionListener(new GraphicsDisplay.MouseMotionHandler());
        this.addMouseListener(new GraphicsDisplay.MouseHandler());
    }

    public void showGraphics(ArrayList<Double[]> graphicsData) {
        this.graphicsData = graphicsData;
        this.originalData = new ArrayList<>(graphicsData.size());
        Iterator var3 = graphicsData.iterator();

        while(var3.hasNext()){
            Double[] point = (Double[])var3.next();
            Double[] newPoint = new Double[]{point[0], point[1]};
            this.originalData.add(newPoint);
        }

        this.minX = (graphicsData.get(0))[0];
        this.maxX = (graphicsData.get(graphicsData.size() - 1))[0];
        this.minY = (graphicsData.get(0))[1];
        this.maxY = this.minY;

        for(int i = 1; i < graphicsData.size(); ++i) {
            if ((graphicsData.get(i))[1] < this.minY) {
                this.minY = (graphicsData.get(i))[1];
            }

            if ((graphicsData.get(i))[1] > this.maxY) {
                this.maxY = (graphicsData.get(i))[1];
            }
        }

        this.zoomToRegion(this.minX, this.maxY, this.maxX, this.minY);
    }

    public void zoomToRegion(double x1, double y1, double x2, double y2) {
        this.viewport[0][0] = x1;
        this.viewport[0][1] = y1;
        this.viewport[1][0] = x2;
        this.viewport[1][1] = y2;
        this.repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        this.repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        this.repaint();
    }

    protected java.awt.geom.Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - this.viewport[0][0];
        double deltaY = this.viewport[0][1] - y;
        return new java.awt.geom.Point2D.Double(deltaX * this.scaleX, deltaY * this.scaleY);
    }

    protected double[] translatePointToXY(int x, int y) {
        return new double[]{this.viewport[0][0] + (double)x / this.scaleX, this.viewport[0][1] - (double)y / this.scaleY};
    }



}