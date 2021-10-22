package com.epam.esm.service.impl;

import com.epam.esm.dao.RoleDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.builder.UserBuilder;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Role;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.exception.ValidationException;
import com.epam.esm.util.UtilClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import javax.persistence.Query;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao<Long> userDao;
    private Validator paramValidator;
    private final UserBuilder userBuilder;
    //private final BCryptPasswordEncoder passwordEncoder;
    private final RoleDao<Long> roleDao;

    @Autowired
    @Qualifier("paramValidator")
    public void setParamValidator(Validator paramValidator) {
        this.paramValidator = paramValidator;
    }

    @Autowired
    public UserServiceImpl(UserDao<Long> userDao, UserBuilder userBuilder, RoleDao<Long> roleDao) {
        this.userBuilder = userBuilder;
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> find(Map<String, String> params) {
        EnumMap<ParamName, String> enumMap = UtilClass.convertToEnumMap(params);
        Errors errors = new MapBindingResult(enumMap, "params");
        paramValidator.validate(enumMap, errors);
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        Query query = userBuilder.buildQuery(enumMap);
        return userDao.find(query);
    }

    @Override
    public long getTotalRows() {
        return userDao.countUsers();
    }

    @Override
    public User register(User user) {
        Role roleUser = roleDao.findByName("USER");
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleUser);
        user.setRoles(userRoles);
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        User registeredUser = userDao.create(user);

        logger.info("IN register - user: {} successfully registered", registeredUser);

        return registeredUser;
    }
}
