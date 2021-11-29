package com.main.Billing;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.service.ServiceRegistry;

import com.main.Billing.controller.MainWindowController;

public class HibernateUtil {

	private static Connection connection = null;
	
	public static Connection getConnection() {
		if(connection == null) {
			Session session = getSessionFactory().openSession();
			connection = session.doReturningWork(new ReturningWork<Connection>() {
	            @Override
	            public Connection execute(Connection conn) throws SQLException {
	                return conn;
	            }
	        });
		}
		return connection;
	}
	
    public static SessionFactory getSessionFactory() {
        Configuration configuration = createConfig();

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
            .applySettings(configuration.getProperties()).build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static Configuration createConfig() {
        Configuration configuration = new Configuration();
        
        InputStream input = MainWindowController.class.getClassLoader().getResourceAsStream("application.properties");
		Properties appProp = new Properties();
		try {
			appProp.load(input);
		} catch (IOException e) {
		}
        
        Properties settings = new Properties();
        settings.put(Environment.DRIVER, appProp.get("db.driver"));
        settings.put(Environment.URL, appProp.get("db.url"));
        settings.put(Environment.USER, appProp.get("db.username"));
        settings.put(Environment.PASS, appProp.get("db.password"));
        settings.put(Environment.DIALECT, appProp.get("hibernate.dialect"));
        settings.put(Environment.SHOW_SQL, appProp.get("hibernate.show_sql"));
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        
        configuration.setProperties(settings);
        configuration.addPackage((String) appProp.get("entitymanager.packagesToScan"));

        return configuration;
    }
}
