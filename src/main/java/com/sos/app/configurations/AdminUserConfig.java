package com.sos.app.configurations;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sos.app.models.RoleModel;
import com.sos.app.models.UserModel;
import com.sos.app.repository.RoleRepository;
import com.sos.app.repository.UserRepository;

import jakarta.transaction.Transactional;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(!roleRepository.existsByName("ADMIN")) {
            var addRoleAdmin = new RoleModel();
            addRoleAdmin.setName("ADMIN");
            roleRepository.save(addRoleAdmin);  
        }

        if(!roleRepository.existsByName("BASIC")) {
            var addRoleBasic = new RoleModel();
            addRoleBasic.setName("BASIC");
            roleRepository.save(addRoleBasic);  
        }

        RoleModel role = roleRepository.findByName("ADMIN").get();
        Optional<UserModel> userAdmin = userRepository.findByUsername("admin");

        if(userAdmin != null) {
            userAdmin.ifPresentOrElse(
                (user) -> {
                    System.out.println("Admin já existe");
                },
                () -> {
                    var user = new UserModel();
                    user.setUsername("admin");
                    user.setName("Admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setIdRole(role.getId());
                    userRepository.save(user);
                });
        }
    }
}