package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserAuthenticationService {

  @Autowired private UserDao userDao;

  @Autowired private UserAuthDao userAuthDao;

  @Autowired private PasswordCryptographyProvider passwordCryptographyProvider;

  /**
   * This method checks if the username and email exist in the DB. if the username or email doesn't
   * exist in the DB.then assign uuid to the user. Assign encrypted password and salt to the user.
   *
   * @throws SignUpRestrictedException SGR-001 if the username exist in the DB , SGR-002 if the
   *     email exist in the DB.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
    if (isUserNameInUse(userEntity.getUserName())) {
      throw new SignUpRestrictedException(
          "SGR-001", "Try any other Username, this Username has already been taken");
    }

    if (isEmailInUse(userEntity.getEmail())) {
      throw new SignUpRestrictedException(
          "SGR-002", "This user has already been registered, try with any other emailId");
    }
    // Assign a UUID to the user that is being created.
    userEntity.setUuid(UUID.randomUUID().toString());
    // Assign encrypted password and salt to the user that is being created.
    String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
    userEntity.setSalt(encryptedText[0]);
    userEntity.setPassword(encryptedText[1]);
    return userDao.createUser(userEntity);
  }


  // checks whether the username exist in the database
  private boolean isUserNameInUse(final String userName) {
    return userDao.getUserByUserName(userName) != null;
  }

  // checks whether the email exist in the database
  private boolean isEmailInUse(final String email) {
    return userDao.getUserByEmail(email) != null;
  }
}
