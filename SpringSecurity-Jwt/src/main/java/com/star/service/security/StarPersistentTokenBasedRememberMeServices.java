package com.star.service.security;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-15 15:15
 */
public class StarPersistentTokenBasedRememberMeServices extends PersistentTokenBasedRememberMeServices {

    private boolean alwaysRemember;

    public StarPersistentTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        if (this.alwaysRemember) {
            return true;
        }
        String paramValue = request.getAttribute(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY).toString();
        if (paramValue != null) {
            if (paramValue.equalsIgnoreCase("true") || paramValue.equalsIgnoreCase("on")
                    || paramValue.equalsIgnoreCase("yes") || paramValue.equals("1")) {
                return true;
            }
        }
        this.logger.debug(
                LogMessage.format("Did not send remember-me cookie (principal did not set parameter '%s')", parameter));
        return false;
    }

    @Override
    public void setAlwaysRemember(boolean alwaysRemember) {
        this.alwaysRemember = alwaysRemember;
    }
}
