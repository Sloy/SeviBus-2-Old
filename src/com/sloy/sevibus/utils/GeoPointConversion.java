/*
 * Código extraído del proyecto mapdroid
 * http://www.java2s.com/Open-Source/Android/Map/mapdroid/org/mapdroid/utils/GeoPointConversion.java.htm
 */

package com.sloy.sevibus.utils;

import com.google.android.maps.GeoPoint;

public class GeoPointConversion{  
	  private static final double rad2deg = 180.0 / Math.PI;
	  //These values describes the model we're using for our computation.
	  //We're using the WGS-84 Ellipsoid Model! 
	  private static final double a =       6378137;    //Equatorial Radius
	  private static final double eccSquared = 0.00669438;    //Eccentricity Squared
	  //-----------------------------------------------------------------
	  private static final double k0 = 0.9996;          //Scale along central meridian of zone
	  public static final String UTM_ZONE = "UTM_ZONE";
	  public static final String UTM_VALUE = "UTM_VALUE";

	      
	  public static GeoPoint utmToGeoPoint(double utmEasting, double utmNorthing, String utmZone){
	  //converts UTM coords to lat/long.  Equations from USGS Bulletin 1532 
	  //East Longitudes are positive, West longitudes are negative. 
	  //North latitudes are positive, South latitudes are negative
	  //Lat and Long are in decimal degrees. 
	    //Written by Chuck Gantz- chuck.gantz@globalstar.com

	    double eccPrimeSquared;
	    double e1 = (1-Math.sqrt(1-eccSquared))/(1+Math.sqrt(1-eccSquared));
	    double N1, T1, C1, R1, D, M;
	    double lonOrigin;
	    double mu, /*phil,*/ phi1Rad;
	    double x, y;
	    int zoneNumber;
	    char zoneLetter;
	    //int NorthernHemisphere; //1 for northern hemispher, 0 for southern

	    x = utmEasting - 500000.0; //remove 500,000 meter offset for longitude
	    y = utmNorthing;

	    CharSequence utmZoneCopy = utmZone;
	    zoneLetter = utmZoneCopy.charAt(utmZoneCopy.length()-1);
	    CharSequence zoneNumberString = utmZoneCopy.subSequence(0, utmZoneCopy.length()-1);
	    zoneNumber = Integer.parseInt((String) zoneNumberString);
	    if((zoneLetter - 'N') >= 0){}
	      //NorthernHemisphere = 1;//point is in northern hemisphere
	    else
	    {
	      //NorthernHemisphere = 0;//point is in southern hemisphere
	      y -= 10000000.0;//remove 10,000,000 meter offset used for southern hemisphere
	    }

	    lonOrigin = (zoneNumber - 1)*6 - 180 + 3;  //+3 puts origin in middle of zone

	    eccPrimeSquared = (eccSquared)/(1-eccSquared);

	    M = y / k0;
	    mu = M/(a*(1-eccSquared/4-3*eccSquared*eccSquared/64-5*eccSquared*eccSquared*eccSquared/256));

	    phi1Rad = mu  + (3*e1/2-27*e1*e1*e1/32)*Math.sin(2*mu) 
	          + (21*e1*e1/16-55*e1*e1*e1*e1/32)*Math.sin(4*mu)
	          +(151*e1*e1*e1/96)*Math.sin(6*mu);
	    //phi1 = phi1Rad*rad2deg;

	    N1 = a/Math.sqrt(1-eccSquared*Math.sin(phi1Rad)*Math.sin(phi1Rad));
	    T1 = Math.tan(phi1Rad)*Math.tan(phi1Rad);
	    C1 = eccPrimeSquared*Math.cos(phi1Rad)*Math.cos(phi1Rad);
	    R1 = a*(1-eccSquared)/Math.pow(1-eccSquared*Math.sin(phi1Rad)*Math.sin(phi1Rad), 1.5);
	    D = x/(N1*k0);

	    double lat = phi1Rad - (N1*Math.tan(phi1Rad)/R1)*(D*D/2-(5+3*T1+10*C1-4*C1*C1-9*eccPrimeSquared)*D*D*D*D/24
	            +(61+90*T1+298*C1+45*T1*T1-252*eccPrimeSquared-3*C1*C1)*D*D*D*D*D*D/720);
	    lat = lat * rad2deg;

	    double lon = (D-(1+2*T1+C1)*D*D*D/6+(5-2*C1+28*T1-3*C1*C1+8*eccPrimeSquared+24*T1*T1)
	            *D*D*D*D*D/120)/Math.cos(phi1Rad);
	    lon = lonOrigin + lon * rad2deg;

	    return new GeoPoint((int)(lat*1E6), (int)(lon*1E6));
	  }
	}