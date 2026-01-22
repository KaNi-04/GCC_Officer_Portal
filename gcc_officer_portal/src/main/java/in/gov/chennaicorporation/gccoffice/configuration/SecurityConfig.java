package in.gov.chennaicorporation.gccoffice.configuration;

import javax.persistence.Basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import in.gov.chennaicorporation.gccoffice.service.CustomUserDetailsService;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CustomUserDetailsService customUserDetailsService;
	

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }
    
	@Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/static/**", "/assets/**", "/api/**","/images/**","/gcc/static/**", "/gcc/assets/**", "/gcc/api/**","/gcc/images/**","/gcc/files/**","/gcc/dumpregistration/api/**");
        	//.antMatchers("/gcc/static/**", "/gcc/assets/**", "/gcc/api/**","/gcc/images/**");
            //.antMatchers("/assets/**", "/css/**", "/js/**", "/images/**", "/fonts/**"); // Add the paths to your static resources here
    }
	
	 @Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	    }
   
    // Configure authorization (URL-based access control)
	 @Override
	 protected void configure(HttpSecurity http) throws Exception {
		 
	     http
	     	 .cors().and() // Enable CORS
	     	 .csrf().disable() // Disable CSRF protection for simplicity (Enable if needed)
	         .authorizeRequests()
	         	 .antMatchers("/gcc/error").permitAll()
	             .antMatchers("/gcc/login").permitAll() // Add the login URL
	             .antMatchers("/gcc/admin/**").hasRole("ADMIN") // Add the admin URLs
	             .antMatchers("/gcc/**").hasAnyRole("ADMIN","USER") // Add other URLs
	             //.anyRequest().authenticated()
	             .and()
	         .formLogin()
	             .loginPage("/gcc/login") // Customize the login page URL
	             .defaultSuccessUrl("/gcc") // Include the success URL
	             .permitAll()
	             .and()
	         .logout()
	             .logoutUrl("/gcc/logout") // Include the logout URL
	             .logoutSuccessUrl("/gcc/login") // Include the success logout URL
	             .permitAll()
	             .and()
	         .sessionManagement()
	     	 	.sessionFixation()
	     	 	.migrateSession(); // or .none(), .newSession()
	     
	     /*
	     // Print role values
	     http.authorizeRequests().anyRequest().authenticated().and().formLogin().successHandler((request, response, authentication) -> {
	         System.out.println("User Roles: ");
	         authentication.getAuthorities().forEach(authority -> System.out.println(authority.getAuthority()));
	         System.out.println();
	         response.sendRedirect("/gcc");
	     });
	     */
	 }

    
    @Basic
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}