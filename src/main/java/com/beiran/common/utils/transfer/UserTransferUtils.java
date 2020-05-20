package com.beiran.common.utils.transfer;

import com.beiran.common.utils.DateTimeUtils;
import com.beiran.core.system.dto.JobSmallDto;
import com.beiran.core.system.dto.RoleSmallDto;
import com.beiran.core.system.dto.UserDto;
import com.beiran.core.system.entity.Dept;
import com.beiran.core.system.entity.Job;
import com.beiran.core.system.entity.Role;
import com.beiran.core.system.entity.User;
import com.beiran.core.system.vo.UserVo;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 提供 User-UserDto 相互转换的工具
 */

@Log
public class UserTransferUtils {

    /**
     * 将传入的 User 转换为 UserDto
     * @param user 需要转换的 User
     * @return UserDto
     */
    public static UserDto userToDto(User user) {
        UserDto userDto = new UserDto();
        // 复制属性
        if (!Objects.equals(user, null)) {
            BeanUtils.copyProperties(user, userDto);
        }
        // 需要手动设置的属性
        JobSmallDto jobSmallDto = new JobSmallDto();
        if (!Objects.equals(user.getUserJob(), null)) {
            BeanUtils.copyProperties(user.getUserJob(), jobSmallDto);
            // FIXME 可能会报空指针
//            jobSmallDto.setDeptId(user.getUserJob().getJobDept().getDeptId());
//            jobSmallDto.setDeptName(user.getUserJob().getJobDept().getDeptName());
        }
        userDto.setJobSmallDto(jobSmallDto);
        Set<RoleSmallDto> roles = new HashSet<>();
        if (!user.getUserRoles().isEmpty() && user.getUserRoles().size() > 0) {
            user.getUserRoles().stream().forEach(role -> {
                RoleSmallDto roleSmallDto = new RoleSmallDto();
                BeanUtils.copyProperties(role, roleSmallDto);
                roles.add(roleSmallDto);
            });
        }
        userDto.setRoles(roles);
        if (!Objects.equals(user.getUserState(), null)) {
            userDto.setState(user.getUserState().getValue());
        }
        if (!Objects.equals(user.getUserSex(), null)) {
            userDto.setSex(user.getUserSex().getValue());
        }
        if (!Objects.equals(user.getUserCreateTime(), null)) {
            userDto.setCreateTime(DateTimeUtils.getDateTime(user.getUserCreateTime()));
        }
        return userDto;
    }

    /**
     * 将传入的 UserDto 转换为 User
     * @param userDto 需要转换的 UserDto
     * @return User
     */
    public static User dtoToUser(UserDto userDto) {
        User user = new User();
        // 复制属性
        if (!Objects.equals(userDto, null)) {
            BeanUtils.copyProperties(userDto, user);
        }
        // 需要手动设置的属性 state、sex、createTime、job、roles
        Job job = new Job();
        if (!Objects.equals(userDto.getJobSmallDto(), null)) {
            BeanUtils.copyProperties(userDto.getJobSmallDto(), job);
            Dept dept = new Dept();
            dept.setDeptId(userDto.getJobSmallDto().getDeptId());
            dept.setDeptName(userDto.getJobSmallDto().getDeptName());
            job.setJobDept(dept);
        }
        user.setUserJob(job);

        Set<Role> roles = new HashSet<>();
        if (!userDto.getRoles().isEmpty() && userDto.getRoles().size() > 0) {
            userDto.getRoles().stream().forEach(roleSmallDto -> {
                Role role = new Role();
                BeanUtils.copyProperties(roleSmallDto, role);
                roles.add(role);
            });
        }
        user.setUserRoles(roles);

        if (Objects.equals(userDto.getState(), "启用")) {
            user.setUserState(User.UserState.ACTIVE);
        } else {
            user.setUserState(User.UserState.DISABLED);
        }

        if (Objects.equals(userDto.getSex(), "男")) {
            user.setUserSex(User.UserSex.MALE);
        } else if (Objects.equals(userDto.getSex(), "女")) {
            user.setUserSex(User.UserSex.FEMALE);
        } else {
            user.setUserSex(User.UserSex.SECRET);
        }

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_TIMESTAMP);
        try {
            date = simpleDateFormat.parse(userDto.getCreateTime());
        } catch (ParseException e) {
            log.info(" { 日期转换异常 } ");
            date = null;
        }
        user.setUserCreateTime(date);
        return user;
    }

    /**
     * 将传入的 UserVo 转换为 User
     * @param userVo 需要转换的 UserVo
     * @return User
     */
    public static User voToUser(UserVo userVo) {
        User user = new User();
        // 复制属性
        if (!Objects.equals(userVo, null)) {
            BeanUtils.copyProperties(userVo, user);
        }
        // 手动设置属性
        Job job = new Job();
        if (StringUtils.hasText(userVo.getJob())) {
            job.setJobId(userVo.getJob());
        }
        user.setUserJob(job);
        Set<Role> roles = new HashSet<>();
        if (!Objects.equals(userVo.getRoles(), null)) {
            userVo.getRoles().stream().forEach(roleId -> {
                Role role = new Role();
                role.setRoleId(roleId);
                roles.add(role);
            });
        }
        user.setUserRoles(roles);
        return user;
    }
}
