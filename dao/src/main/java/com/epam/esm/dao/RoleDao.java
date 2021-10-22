package com.epam.esm.dao;

import com.epam.esm.entity.Role;

public interface RoleDao<K> {
    Role findByName(String name);
}

