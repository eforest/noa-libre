package ag.ion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestProperties {

    private final static String LIBREOFFICE_DEFAULT_PATH = "/usr/lib64/libreoffice";

    private static Properties props = new Properties();
    static {
        try (InputStream in = TestProperties.class.getClassLoader().getResourceAsStream( "test.properties" )) {
            props.load( in );
        }
        catch ( IOException e ) {
            throw new RuntimeException( "Cannot load test.properties" );
        }
    }

    public static String getLibreOfficePath() {
        return props.getProperty( "LO_HOME", LIBREOFFICE_DEFAULT_PATH );
    }

}
