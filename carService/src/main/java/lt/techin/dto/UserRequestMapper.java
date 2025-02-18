package lt.techin.dto;

import lt.techin.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRequestMapper {
    public static List<UserRequestDTO> toUserDTOList(List<User> userList) {
        return userList.stream()
                .map(user -> new UserRequestDTO(user.getUsername(), user.getPassword(), RoleMapper.toRoleDTOList(user)))
                .toList();
    }

    public static User toUser(UserRequestDTO userRequestDTO) {
        User user = new User();

        user.setUsername(userRequestDTO.username());
        user.setPassword(userRequestDTO.password());
        user.setRoles(new ArrayList<>(RoleMapper.toRoleListFromDTO(userRequestDTO.roles())));

        return user;
    }

    public static UserRequestDTO toUserDTO(User user) {
        return new UserRequestDTO(user.getUsername(), user.getPassword(), RoleMapper.toRoleDTOList(user));
    }

    public static void updateUserFromDTO(User user, UserRequestDTO userRequestDTO) {
        user.setUsername(userRequestDTO.username());
        user.setPassword(userRequestDTO.password());
        user.setRoles(new ArrayList<>(RoleMapper.toRoleListFromDTO(userRequestDTO.roles())));
    }
}
