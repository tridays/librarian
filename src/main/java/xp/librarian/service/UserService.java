package xp.librarian.service;

import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import xp.librarian.model.context.AccountContext;
import xp.librarian.model.context.BusinessException;
import xp.librarian.model.context.ErrorCode;
import xp.librarian.model.context.ResourceNotFoundException;
import xp.librarian.model.dto.Role;
import xp.librarian.model.dto.User;
import xp.librarian.model.form.UserLoginForm;
import xp.librarian.model.form.UserRegisterForm;
import xp.librarian.model.form.UserUpdateForm;
import xp.librarian.model.result.UserProfileVM;
import xp.librarian.repository.UserDao;
import xp.librarian.utils.MurmurHash3;
import xp.librarian.utils.TimeUtils;
import xp.librarian.utils.UploadUtils;

/**
 * @author xp
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private Validator validator;

    @Autowired
    private UserDao userDao;

    private UserProfileVM buildUserProfileVM(@NonNull User user) {
        return new UserProfileVM().withUser(user);
    }

    public UserProfileVM register(@Valid UserRegisterForm form) {
        form.validate(validator);

        User existUser = new User().setUsername(form.getUsername());
        if (userDao.get(existUser, true) != null) {
            throw new BusinessException(ErrorCode.USER_EXISTS);
        }
        User user = form.forSet()
                .setId(MurmurHash3.x64_64(form.getUsername()))
                .setAvatarPath(UploadUtils.upload(form.getAvatar()))
                .setStatus(User.Status.NORMAL)
                .setCreateTime(TimeUtils.now());
        if (0 == userDao.add(user)) {
            throw new PersistenceException("user insert failed.");
        }
        if (0 == userDao.addRole(user, Role.READER)) {
            throw new PersistenceException("user role insert failed.");
        }
        return buildUserProfileVM(user);
    }

    public UserProfileVM login(@Valid UserLoginForm form) {
        form.validate(validator);

        User where = form.forWhere();
        User user = userDao.get(where);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_LOGIN_FAIL);
        }
        return buildUserProfileVM(user);
    }

    public void logout(@NonNull AccountContext account) {
        // need to do nothing now.
    }

    public UserProfileVM getProfile(@NonNull AccountContext account) {
        User user = userDao.get(account.getId());
        if (user == null) {
            throw new ResourceNotFoundException("user not found.");
        }
        return buildUserProfileVM(user);
    }

    public UserProfileVM setProfile(@NonNull AccountContext account, @Valid UserUpdateForm form) {
        form.validate(validator);

        User where = account.toDTO();
        User set = form.forSet()
                .setAvatarPath(UploadUtils.upload(form.getAvatar()));
        if (0 == userDao.update(where, set)) {
            throw new PersistenceException("user update failed.");
        }
        return buildUserProfileVM(userDao.get(account.getId()));
    }

}
