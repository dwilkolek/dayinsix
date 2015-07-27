package eu.wilkolek.diary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                // .anyRequest().permitAll();
                .antMatchers("/users/**")
                .hasAuthority("USER")
                .antMatchers("/**")
                .permitAll()
                .anyRequest().fullyAuthenticated().and().formLogin()
                .loginPage("/login").failureUrl("/loginError").defaultSuccessUrl("/loginSuccess").successHandler(successHandler).usernameParameter("email")
                .permitAll().and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout/**")).deleteCookies("remember-me").logoutSuccessHandler(logoutSuccessHandler).permitAll()
                
                .and().rememberMe();

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

}