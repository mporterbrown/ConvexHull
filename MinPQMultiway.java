/***
 * Mason Porter-Brown <mp3902@bard.edu>
 * April 9 2020
 * CMSC 201
 * Lab 6&7: Bounding Box and Convex Hull
 *
 * Collaboration Statement: I worked with Samuel Rallis.
 */

import edu.princeton.cs.algs4.Point2D;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class MinPQMultiway<Key> implements Iterable<Key> {

    private static int D;
    private Key[] pq;                    // store items at indices 1 to n
    private int n;                       // number of items on priority queue
    private Comparator<Key> comparator;  // optional comparator

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param initCapacity the initial capacity of this priority queue
     */
    public MinPQMultiway(int children, int initCapacity) {
        D = children;
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    /**
     * Initializes an empty priority queue.
     */
    public MinPQMultiway(int children) {
        this(children, 1);
    }

    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     *
     * @param initCapacity the initial capacity of this priority queue
     * @param comparator   the order in which to compare the keys
     */
    public MinPQMultiway(int children, int initCapacity, Comparator<Key> comparator) {
        D = children;
        this.comparator = comparator;
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    public MinPQMultiway(Comparator<Key> comparator) {
        this(1, comparator);
    }

    /**
     * Initializes an empty priority queue using the given comparator.
     *
     * @param comparator the order in which to compare the keys
     */
    public MinPQMultiway(int children, Comparator<Key> comparator) {
        this(children, 1, comparator);
    }

    /**
     * Initializes a priority queue from the array of keys.
     * <p>
     * Takes time proportional to the number of keys, using sink-based heap construction.
     *
     * @param keys the array of keys
     */
    public MinPQMultiway(Key[] keys) {
        n = keys.length;
        pq = (Key[]) new Object[keys.length + 1];
        for (int i = 0; i < n; i++)
            pq[i + 1] = keys[i];
        for (int k = n / 2; k >= 1; k--)
            sink(k);
        assert isMinHeap();
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     * {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return n;
    }

    /**
     * Returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > n;
        Key[] temp = (Key[]) new Object[capacity];
        for (int i = 1; i <= n; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
    }

    /**
     * Adds a new key to this priority queue.
     *
     * @param x the key to add to this priority queue
     */
    public void insert(Key x) {
        // double size of array if necessary
        if (n == pq.length - 1) resize(2 * pq.length);

        // add x, and percolate it up to maintain heap invariant
        pq[++n] = x;
        swim(n);
        assert isMinHeap();
    }

    /**
     * Removes and returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key delMin() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        Key min = pq[1];
        exch(1, n--);
        sink(1);
        pq[n + 1] = null;     // to avoid loiterig and help with garbage collection
        if ((n > 0) && (n == (pq.length - 1) / 4)) resize(pq.length / 2);
        assert isMinHeap();
        return min;
    }


    /***************************************************************************
     * Sink and Swim methods, used in insert and delMin.
     ***************************************************************************/

    private void swim(int k) {
        if (k > 1 && greater(parent(k), k)) {
            exch(k, parent(k));
            swim(parent(k));
        }
    }

    private void sink(int i) {
        int child = child(i);
        if (child > n) return;
        int min = minChild(i);
        while (child(i) <= n && greater(i, min)) {
            exch(i, min);
            i = min;
            min = minChild(i);
        }
    }

    /***************************
     * Returns the minimum child
     **************************/

    //Return the minimum child of i
    private int minChild(int i) {
        int loBound = child(i), hiBound = child(i) + (D - 1);
        int min = loBound;
        for (int cur = loBound; cur <= hiBound; cur++) {
            if (cur < n && greater(min, cur)) min = cur;
        }
        return min;
    }

    /***************************************************************************
     * Helper functions for compares and swaps.
     ***************************************************************************/
    private boolean greater(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) pq[i]).compareTo(pq[j]) > 0;
        } else {
            return comparator.compare(pq[i], pq[j]) > 0;
        }
    }

    private void exch(int i, int j) {
        Key swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

    public static int parent(int child) {
        return (child + (D - 2)) / D;
    }

    public static int child(int parent) {
        return (D * parent) - (D - 2);
    }

    // is pq[1..n] a min heap?
    private boolean isMinHeap() {
        for (int i = 1; i <= n; i++) {
            if (pq[i] == null) return false;
        }
        for (int i = n + 1; i < pq.length; i++) {
            if (pq[i] != null) return false;
        }
        if (pq[0] != null) return false;
        return isMinHeapOrdered(1);
    }

    // is subtree of pq[1..n] rooted at k a min heap?
    private boolean isMinHeapOrdered(int k) {

//        if (k > n) return true;
//        int left = 2*k;
//        int right = 2*k + 1;
//        if (left  <= n && greater(k, left))  return false;
//        if (right <= n && greater(k, right)) return false;
//        return isMinHeapOrdered(left) && isMinHeapOrdered(right);

        if (k > n) return true;
        for (int i = 0; i < D; i++) {
            int child = child(k) + i;
            if (child <= n && greater(k, child)) return false;
            return isMinHeapOrdered(child(k) + i);
        }
        return true;
    }


    /**
     * Returns an iterator that iterates over the keys on this priority queue
     * in ascending order.
     * <p>
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the keys in ascending order
     */
    public Iterator<Key> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Key> {
        // create a new pq
        private MinPQMultiway<Key> copy;

        // add all items to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            if (comparator == null) copy = new MinPQMultiway<Key>(size());
            else copy = new MinPQMultiway<Key>(size(), comparator);
            for (int i = 1; i <= n; i++)
                copy.insert(pq[i]);
        }

        public boolean hasNext() {
            return !copy.isEmpty();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Key next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }

    public void display() {
        for (Key x : pq) {
            System.out.println(x);
        }
    }

    /**
     * Unit tests the {@code MinPQ} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        MinPQMultiway<Point2D> pq = new MinPQMultiway<Point2D>(5000);
        pq.insert(new Point2D(1, 3));
        pq.insert(new Point2D(1, 4));
        pq.insert(new Point2D(1, 2));
        pq.insert(new Point2D(5, 6));
        pq.insert(new Point2D(2, 8));
        pq.insert(new Point2D(3, 1));
        pq.insert(new Point2D(15, 2));
        pq.insert(new Point2D(6, 3));
        pq.insert(new Point2D(2, 6));
        pq.insert(new Point2D(1, 100));
        pq.insert(new Point2D(7, 1));

        pq.display();

    }


}
