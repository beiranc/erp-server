package com.beiran.core.system.repository;

import com.beiran.core.system.entity.Dept;
import com.beiran.core.system.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 用户 Repository
 */
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    /**
     * 根据用户名查找一个用户
     * @param userName 用户名
     * @return Optional<User>
     */
    Optional<User> findByUserName(String userName);

    /**
     * 根据部门信息查询用户
     * @param deptId 部门编号
     * @param pageable 分页参数
     * @return List<User>
     */
    List<User> findByUserJob_JobDept_DeptId(String deptId, Pageable pageable);

    /**
     * 根据关键字进行模糊匹配（匹配 userName 与 nickName）
     * @param keyWord 关键字
     * @param pageable 分页参数
     * @return List<User>
     */
    @Query("FROM User u WHERE u.userName LIKE %:keyWord% OR u.nickName LIKE %:keyWord%")
    List<User> findByUserNameAndNickName(@Param("keyWord") String keyWord, Pageable pageable);

    /**
     * 根据用户状态查询用户
     * @param userState 用户状态
     * @param pageable 分页参数
     * @return List<User>
     */
    List<User> findByUserState(User.UserState userState, Pageable pageable);

    /**
     * 根据用户邮箱查询用户
     * @param userEmail 用户邮箱
     * @return Optional<User>
     */
    Optional<User> findByUserEmail(String userEmail);

    /**
     * 根据用户名修改密码（存储的为加密后的密码）
     * @param userName 用户名
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_user SET user_password = ?2 WHERE user_name = ?1", nativeQuery = true)
    int updatePassword(String userName, String newPassword);

    /**
     * 根据用户名更改邮箱
     * @param userName 用户名
     * @param newEmail 新的邮箱地址
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_user SET user_email = ?2 WHERE user_name = ?1", nativeQuery = true)
    int updateEmail(String userName, String newEmail);

    /**
     * 根据用户名更改联系方式
     * @param userName 用户名
     * @param newPhone 新的联系方式
     * @return 是否修改成功
     */
    @Modifying
    @Query(value = "UPDATE erp_user SET user_phone = ?2 WHERE user_name = ?1", nativeQuery = true)
    int updatePhone(String userName, String newPhone);

    /**
     * 根据用户编号修改用户状态
     * @param userId 用户状态
     * @param userState 用户状态
     * @return 是否修改成功
     */
//    @Modifying
//    @Query(value = "UPDATE erp_user SET user_state = ?2 WHERE user_id = ?1", nativeQuery = true)
//    int updateState(String userId, User.UserState userState);
}
