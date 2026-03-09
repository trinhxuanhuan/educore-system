package com.stuman.grade_service.dto.response;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {

    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}
