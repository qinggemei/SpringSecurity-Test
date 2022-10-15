package com.star.entity.request;

import lombok.Data;
import lombok.NonNull;

/**
 * @author liuxing
 */
@Data
public class UserVO {

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String newPassword;
}
