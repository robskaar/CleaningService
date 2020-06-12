package Domain.Map;


import Domain.DeliveryPoint.DeliveryPoint;
import Services.Handlers.DeliveryPointHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;

/**
 * @Author Jacob Bonefeld
 * @Project CleaningService
 * @Date 01.06.2020
 **/

public class GoogleMap {

    private static GoogleMap googleMap = null;               // Singleton instance of the map
    private WebView mapView;                                 // The UI node which shows the map
    private WebEngine engine;                                // Controls the webview
    private final String MAP_HTML = "/UI/Driver/Map.html";   // Path to html document
    private boolean isMarkersSet = false;                    // Boolean to ensure markers only get set once

    private GoogleMap() {
        mapView = new WebView();
        engine = mapView.getEngine();
        engine.load(getClass().getResource(MAP_HTML).toString());
    }

    /**
     * @return returns a singleton instance of GoogleMap
     */
    public static GoogleMap getMap() {
        if (googleMap == null) {
            googleMap = new GoogleMap();
        }
        return googleMap;
    }

    public WebView getMapView() {
        return mapView;
    }

    /**
     * Sets markers on the map for all delivery points on the route
     * @param routeID The ID of the route.
     */
    public void setMarkers(int routeID) {

        if(!isMarkersSet){

            ArrayList<DeliveryPoint> deliveryPoints = DeliveryPointHandler.getRouteDeliveryPoints(routeID);
            String address;
            String storeName;

            for (DeliveryPoint dp : deliveryPoints) {
                address = dp.getAddress() + " " + dp.getZipCode();
                storeName = dp.getName();
                engine.executeScript("setMarker('" + address + "','" + storeName + "')");
            }

            isMarkersSet = true;
        }
    }

}
