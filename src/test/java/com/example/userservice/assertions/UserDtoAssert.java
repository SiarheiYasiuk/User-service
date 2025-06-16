package com.example.userservice.assertions;

import com.example.userservice.dto.UserDto;
import org.assertj.core.api.AbstractAssert;

public class UserDtoAssert extends AbstractAssert<UserDtoAssert, UserDto> {

    public UserDtoAssert(UserDto actual) {
        super(actual, UserDtoAssert.class);
    }

    public static UserDtoAssert assertThat(UserDto actual) {
        return new UserDtoAssert(actual);
    }

    public UserDtoAssert hasId(Long expectedId) {
        isNotNull();
        if (!actual.getId().equals(expectedId)) {
            failWithMessage("Expected user id to be <%s> but was <%s>", expectedId, actual.getId());
        }
        return this;
    }

    public UserDtoAssert hasName(String expectedName) {
        isNotNull();
        if (!actual.getName().equals(expectedName)) {
            failWithMessage("Expected user name to be <%s> but was <%s>", expectedName, actual.getName());
        }
        return this;
    }

    public UserDtoAssert hasEmail(String expectedEmail) {
        isNotNull();
        if (!actual.getEmail().equals(expectedEmail)) {
            failWithMessage("Expected user email to be <%s> but was <%s>", expectedEmail, actual.getEmail());
        }
        return this;
    }

    public UserDtoAssert hasAge(int expectedAge) {
        isNotNull();
        if (actual.getAge() != expectedAge) {
            failWithMessage("Expected user age to be <%s> but was <%s>", expectedAge, actual.getAge());
        }
        return this;
    }
}