package com.bc.wechat.utils;

public class DistanceUtil {
    private static final double EARTH_RADIUS = 6378137.0;

    public static double getDistance(double longitude, double latitue, double longitude2, double latitue2) {
        double lat1 = rad(latitue);
        double lat2 = rad(latitue2);
        double a = lat1 - lat2;
        double b = rad(longitude) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static String getDistancePretty(double distance, String district) {
        String distancePretty = "";
        int distanceIntValue = (int) Math.ceil(distance);
        if (distanceIntValue <= 100) {
            distancePretty = "100米以内";
        } else if (distanceIntValue > 100 && distanceIntValue <= 200) {
            distancePretty = "200米以内";
        } else if (distanceIntValue > 200 && distanceIntValue <= 300) {
            distancePretty = "300米以内";
        } else if (distanceIntValue > 300 && distanceIntValue <= 400) {
            distancePretty = "400米以内";
        } else if (distanceIntValue > 400 && distanceIntValue <= 500) {
            distancePretty = "500米以内";
        } else if (distanceIntValue > 500 && distanceIntValue <= 600) {
            distancePretty = "600米以内";
        } else if (distanceIntValue > 600 && distanceIntValue <= 700) {
            distancePretty = "700米以内";
        } else if (distanceIntValue > 700 && distanceIntValue <= 800) {
            distancePretty = "800米以内";
        } else if (distanceIntValue > 800 && distanceIntValue <= 900) {
            distancePretty = "900米以内";
        } else if (distanceIntValue > 900 && distanceIntValue <= 1000) {
            distancePretty = "1000米以内";
        } else if (distanceIntValue > 1000 && distanceIntValue <= 2000) {
            distancePretty = "2公里以内" + " - " + district;
        } else if (distanceIntValue > 2000 && distanceIntValue <= 3000) {
            distancePretty = "3公里以内" + " - " + district;
        } else if (distanceIntValue > 3000 && distanceIntValue <= 4000) {
            distancePretty = "4公里以内" + " - " + district;
        } else if (distanceIntValue > 4000 && distanceIntValue <= 5000) {
            distancePretty = "5公里以内" + " - " + district;
        } else if (distanceIntValue > 5000 && distanceIntValue <= 6000) {
            distancePretty = "6公里以内" + " - " + district;
        } else if (distanceIntValue > 6000 && distanceIntValue <= 7000) {
            distancePretty = "7公里以内" + " - " + district;
        } else if (distanceIntValue > 7000 && distanceIntValue <= 8000) {
            distancePretty = "8公里以内" + " - " + district;
        } else if (distanceIntValue > 8000 && distanceIntValue <= 9000) {
            distancePretty = "9公里以内" + " - " + district;
        } else if (distanceIntValue > 9000 && distanceIntValue <= 10000) {
            distancePretty = "10公里以内" + " - " + district;
        } else {
            distancePretty = "20公里以内" + " - " + district;
        }
        return distancePretty;
    }

}
