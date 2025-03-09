package lt.techin.dto;

import lt.techin.model.User;

import java.util.List;

public class UserResponseMapper {
    public static UserResponseDTO toUserResponseDTO(User user) {
        return new UserResponseDTO(user.getUsername(), RoleMapper.toRoleDTOList(user));
    }

    public static List<UserResponseDTO> toUserResponseDTOList(List<User> users) {
        return users.stream()
                .map(user -> new UserResponseDTO(user.getUsername(), RoleMapper.toRoleDTOList(user)))
                .toList();
    }
}
