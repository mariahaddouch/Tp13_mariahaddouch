package ma.rest.jersey.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaManager {

    private static final String PERSISTENCE_UNIT_NAME = "benchmark-pu";
    private static EntityManagerFactory factory;

    public static synchronized EntityManagerFactory getFactory() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return factory;
    }

    public static void shutdown() {
        if (factory != null) {
            factory.close();
            factory = null;
        }
    }
}