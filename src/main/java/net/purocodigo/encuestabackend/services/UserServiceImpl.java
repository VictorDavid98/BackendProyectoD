package net.purocodigo.encuestabackend.services;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.purocodigo.encuestabackend.entities.UserEntity;
import net.purocodigo.encuestabackend.models.requests.UserRegisterRequestModel;
import net.purocodigo.encuestabackend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserEntity createUser(UserRegisterRequestModel user) {
        UserEntity userEntity = new UserEntity();

        BeanUtils.copyProperties(user, userEntity);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        userEntity.setRole("USER");

        return userRepository.save(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        String role = "ROLE_" + userEntity.getRole();

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(role));
    }

    @Override
    public UserEntity getUser(String email) {
        return userRepository.findByEmail(email);
    }

}
