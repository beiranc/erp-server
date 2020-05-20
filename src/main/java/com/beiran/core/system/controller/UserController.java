package com.beiran.core.system.controller;

import com.beiran.common.annotation.LogRecord;
import com.beiran.common.respone.ResponseModel;
import com.beiran.common.utils.FileUtils;
import com.beiran.core.system.dto.UserDto;
import com.beiran.core.system.entity.User;
import com.beiran.core.system.service.UserService;
import com.beiran.core.system.vo.UserPartVo;
import com.beiran.core.system.vo.UserPasswordVo;
import com.beiran.core.system.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户相关接口<br>
 * 添加用户权限: system:user:add<br>
 * 修改用户权限: system:user:edit<br>
 * 查询用户权限: system:user:view<br>
 * 删除用户权限: system:user:del<br>
 * 导出用户权限: system:user:export<br>
 */
@RestController
@RequestMapping("/api/v1/users")
@Api(tags = "系统管理：用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 创建一个用户
     * @param userVo 待保存的用户信息
     * @return 保存后的用户信息
     */
    @PostMapping
    @LogRecord("创建用户账号")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:add')")
    @ApiOperation("创建用户")
    public ResponseModel register(@RequestBody @Valid UserVo userVo) {
        return ResponseModel.ok(userService.createUser(userVo));
    }

    /**
     * 修改用户资料
     * @param user 用户信息
     * @return
     */
    @PutMapping
    @LogRecord("修改用户资料")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:edit')")
    @ApiOperation("修改用户资料")
    public ResponseModel editUserInfo(@RequestBody User user) {
        return ResponseModel.ok(userService.update(user));
    }

    /**
     * 根据用户编号修改用户状态
     * @param userId 用户编号
     * @param userState 用户状态
     * @return
     */
    @PutMapping("/state")
    @LogRecord("修改用户状态")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:edit')")
    @ApiOperation("修改用户状态")
    public ResponseModel editState(@RequestParam("userId") String userId,
                                   @RequestParam("userState") User.UserState userState) {
        boolean result = userService.updateState(userId, userState);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 根据用户名重置密码
     * @param userName 用户名
     * @return
     */
    @PutMapping("/reset")
    @LogRecord("重置密码")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:edit')")
    @ApiOperation("重置密码")
    public ResponseModel resetPassword(@RequestParam("userName") String userName) {
        boolean result = userService.resetPassword(userName);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 根据用户名修改密码
     * @param userPasswordVo
     * @return
     */
    @PutMapping("/edit_password")
    @LogRecord("修改密码")
    @PreAuthorize("@erp.check('system:user:edit')")
    @ApiOperation("修改密码")
    public ResponseModel editPassword(@RequestBody @Valid UserPasswordVo userPasswordVo) {
        // 先判断旧密码是否匹配
        UserDto user = userService.getUserByName(userPasswordVo.getUserName());
        if (bCryptPasswordEncoder.matches(userPasswordVo.getOldPassword(),user.getUserPassword())) {
            // 若输入的旧密码与数据库中的匹配才进行修改操作
            boolean result = userService.updatePassword(userPasswordVo.getUserName(), userPasswordVo.getNewPassword());
            if (result) {
                return ResponseModel.ok("修改成功");
            } else {
                return ResponseModel.error("修改失败");
            }
        } else {
            return ResponseModel.error("旧密码不正确");
        }
    }

    /**
     * 根据用户名修改邮箱地址
     * @param userName 用户名
     * @param email 新邮箱
     * @return
     */
    @PutMapping("/edit_email")
    @LogRecord("修改邮箱")
    @PreAuthorize("@erp.check('system:user:edit')")
    @ApiOperation("修改邮箱")
    public ResponseModel editEmail(@RequestParam("userName") String userName,
                                   @RequestParam("email") String email) {
        boolean result = userService.updateEmail(userName, email);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 根据用户名修改用户联系方式
     * @param userName 用户名
     * @param phone 新联系方式
     * @return
     */
    @PutMapping("/edit_phone")
    @LogRecord("修改联系方式")
    @PreAuthorize("@erp.check('system:user:edit')")
    @ApiOperation("修改联系方式")
    public ResponseModel editPhone(@RequestParam("userName") String userName,
                                   @RequestParam("phone") String phone) {
        boolean result = userService.updatePhone(userName, phone);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 修改部分用户资料
     * @param userPartVo
     * @return
     */
    @PutMapping("/edit_info")
    @LogRecord("修改用户部分资料")
    @PreAuthorize("@erp.check('system:user:edit')")
    @ApiOperation("修改用户部分资料")
    public ResponseModel editUserInfo(@RequestBody @Valid UserPartVo userPartVo) {
        boolean result = userService.updateUserInfo(userPartVo);
        if (result) {
            return ResponseModel.ok("修改成功");
        } else {
            return ResponseModel.error("修改失败");
        }
    }

    /**
     * 分页查询用户
     * @param pageable
     * @return
     */
    @GetMapping
    @LogRecord("分页查询用户")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:view')")
    @ApiOperation("分页查询用户")
    public ResponseModel getUsers(@PageableDefault Pageable pageable) {
        return ResponseModel.ok(userService.findAll(pageable));
    }

    /**
     * 根据用户编号查询用户信息
     * @param userId 用户编号
     * @return
     */
    @GetMapping("/id")
    @LogRecord("通过用户编号查询用户")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:view')")
    @ApiOperation("根据用户编号查询用户")
    public ResponseModel getUserById(@RequestParam("userId") String userId) {
        return ResponseModel.ok(userService.findById(userId));
    }

    /**
     * 根据用户名查询用户
     * @param userName 用户名
     * @return
     */
    @GetMapping("/name")
    @LogRecord("通过用户名查询用户")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:view')")
    @ApiOperation("根据用户名查询用户")
    public ResponseModel getUserByName(@RequestParam("userName") String userName) {
        UserDto userDto = userService.getUserByName(userName);
        if (!Objects.equals(userDto, null)) {
            return ResponseModel.ok(userDto);
        } else {
            return ResponseModel.error("用户不存在");
        }
    }

    /**
     * 根据用户邮箱查询用户
     * @param email 用户邮箱
     * @return
     */
    @GetMapping("/email")
    @LogRecord("通过用户邮箱查询用户")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:view')")
    @ApiOperation("根据用户邮箱查询用户")
    public ResponseModel getUserByEmail(@RequestParam("email") String email) {
        return ResponseModel.ok(userService.getUserByEmail(email));
    }

    /**
     * 根据部门编号查询用户
     * @param deptId 部门编号
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/dept")
    @LogRecord("通过部门编号查询用户")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:view')")
    @ApiOperation("根据部门编号查询用户")
    public ResponseModel getUsersByDept(@RequestParam("deptId") String deptId,
                                        @PageableDefault Pageable pageable) {
        return ResponseModel.ok(userService.getUsersByDept(deptId, pageable));
    }

    /**
     * 根据关键词搜索用户
     * @param keyWord 关键词
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/search")
    @LogRecord("通过关键词查询用户")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:view')")
    @ApiOperation("根据关键词查询用户")
    public ResponseModel getUsersByKeyWord(@RequestParam("key") String keyWord,
                                           @PageableDefault Pageable pageable) {
        return ResponseModel.ok(userService.getUsersByKeyWord(keyWord, pageable));
    }

    /**
     * 根据用户状态查询用户
     * @param userState 用户状态
     * @param pageable 分页参数
     * @return
     */
    @GetMapping("/state")
    @LogRecord("通过用户状态查询用户")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:view')")
    @ApiOperation("根据用户状态查询用户")
    public ResponseModel getUsersByState(@RequestParam("userState") User.UserState userState,
                                         @PageableDefault Pageable pageable) {
        return ResponseModel.ok(userService.getUsersByState(userState, pageable));
    }

    /**
     * 导出用户信息
     * @param pageable 分页参数
     * @param response
     * @throws Exception
     */
    @GetMapping("/export")
    @LogRecord("导出用户信息")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:export')")
    @ApiOperation("导出用户信息")
    public void exportUserInfo(@PageableDefault(size = 200) Pageable pageable,
                               HttpServletResponse response) throws Exception {
        File file = userService.createExcelFile(pageable);
        FileUtils.downloadFile(response, file, file.getName());
    }

    /**
     * 删除用户
     * @param userIds 用户编号集合
     * @return
     */
    @DeleteMapping
    @LogRecord("删除用户")
    @PreAuthorize("@erp.check('admin') and @erp.check('system:user:del')")
    @ApiOperation("删除用户")
    public ResponseModel delUser(@RequestBody List<String> userIds) {
        if (userIds.isEmpty() || Objects.equals(userIds, null)) {
            return ResponseModel.error("需要删除的用户不能为空");
        } else {
            List<User> users = userIds.stream().map(userId -> {
                User user = new User();
                user.setUserId(userId);
                return user;
            }).collect(Collectors.toList());
            userService.deleteAll(users);
            return ResponseModel.ok("删除成功");
        }
    }
}
