package com.example.prac.service.auth;


import com.example.prac.model.authEntity.AdminRequest;
import com.example.prac.model.authEntity.Role;
import com.example.prac.model.authEntity.User;
import com.example.prac.repository.auth.AdminRequestRepository;
import com.example.prac.repository.auth.UserRepository;
import com.example.prac.webSocket.AdminWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRequestService {

    private final AdminRequestRepository adminRequestRepository;
    private final AdminWebSocketHandler adminWebSocketHandler;
    private final UserRepository userRepository;

    public boolean createAdminRequest(AdminRequest adminRequest) {
        try {
            if (this.getAdminRequestById(adminRequest.getId()) == null) {
                adminRequestRepository.save(adminRequest);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    public Optional<AdminRequest> findByRequester(User requester) {
        return adminRequestRepository.findByRequester(requester);
    }

    public AdminRequest getAdminRequestById(Long id) {
        return adminRequestRepository.findById(id);
    }

    public void updateAdminRequest(AdminRequest adminRequest) {
        adminRequestRepository.update(adminRequest);
    }

    public List<User> getAllAdmins() {
        return userRepository.findAllAdmins();
    }

    public boolean isRequestApprovedByAll(Long id) {
        AdminRequest adminRequest = getAdminRequestById(id);
        List<User> allAdmins = getAllAdmins();
        return adminRequest.getApprovedBy().containsAll(allAdmins);
    }

    @Transactional(readOnly = true)
    public List<AdminRequest> getAllAdminRequests() {
        return adminRequestRepository.findAll();
    }

    public boolean approveRequest(Long requestId, User currentUser) throws Exception {
        AdminRequest adminRequest = adminRequestRepository.findById(requestId);

        if (adminRequest == null) {
            return false;
        }
        if (!adminRequest.getApprovedBy().stream().map(User::getUsername).toList().contains(currentUser.getUsername())) {
            adminRequest.getApprovedBy().add(currentUser);
            List<User> allAdmins = userRepository.findAllAdmins();

            if (adminRequest.getApprovedBy().stream().map(User::getUsername).toList().containsAll(allAdmins.stream().map(User::getUsername).toList())) {
                adminRequest.setApprovedByAll(true);
                User userToApprove = adminRequest.getRequester();
                promoteUserToAdmin(userToApprove);

                adminWebSocketHandler.sendNotificationToUser(userToApprove.getUsername(), "Your admin request has been approved!");
            }
            this.updateAdminRequest(adminRequest);
            return true;
        }

        return false;
    }

    public void promoteUserToAdmin(User user) {
        user.setRole(Role.ADMIN);
        userRepository.update(user);
    }
}
