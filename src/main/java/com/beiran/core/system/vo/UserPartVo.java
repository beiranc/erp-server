package com.beiran.core.system.vo;

import com.beiran.core.system.entity.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 个人中心修改用户资料时所需字段
 */

@Data
public class UserPartVo {

    /**
     * 用户编号
     */
    @NotBlank(message = "用户编号不能为空")
    private String userId;

    /**
     * 用户昵称
     */
    @NotBlank(message = "用户昵称不能为空")
    private String nickName;

    /**
     * 用户联系方式
     */
    @NotBlank(message = "用户联系方式不能为空")
    private String userPhone;

    /**
     * 用户性别
     */
    @NotNull(message = "用户性别不能为空")
    private User.UserSex userSex;
}
