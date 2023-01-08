package com.yapp.web2.domain.jwt.user

import com.yapp.web2.domain.member.model.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
	val member: Member
) : UserDetails {
	override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
		return null
	}

	override fun getPassword(): String? {
		return null
	}

	override fun getUsername(): String {
		return member.email
	}

	override fun isAccountNonExpired(): Boolean {
		return true
	}

	override fun isAccountNonLocked(): Boolean {
		return true
	}

	override fun isCredentialsNonExpired(): Boolean {
		return true
	}

	override fun isEnabled(): Boolean {
		return true
	}
}
