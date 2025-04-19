package GA;
import java.util.List;

public record City(List<Double> coordinates, int demand, String name, int ready_time) {
    public String getName() {
        return name;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public int getDemand() {
        return demand;
    }

    public int getReady_time(){ return ready_time; }
    public double distanceFrom(City city) {
        //double deltaXSq = Math.pow((city.getX() - this.getX()), 2);
        double deltaXSq = Math.pow(city.getCoordinates().get(0) - this.getCoordinates().get(0), 2);

        //double deltaYSq = Math.pow((city.getY() - this.getY()), 2);
        double deltaYSq = Math.pow(city.getCoordinates().get(1) - this.getCoordinates().get(1), 2);

        return Math.sqrt(Math.abs(deltaXSq + deltaYSq));
    }
}