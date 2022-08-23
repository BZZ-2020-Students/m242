public class Robi {
    private final RobiAPI robi;

    public Robi(String hostname, int portNummer) {
        robi = new RobiAPI(hostname, portNummer);
    }

    // Used to start the Robi
    public static void main(String[] args) {
        Robi r = new Robi("localhost", 60958);   // Tragen Sie hier die Portnummer auf Ihrem Simulator ein.
        // hier Robi Methoden aufrufen
        r.planlosFahren(10);
    }

    /*
     *	Template-Methode für den Robi
     */
    public void templateMethod() {
        // Variabel deklarieren
        robi.connect();                  // ab hier ist der Robi verbunden

        // State-Machine
        state robiState = state.IDLE;

        boolean running = true;
        while (running) {
            robi.getDistSensorValues();

            int value = robi.readSensor(0);
        }

        robi.disconnect();              // hier wird der Robi wieder disconnected
    }

    public void planlosFahren(int maxWandberuehrungen) {

        System.out.println("PLANLOS FAHREN");

        // Intitialisieren
        int anzahlWandberuehrungen = 0;
        SandUhr uhr = new SandUhr();
        state zustand = state.FORWARD;

        robi.connect();
        robi.drive(5);
        System.out.println("Robi faehrt vorwaerts");

        while (anzahlWandberuehrungen < maxWandberuehrungen) {
            switch (zustand) {
                case FORWARD -> {
                    robi.getDistSensorValues();
                    if (anWand()) {
                        sensorWerteAusgeben();
                        robi.drive(-10);
                        uhr.starten(1000);
                        zustand = state.BACKWARD;
                        System.out.println("Robi faehrt rueckwaerts");
                    }
                }
                case BACKWARD -> {
                    if (uhr.abgelaufen()) {
                        robi.turn(20);
                        uhr.starten(2000);
                        zustand = state.TURN_LEFT;
                        System.out.println("Robi dreht ab");
                    }
                }
                case TURN_LEFT -> {
                    if (uhr.abgelaufen()) {
                        anzahlWandberuehrungen++;
                        robi.drive(5);
                        zustand = state.FORWARD;
                        System.out.println("Robi faehrt vorwaerts");
                    }
                }
            }
        }
        robi.stop();
        robi.disconnect();

        System.out.println("FERTIG");
        System.out.println();
    }

    private boolean anWand() {
        for (int i = 2; i < 16; i++) {
            if (robi.readSensor(i) > 100) {
                return true;
            }
        }
        return false;
    }

    private void sensorWerteAusgeben() {
        for (int i = 2; i < 16; i++) {
            System.out.print(robi.readSensor(i) + " ");
        }
        System.out.println();
    }

    enum state {
        IDLE,
        WAIT,
        FORWARD,
        BACKWARD,
        TURN_LEFT,
        TURN_RIGHT,
        STOP
    }
}
