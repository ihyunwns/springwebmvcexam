package com.hyunwns.demoweb.config;

import com.hyunwns.demoweb.repository.MemberRepository;
import com.hyunwns.demoweb.repository.MemoryMemberRepository;
import com.hyunwns.demoweb.security.CustomAuthenticationFailureHandler;
import com.hyunwns.demoweb.security.CustomSessionExpiredStrategy;
import com.hyunwns.demoweb.service.CustomUserDetailsService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;


@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig implements ApplicationContextAware {

//    @Bean @Primary
//    public MemberRepository memberRepository() {
//        return new MemoryMemberRepository();
//    }
//
//    @Bean @Primary
//    UserDetailsService userDetailsService() {
//        return new CustomUserDetailsService(memberRepository());
//    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        UserDetailsService userDetailsService = applicationContext.getBean(CustomUserDetailsService.class);

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    // 특정 요청은 필터를 거치지 않게 하는 방법, 필터를 거치게 하는 것보다 자원을 덜 잡아먹기 때문에 정적파일에 대해서 이렇게 설정해주면 효율적이다.
    // 내부에 필터가 한개도 없는 필터체인을 거치게 됨
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/static/**");
    }

    @Bean SessionInformationExpiredStrategy customSessionExpiredStrategy() {
        return new CustomSessionExpiredStrategy();
    }

    @Bean
    AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }


    // 필터 체인 등록, FilterChainProxy 필터 목록에 등록이 됨, 두개의 필터를 등록할 수도 있음
    // 두개를 등록하게 되면 FilterChainProxy는 어떤 필터체인을 선택해야 할지 모름
    // 이떄 선택하는 방법은 1. 등록 인덱스 순 2. 필터 체인에 대한 RequestMatcher 값이 일치하는 지 확인 (** 인가 설정이 아닌 필터에 대한 RequestMatcher)
    // 매핑 방법: securityMatchers
    @Bean
//    @Order(1) FilterChainProxy에 등록될 때 등록 순서를 정할 수 있는 어노테이션, 숫자가 낮을수록 먼저 등록이 된다.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        http.securityMatchers( (auth) -> auth.requestMatchers("/test"));
//        이런 식으로 멀티 SecurityFilterChain이 등록된 경우에는 매핑을 해줘야 한다.
//        /test 경로로 오는 요청은 해당 필터 체인이 동작을 하게 된다.

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/signup").permitAll()
                        //.requestMatchers("/static/**").permitAll() // 필터 체인을 거치지 않게 설정해줬음
                        .anyRequest().authenticated()
                )
//                .httpBasic(form -> form.configure(http)) 내부 사설망에서 더 엄격한 보안을 위해서 주로 사용된다고 한다.

                .formLogin(form -> form.loginPage("/") // 기본 로그인 페이지를 "/" 경로로 지정, 잘못된 URL 입력 시 로그인 페이지로 보냄
                        .defaultSuccessUrl("/main")
                        .loginProcessingUrl("/login") // form 태그에서 로그인 할 정보를 특정 URL로 POST 할 텐데 해당 경로를 가지고 Spring Security 가 로그인 처리를 진행함
                                                      // processingURL에 경로를 지정하게 되면 로그인을 담당하는 UsernamePasswordAuthenticaionFilter가 어떤 주소에 동작할 지 set 해주는 메서드
                        .failureHandler(customAuthenticationFailureHandler())
                        .permitAll())

                .sessionManagement(session -> session
                        .maximumSessions(1) //CustomUserDetails 를 만든 경우 hashcode, equals 메서드를 구현해줘야 한다.
                        .maxSessionsPreventsLogin(false)
                        .expiredSessionStrategy(customSessionExpiredStrategy())
                );

        http.sessionManagement(auth -> auth.sessionFixation().changeSessionId()); // 로그인 시 동일한 세션에 대한 id 변경

        //세션 ID 변경: 이 옵션을 사용하면 사용자가 로그인하거나 중요한 작업(예: 권한 변경)을 수행할 때, 세션 ID만 변경됩니다. 이 과정에서 기존 세션에 저장된 속성과 데이터는 유지됩니다.
        //기존 세션 데이터 유지: changeSessionId()는 기존 세션 데이터를 새로운 세션 ID로 그대로 옮겨서 유지합니다. 세션 데이터가 사라지지 않고, 사용자에게는 로그아웃 없이 연속적인 사용 환경이 제공됩니다.


        // csrf 방지 동작이 작동하면 CSRF 토큰을 보내주어야 로그인이 되는데 개발 환경에서는 disable 한다.
        // 토큰을 가지고 (ex.JWT) 서버 로그인을 하는 경우 csrf 비활성화 해도 된다.
        //http.csrf(AbstractHttpConfigurer::disable);


                // 기본은 withDefault(), 스프링이 제공하는 로그인 페이지
                // 초기화 시 인증 방식 2개가 있음
                // httpBasic, formLogin

        // 커스텀 필터 등록
        // .addFilterBefore, At, After(추가할 필터, 기존 필터.class)
        // Before 기존 필터 이전에 등록한다. At 기존 필터 위치에 등록한다. After 기존 필터 이후에 등록한다.
        // 예시로 JWT 인증 필터를 등록할 때 사용을 한다.

        // 궁금증
        // FilterAt은 기존 필터에 위치하게 되고 순차적으로 실행이 된다고 하는데 Before랑 똑같이 동작하는 거 아닌가 ?
        // 여러 개의 커스텀 필터를 넣는다고 가정을 해보면  | 커스텀 필터 > 기존 필터 | 이렇게 At 메서드로 추가를 했다고 쳐보자
        // 이때 새로운 커스텀 필터를 내가 추가한 커스텀 필터 앞에다 추가할 것이냐, 기존 필터 앞에다가 추가할 것이냐에 따라서 Before, At 을 쓰면 된다.
        // Before를 하면 이 전에 넣었던 커스텀 필터는 At이기 때문에 Before 보다는 늦게 실행이 되게 되서 추가한 필터가 먼저 실행이 되고
        // At으로 넣게 되면 이 전에 넣었던 커스텀 필터보다 늦게 들어온 것이기 때문에 전에 넣었던 필터보다는 늦게 실행이 되게 된다.

        // 커스텀 필터는 GenericFilterBean, OncePerRequestFilter 둘 중 하나를 상속 받아서 사용한다.
        // 이 떄 동작 방식이 다르기 때문에 잘 알고 사용해야 한다.

        return http.build();

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
