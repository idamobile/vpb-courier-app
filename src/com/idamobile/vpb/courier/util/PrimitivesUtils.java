package com.idamobile.vpb.courier.util;

/**
 * @author colriot
 * @since Aug 8, 2012
 *
 */
public class PrimitivesUtils {

    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(float x, float y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(double x, double y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
}
