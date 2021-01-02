package sst.inverted.properties;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class InvertedProperties {
    public static final String DELIMITER = ",";
    private final String filename;
    private final Map<String, String> mapping = new HashMap<>();
    private final FileBasedConfigurationBuilder<PropertiesConfiguration> builder;
    private final PropertiesConfiguration config;

    public InvertedProperties(String filename) throws ConfigurationException {
        this.filename = filename;
        Configurations configs = new Configurations();
        builder = configs.propertiesBuilder(filename);
        config = builder.getConfiguration();
        init();
    }

    private void init() {
        for (Iterator<String> it = config.getKeys(); it.hasNext(); ) {
            String key = it.next();
            final List<String> values = getValues(key);
            values.forEach(c -> mapping.put(c, key));
        }
    }

    public void save() throws ConfigurationException {
        builder.save();
    }

    private List<String> getValues(String key) {
        String valuesString = config.getString(key);
        return (valuesString != null) ? Arrays.asList(valuesString.split(DELIMITER)) : new ArrayList<>();
    }

    public String map(String counterparty) {
        return mapping.get(counterparty);
    }

    public void putMapping(String key, String value) throws ConfigurationException {
        mapping.put(key, value);
        final ArrayList<String> stringList = new ArrayList<>(getValues(value));
        if (!stringList.contains(key)) {
            stringList.add(key);
        }
        config.setProperty(value, String.join(",", stringList));
        try {
            backup();
        } catch (IOException e) {
            throw new ConfigurationException(String.format("Cannot backup <%s>", filename), e);
        }
        save();
    }

    private void backup() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        final Path original = Paths.get(filename);
        final Path copied = Paths.get(filename + "." + sdf.format(new Date()));
        Files.copy(original, copied, StandardCopyOption.REPLACE_EXISTING);
    }
}
