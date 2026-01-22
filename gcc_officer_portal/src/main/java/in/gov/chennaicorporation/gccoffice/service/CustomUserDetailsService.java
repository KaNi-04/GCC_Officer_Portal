package in.gov.chennaicorporation.gccoffice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import in.gov.chennaicorporation.gccoffice.entity.AppUserEntity;
import in.gov.chennaicorporation.gccoffice.repository.AppUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final UserActivityService userActivityService;

    @Autowired
    public CustomUserDetailsService(AppUserRepository appUserRepository,UserActivityService userActivityService ) {
        this.appUserRepository = appUserRepository;
        this.userActivityService = userActivityService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserEntity appUserEntity = appUserRepository.findByUsername(username);
     
        userActivityService.logUserActivity("0", "Trying to Login with Username: " + username);
        //System.out.println(username);
        if (appUserEntity == null) {
        	//System.out.println("User not found");
        	userActivityService.logUserActivity("0","User (" + username +") not found");
            throw new UsernameNotFoundException("User not found");
        }
        
        /*
        return org.springframework.security.core.userdetails.User.builder()
                .username(appUserEntity.getUsername())
                .password(appUserEntity.getPassword())
                .roles(appUserEntity.getRoles())
                .additionalAttributes(Map.of("userid", appUserEntity.getUserid()))
                .build();
        */
        
        String userRole = appUserEntity.getRoles();
        // Ensure that the role starts with "ROLE_"
        if (!userRole.startsWith("ROLE_")) {
            userRole = "ROLE_" + userRole;
            //System.out.println("User Role: "+userRole);
        }
        
        // Create a SimpleGrantedAuthority for the user role
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole);
     
        // Create a list to hold the authority/role
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(authority);
        
        // Construct CustomUserDetails instance with custom attributes
        CustomUserDetails userDetails = new CustomUserDetails(
        		appUserEntity.getUsername(), 
        		appUserEntity.getPassword(), 
        		authorityList, 
        		true, 
        		true, 
        		true, 
        		true, 
        		Map.of("userid",appUserEntity.getUserid().getUserid(),
        				"usergroupid",appUserEntity.getUsergroup_id().getId(),
        				"userrole",appUserEntity.getRoles().toString()
        				)
        		);
        
        userActivityService.logUserActivity(appUserEntity.getUserid().getUserid().toString(), "Login Success: <br>Username: "+appUserEntity.getUsername()
        +"<br>Role: "+appUserEntity.getRoles().toString()
        +"<br>Access Group: "+appUserEntity.getUsergroup_id().getName()
        +"<br>Access Group ID: "+appUserEntity.getUsergroup_id().getId());
        
        return userDetails;
    }
}
