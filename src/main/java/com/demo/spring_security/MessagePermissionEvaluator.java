package com.demo.spring_security;

import com.demo.spring_security.domain.Message;
import com.demo.spring_security.domain.User;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author Parisana
 */
@Component
public class MessagePermissionEvaluator implements PermissionEvaluator{
        /* (non-Javadoc)
         * @see org.springframework.security.access.PermissionEvaluator#hasPermission(org.springframework.security.core.Authentication, java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean hasPermission(Authentication authentication,
                                     Object targetDomainObject, Object permission) {
            if(authentication == null) {
                return false;
            }
            if (targetDomainObject instanceof Optional)
                targetDomainObject= ((Optional) targetDomainObject).get();
            Message message = (Message) targetDomainObject;
            if(message == null) {
                return true;
            }
            User currentUser = (User) authentication.getPrincipal();
            return currentUser.getId().equals(message.getTo().getId());
        }

        /* (non-Javadoc)
         * @see org.springframework.security.access.PermissionEvaluator#hasPermission(org.springframework.security.core.Authentication, java.io.Serializable, java.lang.String, java.lang.Object)
         */
        @Override
        public boolean hasPermission(Authentication authentication,
                                     Serializable targetId, String targetType, Object permission) {
            throw new UnsupportedOperationException();
        }

}
