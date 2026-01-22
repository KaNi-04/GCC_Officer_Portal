package in.gov.chennaicorporation.gccoffice.configuration;

//import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import in.gov.chennaicorporation.gccoffice.interceptor.AppInterceptor;
import in.gov.chennaicorporation.gccoffice.repository.AppModuleAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.AppModuleRepository;
import in.gov.chennaicorporation.gccoffice.repository.MenuItemAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.MenuItemRepository;
import in.gov.chennaicorporation.gccoffice.repository.SubMenuItemAccessRepository;
import in.gov.chennaicorporation.gccoffice.repository.SubMenuItemRepository;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	private final String uploadDirectory;
	private AppModuleAccessRepository appModuleAccessRepository;
	private MenuItemAccessRepository menuItemAccessRepository;
	private SubMenuItemAccessRepository subMenuItemAccessRepository;
	
	private AppModuleRepository appModuleRepository;
	private MenuItemRepository menuItemRepository;
	private SubMenuItemRepository subMenuItemRepository;
	
	private final Environment environment;
	
	@Autowired
	public WebMvcConfig(
            @Value("${file.upload.directory}") String uploadDirectory,
            AppModuleAccessRepository appModuleAccessRepository,
            MenuItemAccessRepository menuItemAccessRepository,
            SubMenuItemAccessRepository subMenuItemAccessRepository,
            AppModuleRepository appModuleRepository,
    		MenuItemRepository menuItemRepository,
    		SubMenuItemRepository subMenuItemRepository,
    		Environment environment) {
        this.uploadDirectory = uploadDirectory;
        this.appModuleAccessRepository = appModuleAccessRepository;
        this.menuItemAccessRepository = menuItemAccessRepository;
        this.subMenuItemAccessRepository = subMenuItemAccessRepository;
        this.appModuleRepository = appModuleRepository;
        this.menuItemRepository = menuItemRepository;
        this.subMenuItemRepository = subMenuItemRepository;
        this.environment = environment;
    }
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	
        registry.addInterceptor(new AppInterceptor(
        		appModuleAccessRepository, 
        		menuItemAccessRepository, 
        		subMenuItemAccessRepository,
        		appModuleRepository,
        		menuItemRepository,
        		subMenuItemRepository,
        		environment));
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + uploadDirectory +"/");
    }
}

