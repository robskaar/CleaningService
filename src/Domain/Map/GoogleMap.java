package Domain.Map;


import Domain.DeliveryPoint.DeliveryPoint;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;


public class GoogleMap {

    private static GoogleMap googleMap = null;
    private WebView mapView;
    private WebEngine engine;
    private final String MAP_HTML = "/UI/Driver/Map2.html";

    private GoogleMap() {
        mapView = new WebView();
        engine = mapView.getEngine();
        engine.load(getClass().getResource(MAP_HTML).toString());
    }

    public static GoogleMap getMap() {
        if (googleMap == null) {
            googleMap = new GoogleMap();
        }
        return googleMap;
    }

    public WebView getMapView() {
        return mapView;
    }

    public void setMarkers(ArrayList<DeliveryPoint> deliveryPoints) {
        String address;

        for (DeliveryPoint dp : deliveryPoints) {
            address = dp.getAddress() + " " + dp.getZipCode();
            engine.executeScript("setMarker('" + address + "')");
        }
    }
}
