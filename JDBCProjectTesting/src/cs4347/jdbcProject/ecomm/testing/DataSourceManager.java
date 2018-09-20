package cs4347.jdbcProject.ecomm.testing;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Application use the static method getDataSource() to obtain the singleton
 * DataSource. Data sources are used to open connections to the MySQL server.
 * The DBMS connection parameters (url, id, and password) is maintained in a
 * property file 'dbconfig.properties'. The property file must be located on the
 * application's CLASSPATH. See the configuration property file is loaded by the
 * method getPropertiesFromClasspath().
 */
public class DataSourceManager {

	private static MysqlDataSource singletonDS = null;

	public synchronized static DataSource getDataSource() throws IOException {
		if (singletonDS == null) {
			Properties props = getPropertiesFromClasspath();

			String url = props.getProperty("url");
			if (url == null || url.isEmpty()) {
				throw new RuntimeException("property 'url' not found in configuration file");
			}

			String id = props.getProperty("id");
			if (id == null || id.isEmpty()) {
				throw new RuntimeException("property 'id' not found in configuration file");
			}

			String passwd = props.getProperty("passwd");
			if (passwd == null || passwd.isEmpty()) {
				throw new RuntimeException("property 'passwd' not found in configuration file");
			}

			singletonDS = new MysqlDataSource();
			singletonDS.setURL(url);
			singletonDS.setUser(id);
			singletonDS.setPassword(passwd);
		}
		return singletonDS;
	}

	private static final String propFileName = "dbconfig.properties";

	public static Properties getPropertiesFromClasspath() throws IOException {
		// Load dbconfig.properties from the classpath
		Properties props = new Properties();
		InputStream inputStream = DataSourceManager.class.getClassLoader().getResourceAsStream(propFileName);

		if (inputStream == null) {
			throw new RuntimeException("property file '" + propFileName + "' not found in the classpath");
		}

		props.load(inputStream);

		return props;
	}

	public static void main(String args[]) {
		try {
			DataSource ds = DataSourceManager.getDataSource();
			Connection con = ds.getConnection();
			Statement stat = con.createStatement();
			ResultSet rs = stat.executeQuery("select count(*) from simple_company.customer");
			if (rs.next()) {
				System.out.println("Count: " + rs.getInt(1));
			}
			System.out.println("Finished");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}