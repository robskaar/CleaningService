package Domain.Route;

/**
 * @Author Robert Skaar
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 08-06-2020
 **/

public class Route {
    int routeID;
    int isAssigned; // 0 for false, 1 for true

    public Route(int routeID, int isAssigned){
        this.routeID = routeID;
        this.isAssigned = isAssigned;
    }

    public int getRouteID( ) {
        return routeID;
    }

    public int getIsAssigned( ) {
        return isAssigned;
    }

    public void setIsAssigned(int isAssigned) {
        this.isAssigned = isAssigned;
    }
}
