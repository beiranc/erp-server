package com.beiran.core.system.service;

import com.beiran.common.service.GenericService;
import com.beiran.core.system.dto.UserDto;
import com.beiran.core.system.entity.User;
import com.beiran.core.system.vo.UserPartVo;
import com.beiran.core.system.vo.UserVo;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;


/**
 * 用户业务操作
 */
public interface UserService extends GenericService<User, String> {

    /**
     * 根据给定的 UserVo 创建一个用户
     * @param userVo UserVo
     * @return UserDto
     */
    UserDto createUser(UserVo userVo);

    /**
     * 根据用户名查询一个用户
     * @param userName 用户名
     * @return UserDto
     */
    UserDto getUserByName(String userName);

    /**
     * 根据邮箱地址查询一个用户
     * @param userEmail 用户邮箱
     * @return UserDto
     */
    UserDto getUserByEmail(String userEmail);

    /**
     * 根据用户编号修改用户状态
     * @param userId 用户编号
     * @param userState 用户状态
     * @return 是否修改成功
     */
    Boolean updateState(String userId, User.UserState userState);

    /**
     * 根据用户名重置密码，默认为 123456
     * @param userName 用户名
     * @return 是否修改成功
     */
    Boolean resetPassword(String userName);

    /**
     * 根据用户名修改密码
     * @param userName 用户名
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    Boolean updatePassword(String userName, String newPassword);

    /**
     * 根据用户名修改邮箱
     * @param userName 用户名
     * @param newEmail 新邮箱
     * @return 是否修改成功
     */
    Boolean updateEmail(String userName, String newEmail);

    /**
     * 根据用户名修改联系方式
     * @param userName 用户名
     * @param newPhone 新联系方式
     * @return 是否修改成功
     */
    Boolean updatePhone(String userName, String newPhone);

    /**
     * 修改用户部分资料
     * @param userPartVo 需要修改的用户资料
     * @return 是否修改成功
     */
    Boolean updateUserInfo(UserPartVo userPartVo);

    /**
     * 根据部门编号获取用户信息
     * @param deptId 部门编号
     * @return List<UserDto>
     */
    List<UserDto> getUsersByDept(String deptId, Pageable pageable);

    /**
     * 根据传入的关键字模糊查询用户信息（匹配的为 userName 和 nickName）
     * @param keyWord 关键字
     * @return List<UserDto>
     */
    List<UserDto> getUsersByKeyWord(String keyWord, Pageable pageable);

    /**
     * 根据用户状态查询用户信息
     * @param userState 用户状态
     * @return List<UserDto>
     */
    List<UserDto> getUsersByState(User.UserState userState, Pageable pageable);

    /**
     * 导出用户信息
     * @param pageable 分页参数
     * @return File
     */
    File createExcelFile(Pageable pageable);
}
