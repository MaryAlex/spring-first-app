package com.springapp.mvc.service.Impl;

import com.springapp.mvc.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService
{
	@Autowired
	private UserDao userDao;

	@Transactional(readOnly=true)
	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException
	{

		com.springapp.mvc.model.User user = userDao.findUserByEmail(email);
		return buildUserForAuthentication(user);
	}
	private User buildUserForAuthentication(com.springapp.mvc.model.User user) {
		Set<GrantedAuthority> roles = new HashSet();
		roles.add(new SimpleGrantedAuthority("USER"));
		if(user.getSecret() == null)
			return new User(user.getNickname(), user.getPassword(), roles);
		return new User(user.getNickname(), user.getPassword(), false, true, true, true, roles);
	}
}
