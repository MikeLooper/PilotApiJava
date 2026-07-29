package com.pilotapi.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.core.env.Environment;

public class PropertyResolvingNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private final Environment environment;

    public PropertyResolvingNamingStrategy(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        Identifier identifier = super.toPhysicalTableName(logicalName, jdbcEnvironment);
        String text = identifier.getText();
        if (text.startsWith("${") && text.endsWith("}")) {
            String propertyKey = text.substring(2, text.length() - 1);
            String resolved = environment.getProperty(propertyKey, text);
            return Identifier.toIdentifier(resolved);
        }
        return identifier;
    }
}
