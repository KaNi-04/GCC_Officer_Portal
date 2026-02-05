package in.gov.chennaicorporation.gccoffice.configuration;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import in.gov.chennaicorporation.gccoffice.repository.AppUserRepository;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

	private static String host = "localhost:3306";
	private static String dbpassword = "root";

	// private static String dbpassword = "gccroot";

	// AWS
	// private static String host =
	// "gcc-facial-db-instance-1.cf48eqcciziq.ap-south-1.rds.amazonaws.com:3306";
	// private static String dbpassword = "gcc-facial-password";

	////////////////////////////// (For GCC APP) ////////////////////////
	@Configuration
	@EnableTransactionManagement
	@EnableJpaRepositories(entityManagerFactoryRef = "appEntityManagerFactory", transactionManagerRef = "appTransactionManager", basePackages = {
			"in.gov.chennaicorporation.gccoffice.repository" })

	public static class MysqlAppDataSourceConfig {
		@Primary
		@Bean(name = "mysqlAppDataSource")
		public DataSource mysqlAppDataSource() {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
			dataSource.setUrl("jdbc:mysql://" + host + "/gcc_apps");
			dataSource.setUsername("root");
			dataSource.setPassword(dbpassword);
			return dataSource;
		}

		/*
		 * @Bean(name = "mysqlAppJdbcTemplate")
		 * public JdbcTemplate mysqlAppJdbcTemplate(@Qualifier("mysqlAppDataSource")
		 * DataSource dataSource) {
		 * return new JdbcTemplate(dataSource);
		 * }
		 */
		@Primary
		@Bean(name = "appEntityManagerFactory")
		public LocalContainerEntityManagerFactoryBean entityManagerFactory(
				@Qualifier("mysqlAppDataSource") DataSource dataSource) {
			LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
			entityManagerFactoryBean.setDataSource(dataSource);
			entityManagerFactoryBean.setPackagesToScan("in.gov.chennaicorporation.gccoffice.entity");
			entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
			Properties properties = new Properties();
			properties.setProperty("hibernate.hbm2ddl.auto", "update");
			entityManagerFactoryBean.setJpaProperties(properties);

			return entityManagerFactoryBean;
		}

		@Primary
		@Bean(name = "appTransactionManager")
		public PlatformTransactionManager transactionManager(
				@Qualifier("appEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
			return new JpaTransactionManager(entityManagerFactory);
		}
	}

	////////////////////////////// (For Tax Collection 1913)
	////////////////////////////// ////////////////////////
	@Configuration
	@EnableTransactionManagement
	@EnableJpaRepositories(entityManagerFactoryRef = "taxEntityManagerFactory", transactionManagerRef = "taxTransactionManager", basePackages = {
			"in.gov.chennaicorporation.gccoffice.taxcollection.repository" })
	public static class MysqlTaxCollectionDataSourceConfig {

		@Bean(name = "mysqlTaxCollectionDataSource")
		public DataSource mysqlTaxCollectionDataSource() {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
			dataSource.setUrl("jdbc:mysql://" + host + "/gcc_tax_collection");
			dataSource.setUsername("root");
			dataSource.setPassword(dbpassword);
			return dataSource;
		}

		@Bean(name = "taxEntityManagerFactory")
		public LocalContainerEntityManagerFactoryBean taxEntityManagerFactory(
				@Qualifier("mysqlTaxCollectionDataSource") DataSource dataSource) {
			LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
			entityManagerFactoryBean.setDataSource(dataSource);
			entityManagerFactoryBean.setPackagesToScan("in.gov.chennaicorporation.gccoffice.taxcollection.entity");
			entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
			Properties properties = new Properties();
			properties.setProperty("hibernate.hbm2ddl.auto", "update");
			entityManagerFactoryBean.setJpaProperties(properties);

			return entityManagerFactoryBean;
		}

		@Bean(name = "taxTransactionManager")
		public PlatformTransactionManager taxTransactionManager(
				@Qualifier("taxEntityManagerFactory") EntityManagerFactory taxEntityManagerFactory) {
			return new JpaTransactionManager(taxEntityManagerFactory);
		}
	}

	////////////////////////////// (For Mayor Petition) ////////////////////////
	@Bean(name = "mysqlMayorPetitionDataSource")
	public DataSource mysqlMayorPetitionDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/mayor_petition");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For Commissioner Petition)
	////////////////////////////// ////////////////////////
	@Bean(name = "mysqlCommissionerPetitionDataSource")
	public DataSource mysqlCommissionerPetitionDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/commissioner_petition");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For Petition Master) ////////////////////////
	@Bean(name = "mysqlPetitionMasterDataSource")
	public DataSource mysqlPetitionMasterDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/petition_master");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For Councillor Petition)
	////////////////////////////// ////////////////////////
	@Bean(name = "mysqlCouncillorPetitionDataSource")
	public DataSource mysqlCouncillorPetitionDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/councillor_petition");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For PGR Master) ////////////////////////
	@Bean(name = "mysqlPGRMasterDataSource")
	public DataSource mysqlPGRMasterDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/erp_pgr");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For Circular Petition) ////////////////////////
	@Bean(name = "mysqlCircularDataSource")
	public DataSource mysqlCircularDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/circular");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For NULM Staff) ////////////////////////
	@Bean(name = "mysqlNulmDataSource")
	public DataSource mysqlNulmDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_nulm");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For Zero Hours Petition)
	////////////////////////////// ////////////////////////
	@Bean(name = "mysqlZeroHoursDataSource")
	public DataSource mysqlZeroHoursDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/zerohours_petition");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For Ward Sabha) ////////////////////////
	@Bean(name = "mysqlWardSabhaDataSource")
	public DataSource mysqlWardSabhaDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/ward_sabha");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For 1913 Campaign) ////////////////////////
	@Bean(name = "mysql1913CampaignDataSource")
	public DataSource mysql1913CampaignDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_1913_campaign");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For 1913 QAQC) ////////////////////////
	@Bean(name = "mysql1913QAQCDataSource")
	public DataSource mysql1913QAQCDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_1913_qaqc");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For SOS) ////////////////////////
	@Bean(name = "mysqlGccSOSDataSource")
	public DataSource mysqlGccSOSDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_sos");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For Kural) ////////////////////////
	@Bean(name = "mysqlKuralDataSource")
	public DataSource mysqlKuralDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/Kural");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For WORKS) ////////////////////////
	@Bean(name = "mysqlWorksDataSource")
	public DataSource mysqlWorksDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_works_status");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For Ward Sabha) ////////////////////////
	@Bean(name = "mysqlWardShabaDataSource")
	public DataSource mysqlWardShabaDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/ward_sabha");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For V-Track (jTrack)) ////////////////////////
	@Bean(name = "mysqlVehiclTrackingDataSource")
	public DataSource mysqlVehiclTrackingDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_vtrack");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For school) ////////////////////////
	@Bean(name = "mysqlSchoolDataSource")
	public DataSource mysqlSchoolDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_schools_web");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For dump registration) ////////////////////////
	@Bean(name = "mysqlFlagPoleManagerSystemDataSource")
	public DataSource mysqlFlagPoleManagerSystemDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_flag_pole");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For gcc_c_d_waste_web) ////////////////////////
	@Bean(name = "mysqlCDManagerSystemDataSource")
	public DataSource mysqlCDManagerSystemDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_c_d_waste_web");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For gcc_c_d_waste_web) ////////////////////////
	@Bean(name = "mysqlPensionerDataSource")
	public DataSource mysqlPensionerDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_pensioner");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For gcc_c_d_waste_web) ////////////////////////
	@Bean(name = "mysqlGccStreetVendorDataSource")
	public DataSource mysqlGccStreetVendorDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_street_vendor");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// (For vending committe) ////////////////////////
	@Bean(name = "mysqlVendingCommitteDataSource")
	public DataSource mysqlVendingCommitteDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/vendingcommitte");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	/////////////// Domestic Waste /////////////////////
	@Bean(name = "mysqlDomesticWasteDataSource")
	public DataSource mysqlDomesticWasteDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/domestic_waste_management");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	/////////////// Green committee /////////////////////
	@Bean(name = "mysqlGreenCommitteeDataSource")
	public DataSource mysqlGreenCommitteeDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/green_committee");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	/////////////// SCP /////////////////////
	@Bean(name = "mysqlScpDataSource")
	public DataSource mysqlScpDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://" + host + "/gcc_1913_siltcatchpit");
		dataSource.setUsername("root");
		dataSource.setPassword(dbpassword);
		return dataSource;
	}

	////////////////////////////// ORACLE ////////////////////////
	@Bean(name = "oracleDataSource")
	public DataSource oracleDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@10.1.0.14:1521:chncorp");
		dataSource.setUsername("FAS");
		dataSource.setPassword("FAS");
		return dataSource;
	}
}