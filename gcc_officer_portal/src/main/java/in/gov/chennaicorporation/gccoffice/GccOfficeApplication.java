package in.gov.chennaicorporation.gccoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EntityScan(basePackages = {
        "in.gov.chennaicorporation.gccoffice",
        "in.gov.chennaicorporation.gccoffice.taxcollection.entity"
})
@ComponentScan(basePackages = {
        "in.gov.chennaicorporation.gccoffice",
        "in.gov.chennaicorporation.gccoffice.taxcollection",
        "in.gov.chennaicorporation.gccoffice.garbagecollection"
})
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@EnableDiscoveryClient
public class GccOfficeApplication {
    public static void main(String[] args) {
    	//System.setProperty("spring.config.name", "gcc-portal");
        SpringApplication.run(GccOfficeApplication.class, args);
    }
}