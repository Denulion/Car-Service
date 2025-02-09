package lt.techin.dto;

import lt.techin.model.Role;
import lt.techin.model.User;

import java.util.List;

public class RoleMapper {

    public static List<RoleDTO> toRoleDTOList(User user) {
        return user.getRoles().stream()
                .map(RoleMapper::toRoleDTO)
                .toList();
    }

    private static RoleDTO toRoleDTO(Role role) {
        return new RoleDTO(role.getId()); // role.getName()
    }

    public static List<Role> toRoleListFromDTO(List<RoleDTO> roleDTOList) {
        return roleDTOList.stream().map(RoleMapper::toRole).toList();
    }

    private static Role toRole(RoleDTO roleDTO) {
        Role role = new Role();

        role.setId(roleDTO.id());
//        role.setName(roleDTO.name());

        return role;
    }
}
