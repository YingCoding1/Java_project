package com.skillup.domain.user;

//@Repository
public interface UserRepository {
    void createUser(UserDomain userDomain);
    UserDomain getUserByUserName(String userName);
    UserDomain getUserByUserId(String userId);
    void  updateUser(UserDomain userDomain);
}
