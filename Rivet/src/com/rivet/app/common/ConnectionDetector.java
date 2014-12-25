package com.rivet.app.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class ConnectionDetector {

	private Context _context;

	public ConnectionDetector(Context context) {
		this._context = context;
	}

	/**
	 * Checking for all possible internet providers
	 * **/
	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	/**
	 * Get the network info
	 * 
	 * @param context
	 * @return
	 */
	public static NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/**
	 * Check if there is any connectivity
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		NetworkInfo info = ConnectionDetector.getNetworkInfo(context);
		return (info != null && info.isConnected());
	}

	/**
	 * Check if there is any connectivity to a Wifi network
	 * 
	 * @param context
	 * @param type
	 * @return
	 */
	public static boolean isConnectedWifi(Context context) {
		NetworkInfo info = ConnectionDetector.getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * Check if there is any connectivity to a mobile network
	 * 
	 * @param context
	 * @param type
	 * @return
	 */
	public static boolean isConnectedMobile(Context context) {
		NetworkInfo info = ConnectionDetector.getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * Check if there is fast connectivity
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnectedFast(Context context) {
		NetworkInfo info = ConnectionDetector.getNetworkInfo(context);
		return (info != null && info.isConnected() && ConnectionDetector
				.isConnectionFast(info.getType(), info.getSubtype()));
	}

	/**
	 * Check if the connection is fast
	 * 
	 * @param type
	 * @param subType
	 * @return
	 */
	public static boolean isConnectionFast(int type, int subType) {
		
		boolean isFast = false;
		
		if (type == ConnectivityManager.TYPE_WIFI) {
			isFast = true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {

			if (type == ConnectivityManager.TYPE_WIFI) {
				isFast = true;
				return isFast;
			} else if (subType == TelephonyManager.NETWORK_TYPE_1xRTT) {

				isFast = false; // ~ 50-100 kbps

			} else if (subType == TelephonyManager.NETWORK_TYPE_CDMA) {

				isFast = false; // ~ 14-64 kbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_EDGE) {

				isFast = false; // ~ 50-100 kbps

			} else if (subType == TelephonyManager.NETWORK_TYPE_EVDO_0) {

				isFast = true; // ~ 400-1000 kbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_EVDO_A) {

				isFast = true; // ~ 600-1400 kbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_GPRS) {

				isFast = false; // ~ 100 kbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_HSDPA) {

				isFast = true; // ~ 2-14 Mbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_HSPA) {

				isFast = true; // ~ 700-1700 kbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_HSUPA) {

				isFast = true; // ~ 1-23 Mbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS) {

				isFast = true; // ~ 400-7000 kbps

			} else if (subType == TelephonyManager.NETWORK_TYPE_EHRPD) {

				isFast = true; // ~ 1-2 Mbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {

				isFast = true; // ~ 5 Mbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_HSPAP) {

				isFast = true; // ~ 10-20 Mbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_IDEN) {

				isFast = false; // ~25 kbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {

				isFast = true; // ~ 10+ Mbps
			} else if (subType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {

				isFast = false;
			} else {
				isFast = false;
			}
		}
		
		return isFast;

	}
}
