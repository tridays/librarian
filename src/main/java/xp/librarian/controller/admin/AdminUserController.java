package xp.librarian.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import xp.librarian.controller.BaseController;
import xp.librarian.model.form.PagingForm;
import xp.librarian.service.admin.UserService;

/**
 * @author xp
 */
@Api(
        description = "Admin::Users 用户管理"
)
@Controller
@RequestMapping(value = "/admin/")
public class AdminUserController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping("users/")
    public Object getUsers(PagingForm paging) {
        return renderForEntities(userService.getUsers(paging));
    }

    @GetMapping("users/{userId}/")
    public Object getUser(@PathVariable Long userId) {
        return renderForEntity(userService.getUser(userId));
    }

    @PostMapping("users/{userId}/freeze")
    public Object freezeUser(@PathVariable Long userId) {
        userService.freeze(userId);
        return renderForAction(true);
    }

    @PostMapping("users/{userId}/unfreeze")
    public Object unfreezeUser(@PathVariable Long userId) {
        userService.unfreeze(userId);
        return renderForAction(true);
    }

    @PostMapping("users/{userId}/delete")
    public Object deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        return renderForAction(true);
    }

}
