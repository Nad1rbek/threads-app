package nad1r.techie

import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.stereotype.Component
import java.util.*


@Configuration
class EntityAuditingConfig {
    @Bean
    fun userIdAuditorAware() = AuditorAware {
        Optional.ofNullable(
            SecurityContextHolder.getContext().getUsername()
        )
    }
}

@EnableResourceServer
@Configuration
class ResourceServerConfig : ResourceServerConfigurerAdapter() {
    override fun configure(http: HttpSecurity?) {
        http
            ?.sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ?.and()
            ?.authorizeRequests()
            ?.antMatchers("/**")?.authenticated()
            ?.and()
            ?.csrf()?.disable()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()


    @Bean
    @LoadBalanced
    fun oauth2RestTemplate(userInfoRestTemplateFactory: UserInfoRestTemplateFactory): OAuth2RestTemplate {
        return userInfoRestTemplateFactory.userInfoRestTemplate
    }
}

class Auth2TokenConfiguration {
    @Bean
    fun feignInterceptor(context: OAuth2ClientContext, details: OAuth2ProtectedResourceDetails) =
        OAuth2FeignRequestInterceptor(context, details)
}