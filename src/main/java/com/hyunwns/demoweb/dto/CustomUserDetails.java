package com.hyunwns.demoweb.dto;

import com.hyunwns.demoweb.domain.Member;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // GrantedAuthority 인터페이스
        // 사용자의 권한을 나타내기 위해 사용되는 인터페이스, 이 인터페이스는 Authenticaion 객체에 포함되어 사용자의 권한 정보를 제공하며, 이를 통해 특정 리소스나
        // 작업에 대한 접근 권한을 결정할 수 있다.

        // 의문점. 그냥 권한을 가져오는 간단한 인터페이스면 데이터베이스에 있는 유저 권한 정보를 가지고 하면 되는 것 아닌가?
        // 1. Security Context 에서의 관리: Spring Security 는 인증된 사용자의 정보를 Security Context 에 저장하고, 이 컨텍스트는 애플리케이션 여러 계층에서
        // 접근 할 수 있다. GrantedAuthority는 이 컨텍스트에서 사용자의 권한을 나타내는 데 사용된다. 이는 권한 정보가 애플리케이션의 여러 곳에서 쉽게 사용될 수 있게 한다.
        // 2. 보안 기능과의 통합 : Spring Security는 다양한 보안 기능을 제공하며, 이 기능들은 GrantedAuthority를 기반으로 작동한다.
        // 3. 커스터마이징 용이
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole()));

        return authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(member);
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getId();
    }

/* 여기 아래에 존재하는 값들은 데이터베이스에 같이 존재하여 비교해주어야 하는데 해당 값은 현재 테스트에서는 넣어주지 않았다. 따라서 기본적으로 true를 넘겨주도록 한다. */

    // 사용자 아이디가 활성화 상태인지
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // 사용자의 비밀번호가 만료되었는지
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // 사용자의 아이디가 잠겼는지
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // 사용자 계정이 만료되었는지
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

}
