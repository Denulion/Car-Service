package lt.techin.dto;

import lt.techin.model.User;

public class UserPostResponseMapper {
    public static UserPostResponseDTO toUserPostResponseDTO(User user) {
        return new UserPostResponseDTO(user.getUsername(), RoleMapper.toRoleDTOList(user));
    }

}
