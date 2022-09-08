package com.beiran;

import com.beiran.core.system.entity.*;
import com.beiran.core.system.repository.*;
import com.beiran.core.system.service.UserService;
import com.beiran.core.system.vo.UserVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class ErpServerApplicationTests {

	@Autowired
	UserService userService;

	@Autowired
	PermissionRepository permissionRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	DeptRepository deptRepository;

	@Autowired
	JobRepository jobRepository;

	@Test
	void contextLoads() {
	}

	@Test
	public void addPermissionData() {
		Permission permission1 = new Permission();
		permission1.setPermissionName("system:dept:add");
		permission1.setPermissionDesc("新增部门");

		Permission permission2 = new Permission();
		permission2.setPermissionName("system:dept:edit");
		permission2.setPermissionDesc("修改部门");

		Permission permission3 = new Permission();
		permission3.setPermissionName("system:dept:del");
		permission3.setPermissionDesc("删除部门");

		Permission permission4 = new Permission();
		permission4.setPermissionName("system:dept:view");
		permission4.setPermissionDesc("查询部门");

		Permission permission5 = new Permission();
		permission5.setPermissionName("system:dept:export");
		permission5.setPermissionDesc("导出部门");

		permissionRepository.save(permission1);
		permissionRepository.save(permission2);
		permissionRepository.save(permission3);
		permissionRepository.save(permission4);
		permissionRepository.save(permission5);
	}

	@Test
	public void addRoleData() {
		List<Permission> permissions = permissionRepository.findAll();
		Role admin = new Role();
		admin.setRoleName("admin");
		admin.setRoleDesc("系统管理员");
		admin.setRolePermissions(new HashSet<>(permissions));

		roleRepository.save(admin);
	}

	@Test
	public void addUserData() {
		Dept dept = new Dept();
		dept.setDeptName("开发部");
		dept.setDeptState(Dept.DeptState.ACTIVE);

		deptRepository.save(dept);

		Job job = new Job();
		job.setJobDept(dept);
		job.setJobName("开发者");
		job.setJobState(Job.JobState.ACTIVE);

		Job jobSave = jobRepository.save(job);

		UserVo userVo = new UserVo();
		userVo.setUserName("beiran");
		userVo.setUserPassword("123456");
		userVo.setNickName("北然");
		userVo.setUserSex(User.UserSex.MALE);
		userVo.setUserState(User.UserState.ACTIVE);
		userVo.setJob(jobSave.getJobId());

		userVo.setUserPhone("18888888888");
		userVo.setUserEmail("beiranlp@gmail.com");
		Set<String> roles = new HashSet<>();
		Role role = roleRepository.findByRoleName("admin").orElse(null);
		roles.add(Objects.requireNonNull(role).getRoleId());
		userVo.setRoles(roles);

		userService.createUser(userVo);
	}
}
