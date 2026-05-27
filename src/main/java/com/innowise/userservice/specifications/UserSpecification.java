package com.innowise.userservice.specifications;

import org.springframework.data.jpa.domain.Specification;
import com.innowise.userservice.model.User;

public class UserSpecification {

    public static Specification<User> hasName(String name) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("surname")),
                        "%" + surname.toLowerCase() + "%"
                );
    }
}
