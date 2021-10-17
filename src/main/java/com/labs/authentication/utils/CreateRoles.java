package com.labs.authentication.utils;

import com.labs.authentication.entity.Role;
import com.labs.authentication.enums.RoleName;
import com.labs.authentication.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CreateRoles implements CommandLineRunner {

    @Autowired
    RoleService roleService;

    //  * VERY IMPORTANT: THIS CLASS WILL ONLY BE RUN ONCE TO CREATE THE ROLES.
    //  * ONCE CREATED THE CODE MUST BE DELETED OR COMMENTED

    @Override
    public void run(String... args) throws Exception {
//        Role roleAdmin = new Role(RoleName.ROLE_ADMIN);
//        Role roleUser = new Role(RoleName.ROLE_USER);
//         roleService.save(roleAdmin);
//         roleService.save(roleUser);
    }
}
