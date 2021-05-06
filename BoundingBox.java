/***
 * Mason Porter-Brown <mp3902@bard.edu>
 * April 9 2020
 * CMSC 201
 * Lab 6&7: Bounding Box and Convex Hull
 *
 * Collaboration Statement: I worked with Samuel Rallis.
 */

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Bag;

import java.util.Iterator;


public class BoundingBox {

    Bag b = new Bag();
    Point2D top, bottom, left, right;

    public final static int W = 800;
    public final static int H = 800;
    public final static int N = 150;

    public void add(Point2D item) {
        Point2D temp = item;

        if (b.isEmpty()) {
            top = temp;
            bottom = temp;
            left = temp;
            right = temp;
        }
        if (temp.y() > top.y()) {
            top = temp;
        }
        if (temp.y() < bottom.y()) {
            bottom = temp;
        }
        if (temp.x() > right.x()) {
            right = temp;
        }
        if (temp.x() < left.x()) {
            left = temp;
        }

        b.add(item);
    }

    public boolean isEmpty() {
        return b.isEmpty();
    }

    public int size() {
        return b.size();
    }

    public Point2D top() {
        return top;
    }

    public Point2D right() {
        return right;
    }

    public Point2D left() {
        return left;
    }

    public Point2D bottom() {
        return bottom;
    }

    public Point2D centroid() {
        double x_val, y_val;
        double x_s = 0, y_s = 0;
        for (Iterator i = b.iterator(); i.hasNext(); ) {
            Point2D temp = (Point2D) i.next();
            x_s += temp.x();
            y_s += temp.y();
        }
        return new Point2D(x_s / b.size(), y_s / b.size());
    }


    public static void main(String[] args) {
        BoundingBox bb = new BoundingBox();

        StdDraw.setCanvasSize(W, H);
        StdDraw.setXscale(0, W);
        StdDraw.setYscale(0, H);
        StdDraw.setPenColor(StdDraw.BOOK_BLUE);

        for (int i = 0; i < N; i++) {
            Point2D p = new Point2D(StdRandom.gaussian(W / 2, W / 8),
                    StdRandom.gaussian(H / 2, H / 8));
            bb.add(p);
            StdDraw.filledCircle(p.x(), p.y(), 3);
        }

        Point2D left = bb.left();
        Point2D right = bb.right();
        Point2D top = bb.top();
        Point2D bottom = bb.bottom();
        StdDraw.setPenColor(StdDraw.CYAN);
        StdDraw.line(right.x(), bottom.y(), right.x(), top.y());
        StdDraw.line(left.x(), bottom.y(), left.x(), top.y());
        StdDraw.line(left.x(), bottom.y(), right.x(), bottom.y());
        StdDraw.line(left.x(), top.y(), right.x(), top.y());
    }
}