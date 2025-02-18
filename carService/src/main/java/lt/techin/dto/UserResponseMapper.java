package lt.techin.dto;

import lt.techin.model.User;

public class UserResponseMapper {
    public static UserResponseDTO toUserResponseDTO(User user) {
        return new UserResponseDTO(user.getUsername(), RoleMapper.toRoleDTOList(user));
    }

}
