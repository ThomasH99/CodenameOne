
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.plaf.Style;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Positions west container aligned with the left border and east aligned with
 * right border. If possible, will give preferred width to both. If not
 * possible, it will give preferred width to the smallest.
 *
 * @author Thomas
 */
public class SettingsLayout {

    /**
     * The west layout constraint (left of container).
     */
    public static final String WEST = "West";
    /**
     * The east layout constraint (right of container).
     */
    public static final String EAST = "East";

    private boolean scaleEdges = true;
    private Component portraitWest;
    private Component portraitEast;

    private Component getEast() {
        return portraitEast;
    }

    private Component getWest() {
        return portraitWest;
    }

    /**
     * Position the east/west component variables
     */
    private void positionLeftRight(Component c, int targetWidth, int bottom, int top) {
        int y = top + c.getStyle().getMarginTop();
        int h = bottom - top - c.getStyle().getMarginTop() - c.getStyle().getMarginBottom();
        if (scaleEdges) {
            c.setY(y);
            c.setHeight(h);
        } else {
            int ph = c.getPreferredH();
            if (ph < h) {
                c.setHeight(ph);
                c.setY(y + (h - ph) / 2);
            } else {
                c.setY(y);
                c.setHeight(h);
            }
        }
        c.setWidth(Math.min(targetWidth, c.getPreferredW()));
    }

    /**
     * Layout the given parent container children
     *
     * @param parent the given parent container
     */
    public void layoutContainer(Container target) {
        Style s = target.getStyle();
        int top = s.getPaddingTop();
        int bottom = target.getLayoutHeight() - target.getBottomGap() - s.getPaddingBottom();
        int left = s.getPaddingLeft(target.isRTL());
        int right = target.getLayoutWidth() - target.getSideGap() - s.getPaddingRight(target.isRTL());
        int targetWidth = target.getWidth();
        int targetHeight = target.getHeight();

        boolean rtl = target.isRTL();
        if (rtl) {
            left += target.getSideGap();
        }

        Component east = getEast();
        Component west = getWest();

        Component realEast = east;
        Component realWest = west;

        if (rtl) {
            realEast = west;
            realWest = east;
        }

        if (realEast != null) {
            Component c = realEast;
            positionLeftRight(realEast, targetWidth, bottom, top);
            c.setX(right - c.getWidth() - c.getStyle().getMarginRight(rtl));
            right -= (c.getWidth() + c.getStyle().getHorizontalMargins());
        }
        if (realWest != null) {
            Component c = realWest;
            positionLeftRight(realWest, targetWidth, bottom, top);
            c.setX(left + c.getStyle().getMarginLeft(rtl));
            left += (c.getWidth() + c.getStyle().getMarginLeftNoRTL() + c.getStyle().getMarginRightNoRTL());
        }
    }

    /**
     * Returns the container preferred size
     *
     * @param parent the parent container
     * @return the container preferred size
     */
    public Dimension getPreferredSize(Container parent) {
        return null;
    }

    /**
     * Some layouts can optionally track the addition of elements with meta-data
     * that allows the user to "hint" on object positioning.
     *
     * @param value optional meta data information, like alignment orientation
     * @param comp the added component to the layout
     * @param c the parent container
     */
    public void addLayoutComponent(Object value, Component comp, Container c) {
        if (value != null) {
            throw new IllegalStateException("Layout doesn't support adding with arguments: " + getClass().getName());
        }
    }

    /**
     * Removes the component from the layout this operation is only useful if
     * the layout maintains references to components within it
     *
     * @param comp the removed component from layout
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the optional component constraint
     *
     * @param comp the component whose constraint should be returned
     * @return the optional component constraint
     */
    public Object getComponentConstraint(Component comp) {
        return null;
    }

    /**
     * This method returns true if the Layout allows Components to Overlap.
     *
     * @return true if Components may intersect in this layout
     */
    public boolean isOverlapSupported() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass();
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return getClass().getName().hashCode();
    }

    /**
     * If this method returns true, the addLayoutComponent method will be called
     * when replacing a layout for every component within the container
     *
     * @return false by default
     */
    public boolean isConstraintTracking() {
        return false;
    }

    /**
     * Some layout managers can obscure their child components in some cases
     * this returns true if the basic underpinnings are in place for that. This
     * method doesn't take padding/margin etc. into account since that is
     * checked by the caller
     *
     * @param parent parent container
     * @return true if there is a chance that this layout manager can fully
     * obscure the background, when in doubt return false...
     */
    public boolean obscuresPotential(Container parent) {
        return false;
    }
}
