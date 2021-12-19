import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 *
 * @author Josh Pfefferkorn
 * Dartmouth CS10, Fall 2020
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		if (p2.getY()<point.getY() && p2.getX()>point.getX()) { // above and to right - quadrant 1
			// check if already a child
			if (this.hasChild(1)) {
				// insert into child
				c1.insert(p2);
			}
			// otherwise create new child, using quadrant bounds as rectangle
			else c1 = new PointQuadtree<E>(p2,(int)point.getX(),y1,x2,(int)point.getY());
		}
		else if (p2.getY()<point.getY() && p2.getX()<point.getX()) { // above and to left- quadrant 2
			if (this.hasChild(2)) {
				c2.insert(p2);
			}
			else c2 = new PointQuadtree<E>(p2,x1,y1,(int)point.getX(),(int)point.getY());
		}

		if (p2.getY()>point.getY() && p2.getX()<point.getX()) { // below and to left - quadrant 3
			if (this.hasChild(3)) {
				c3.insert(p2);
			}
			else c3 = new PointQuadtree<E>(p2,x1,(int)point.getY(),(int)point.getX(),y2);
		}
		if (p2.getY()>point.getY() && p2.getX()>point.getX()) { // below and to right - quadrant 4
			if (this.hasChild(4)) {
				c4.insert(p2);
			}
			else c4 = new PointQuadtree<E>(p2,(int)point.getX(),(int)point.getY(),x2,y2);
		}
	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		int size = 1;
		// check if there's a child
		if (hasChild(1)) {
			// if so, call size() on that child
			size += c1.size();
		}
		if (hasChild(2)) {
			size += c2.size();
		}
		if (hasChild(3)) {
			size += c3.size();
		}
		if (hasChild(4)) {
			size += c4.size();
		}
		return size;
	}

	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		// create list of points
		List<E> points = new ArrayList<E>();
		// call helper method (below)
		addToAllPoints(points);
		return points;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		// create list of points within radius
		List<E> found = new ArrayList<E>();
		// call helper method (below)
		addtoFoundInCircle(found,cx,cy,cr);
		return found;
	}


	// TODO: YOUR CODE HERE for any helper methods

	/**
	 * Helper for allPoints(); adds points to the list
	 */
	private void addToAllPoints(List<E> points) {
		// if no children, add to points list
		if (isLeaf()) { // helper method (below)
			points.add(point);
		}
		// otherwise recursively call method on child
		else {
			if (hasChild(1)) c1.addToAllPoints(points);
			if (hasChild(2)) c2.addToAllPoints(points);
			if (hasChild(3)) c3.addToAllPoints(points);
			if (hasChild(4)) c4.addToAllPoints(points);
		}
	}

	public void addtoFoundInCircle(List<E> found, double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		// if circle intersects rectangle
		if (Geometry.circleIntersectsRectangle(cx,cy,cr,x1,y1,x2,y2)) {
			// and point is within circle
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx,cy,cr)) {
				// add to list of points within radius
				found.add(point);
			}
			// for each quadrant with a child
			if (hasChild(1)) {
				// recursively call method on child
				c1.addtoFoundInCircle(found,cx,cy,cr);
			}
			if (hasChild(2)) {
				c2.addtoFoundInCircle(found,cx,cy,cr);
			}
			if (hasChild(3)) {
				c3.addtoFoundInCircle(found,cx,cy,cr);
			}
			if (hasChild(4)) {
				c4.addtoFoundInCircle(found,cx,cy,cr);
			}
		}
	}

	/**
	 * Returns true if has no children (i.e. is a leaf node)
	 */
	public Boolean isLeaf() {
		return (c1==null && c2 == null && c3 == null && c4 == null);
	}
}
