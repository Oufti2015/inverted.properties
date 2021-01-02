package sst.inverted.properties;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * InvertedProperties Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>janv. 1, 2021</pre>
 */
public class InvertedPropertiesTest {

    public static final String INVERTED_PROPERTIES_TEST_FILENAME = "InvertedPropertiesTest.properties";

    @Before
    public void before() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("UN", "ONE,TWO,THREE,FOUR");
        properties.setProperty("DEUX", "FIVE,SIX,SEVEN,EIGHT");
        try (FileOutputStream outputStream = new FileOutputStream(INVERTED_PROPERTIES_TEST_FILENAME)) {
            properties.store(outputStream, "InvertedPropertiesTest JUnit test");
        }
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: save()
     */
    @Test
    public void testSave() throws Exception {
        try {
            Assert.assertTrue(new File(INVERTED_PROPERTIES_TEST_FILENAME).exists());
            InvertedProperties invertedProperties = new InvertedProperties(INVERTED_PROPERTIES_TEST_FILENAME);
            Assert.assertEquals("UN", invertedProperties.map("TWO"));
            Assert.assertEquals("DEUX", invertedProperties.map("SEVEN"));

            invertedProperties.putMapping("NINE", "TROIS");
            Assert.assertEquals("TROIS", invertedProperties.map("NINE"));

            invertedProperties.putMapping("TEN", "TROIS");
            Assert.assertEquals("TROIS", invertedProperties.map("TEN"));

            invertedProperties.putMapping("ELEVEN", "DEUX");
            Assert.assertEquals("DEUX", invertedProperties.map("ELEVEN"));

            invertedProperties.save();

            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(INVERTED_PROPERTIES_TEST_FILENAME)) {
                properties.load(fis);
            }

            Assert.assertEquals("ONE,TWO,THREE,FOUR", properties.getProperty("UN"));
            Assert.assertEquals("FIVE,SIX,SEVEN,EIGHT,ELEVEN", properties.getProperty("DEUX"));
            Assert.assertEquals("NINE,TEN", properties.getProperty("TROIS"));
        } catch (ConfigurationException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
