package ACO;


import org.example.DataUtils;

public class Application {
    public static void main(String... args) {

        Configuration.INSTANCE.logEngine.write("--- starting");
        Configuration.INSTANCE.data = new DataUtils();

        AntColony antColony = new AntColony();
        antColony.solve();
        Configuration.INSTANCE.logEngine.write(antColony.toString());

        Configuration.INSTANCE.logEngine.close();
    }
}
