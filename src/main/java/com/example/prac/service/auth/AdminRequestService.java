package com.example.prac.service.auth;


import com.example.prac.model.authEntity.AdminRequest;
import com.example.prac.model.authEntity.Role;
import com.example.prac.model.authEntity.User;
import com.example.prac.repository.auth.AdminRequestRepository;
import com.example.prac.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRequestService {

    private final AdminRequestRepository adminRequestRepository;
    private final UserRepository userRepository;

    // Сохранение заявки
    public boolean createAdminRequest(AdminRequest adminRequest) {
        try {
            adminRequestRepository.save(adminRequest);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    // Получение заявки по id
    public AdminRequest getAdminRequestById(Long id) {
        return adminRequestRepository.findById(id);
    }

    // Обновление заявки
    public void updateAdminRequest(AdminRequest adminRequest) {
        adminRequestRepository.update(adminRequest);
    }

    // Получение всех администраторов
    public List<User> getAllAdmins() {
        return userRepository.findAllAdmins();
    }

    // Проверка, все ли администраторы одобрили заявку
    public boolean isRequestApprovedByAll(Long id) {
        AdminRequest adminRequest = getAdminRequestById(id);
        List<User> allAdmins = getAllAdmins();
        return adminRequest.getApprovedBy().containsAll(allAdmins);
    }
    @Transactional(readOnly = true)
    public List<AdminRequest> getAllAdminRequests(){
        return adminRequestRepository.findAll();
    }
    public boolean approveRequest(Long requestId, User currentUser) {
        // Находим запрос на админку по ID
        AdminRequest adminRequest = adminRequestRepository.findById(requestId);

        if (adminRequest == null) {
            return false;
        }

        // Проверяем, является ли текущий пользователь администратором и еще не одобрил запрос
        if (!adminRequest.getApprovedBy().stream().map(User::getUsername).toList().contains(currentUser.getUsername())) {
            // Добавляем текущего администратора в список тех, кто одобрил
            adminRequest.getApprovedBy().add(currentUser);

            // Проверяем, одобрили ли все администраторы
            List<User> allAdmins = userRepository.findAllAdmins();

            System.out.println(adminRequest.getApprovedBy().stream().map(User::getUsername).toList().containsAll(allAdmins.stream().map(User::getUsername).toList()));
            if (adminRequest.getApprovedBy().stream().map(User::getUsername).toList().containsAll(allAdmins.stream().map(User::getUsername).toList())) {
                System.out.println("ALL ADMINS APPROVED THE REQUEST");
                adminRequest.setApprovedByAll(true); // Устанавливаем флаг "одобрено всеми"
                User userToApprove = adminRequest.getRequester();
                promoteUserToAdmin(userToApprove);
                System.out.println("addminRequest: " + adminRequest);
            }

            // Сохраняем изменения в базе данных
            this.updateAdminRequest(adminRequest);
            return true;
        }

        return false; // Если запрос уже был одобрен текущим пользователем
    }


    // Обновление роли пользователя на ADMIN
    public void promoteUserToAdmin(User user) {
        user.setRole(Role.ADMIN);
        userRepository.update(user);
    }
}
