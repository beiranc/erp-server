package com.beiran.core.system.service.impl;

import com.beiran.common.exception.EntityExistException;
import com.beiran.common.exception.EntityNotExistException;
import com.beiran.common.exception.ParameterException;
import com.beiran.common.utils.DateTimeUtils;
import com.beiran.common.utils.FileUtils;
import com.beiran.common.utils.transfer.UserTransferUtils;
import com.beiran.core.system.dto.UserDto;
import com.beiran.core.system.entity.Role;
import com.beiran.core.system.entity.User;
import com.beiran.core.system.repository.UserRepository;
import com.beiran.core.system.service.UserService;
import com.beiran.core.system.vo.UserPartVo;
import com.beiran.core.system.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserVo userVo) {
        // 不开事务，因为 save() 方法那边有了
        String secretPassword = bCryptPasswordEncoder.encode(userVo.getUserPassword());
        userVo.setUserPassword(secretPassword);
        User user = UserTransferUtils.voToUser(userVo);
        User save = save(user);
        if (!Objects.equals(save, null)) {
            return UserTransferUtils.userToDto(save);
        }
        return null;
    }

    @Override
    public UserDto getUserByName(String userName) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        // FIXME 这边设为 null，由调用方自行判断
        User user = userRepository.findByUserName(userName).orElse(null);
        if (Objects.equals(user, null)) {
            return null;
        } else {
            return UserTransferUtils.userToDto(user);
        }
    }

    @Override
    public UserDto getUserByEmail(String userEmail) {
        if (!StringUtils.hasText(userEmail)) {
            throw new ParameterException("用户邮箱不能为空");
        }
        User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new EntityNotExistException("用户不存在"));
        return UserTransferUtils.userToDto(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateState(String userId, User.UserState userState) {
        if (!StringUtils.hasText(userId)) {
            throw new ParameterException("用户编号不能为空");
        }
        if (Objects.equals(userState, null)) {
            throw new ParameterException("用户状态不能为空");
        }
        User user = findById(userId);
        user.setUserState(userState);
        User update = update(user);
        return !Objects.equals(update,null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetPassword(String userName) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        String newPassword = bCryptPasswordEncoder.encode("123456");
        return userRepository.updatePassword(userName, newPassword) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePassword(String userName, String newPassword) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new ParameterException("新密码不能为空");
        }
        String secret = bCryptPasswordEncoder.encode(newPassword);
        return userRepository.updatePassword(userName, secret) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEmail(String userName, String newEmail) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        if (!StringUtils.hasText(newEmail)) {
            throw new ParameterException("新邮箱地址不能为空");
        }
        return userRepository.updateEmail(userName, newEmail) > 0;
    }

    @Override
    public Boolean updatePhone(String userName, String newPhone) {
        if (!StringUtils.hasText(userName)) {
            throw new ParameterException("用户名不能为空");
        }
        if (!StringUtils.hasText(newPhone)) {
            throw new ParameterException("新联系方式不能为空");
        }
        return userRepository.updatePhone(userName, newPhone) > 0;
    }

    @Override
    public Boolean updateUserInfo(UserPartVo userPartVo) {
        User user = findById(userPartVo.getUserId());
        user.setNickName(userPartVo.getNickName());
        user.setUserPhone(userPartVo.getUserPhone());
        user.setUserSex(userPartVo.getUserSex());
        User update = update(user);
        return !Objects.equals(update, null);
    }

    @Override
    public List<UserDto> getUsersByDept(String deptId, Pageable pageable) {
        if (!StringUtils.hasText(deptId)) {
            throw new ParameterException("部门编号不能为空");
        }
        List<User> users = userRepository.findByUserJob_JobDept_DeptId(deptId, pageable);
        List<UserDto> userDtos =
                users.stream()
                        .map(user -> UserTransferUtils.userToDto(user))
                        .collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public List<UserDto> getUsersByKeyWord(String keyWord, Pageable pageable) {
        if (!StringUtils.hasText(keyWord)) {
            throw new ParameterException("关键词不能为空");
        }
        List<User> users = userRepository.findByUserNameAndNickName(keyWord, pageable);
        List<UserDto> userDtos =
                users.stream()
                        .map(user -> UserTransferUtils.userToDto(user))
                        .collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public List<UserDto> getUsersByState(User.UserState userState, Pageable pageable) {
        if (Objects.equals(userState, null)) {
            throw new ParameterException("用户状态不能为空");
        }
        List<User> users = userRepository.findByUserState(userState, pageable);
        List<UserDto> userDtos =
                users.stream()
                        .map(user -> UserTransferUtils.userToDto(user))
                        .collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public File createExcelFile(Pageable pageable) {
        // 根据分页信息获取数据
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> users = userPage.getContent();
        if (Objects.equals(users, null)) {
            users = new ArrayList<>();
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 创建头信息
        Row rowInfo = sheet.createRow(0);
        int columnIndex = 0;
        rowInfo.createCell(columnIndex).setCellValue("No");
        rowInfo.createCell(++columnIndex).setCellValue("用户编号");
        rowInfo.createCell(++columnIndex).setCellValue("用户名");
        rowInfo.createCell(++columnIndex).setCellValue("用户头像");
        rowInfo.createCell(++columnIndex).setCellValue("用户昵称");
        rowInfo.createCell(++columnIndex).setCellValue("登录密码");
        rowInfo.createCell(++columnIndex).setCellValue("用户状态");
        rowInfo.createCell(++columnIndex).setCellValue("用户联系方式");
        rowInfo.createCell(++columnIndex).setCellValue("用户性别");
        rowInfo.createCell(++columnIndex).setCellValue("用户邮箱");
        rowInfo.createCell(++columnIndex).setCellValue("用户创建时间");
        rowInfo.createCell(++columnIndex).setCellValue("用户所属部门");
        rowInfo.createCell(++columnIndex).setCellValue("用户岗位");
        rowInfo.createCell(++columnIndex).setCellValue("用户角色");

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columnIndex + 1; j++) {
                row.createCell(j);
            }
            columnIndex = 0;
            row.getCell(columnIndex).setCellValue(i + 1);
            row.getCell(++columnIndex).setCellValue(user.getUserId());
            row.getCell(++columnIndex).setCellValue(user.getUserName());
            row.getCell(++columnIndex).setCellValue(user.getUserAvatar());
            row.getCell(++columnIndex).setCellValue(user.getNickName());
            row.getCell(++columnIndex).setCellValue("*********");
            row.getCell(++columnIndex).setCellValue(user.getUserState().getValue());
            row.getCell(++columnIndex).setCellValue(user.getUserPhone());
            row.getCell(++columnIndex).setCellValue(user.getUserSex().getValue());
            row.getCell(++columnIndex).setCellValue(user.getUserEmail());
            row.getCell(++columnIndex).setCellValue(DateTimeUtils.getDateTime(user.getUserCreateTime()));
            // FIXME 可能会出问题
            row.getCell(++columnIndex).setCellValue(user.getUserJob().getJobDept().getDeptName());
            row.getCell(++columnIndex).setCellValue(user.getUserJob().getJobName());
            // 用户角色
            String roleStr = user.getUserRoles().stream().map(Role::getRoleName).collect(Collectors.joining(", ", "[ ", " ]"));
            row.getCell(++columnIndex).setCellValue(roleStr);
        }
        return FileUtils.createExcelFile(workbook, "erp_users");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User save(User entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("用户不能为空");
        }
        User user = userRepository.findByUserName(entity.getUserName()).orElse(null);
        if (Objects.equals(user, null)) {
            return userRepository.save(entity);
        } else {
            throw new EntityExistException("用户已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<User> entities) {
        if (entities.isEmpty() || Objects.equals(entities, null)) {
            throw new ParameterException("用户集合不能为空");
        }
        userRepository.deleteAll(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User update(User entity) {
        if (Objects.equals(entity, null)) {
            throw new ParameterException("用户不能为空");
        }
        return userRepository.saveAndFlush(entity);
    }

    @Override
    public User findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new ParameterException("用户编号不能为空");
        }
        return userRepository.findById(id).orElseThrow(() -> new EntityNotExistException("用户不存在"));
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
